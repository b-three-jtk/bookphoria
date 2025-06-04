package com.example.bookphoria.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.bookphoria.data.local.AppDatabase
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.dao.ShelfDao
import com.example.bookphoria.data.local.entities.ShelfEntity
import com.example.bookphoria.data.local.entities.ShelfWithBooks
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.ShelfApiServices
import com.example.bookphoria.data.remote.responses.BookIdRequest
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import retrofit2.Response


class ShelfRepository @Inject constructor(
    private val api: ShelfApiServices,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val shelfDao: ShelfDao,
    private val bookDao: BookDao
) {
    companion object {
        private const val MAX_COMPRESS_QUALITY = 90
        private const val MIN_COMPRESS_QUALITY = 30
        private const val QUALITY_STEP = 15
        private const val DEFAULT_MAX_SIZE_KB = 300
        private const val MAX_IMAGE_DIMENSION = 2048
    }

    suspend fun createShelf(
        name: String,
        desc: String?,
        imageFile: File?,
        imageUri: String?
    ): Result<Unit> {
        return try {
            // Get the token first
            val token = userPreferences.getAccessToken().first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Authentication required"))
            }

            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("cover", it.name, requestFile)
            }

            val response = try {
                api.createShelf(
                    token = "Bearer $token",  // Add Bearer prefix
                    name = name.toRequestBody("text/plain".toMediaType()),
                    description = desc?.toRequestBody("text/plain".toMediaType()),
                    image = imagePart
                )
            } catch (e: IOException) {
                return Result.failure(Exception("Network error: ${e.message}"))
            } catch (e: Exception) {
                return Result.failure(Exception("API error: ${e.message}"))
            }

            if (response.isSuccessful) {
                // Simpan ke database lokal
                saveToLocalDatabase(name, desc, imageUri, response.body())
            }

            handleApiResponse(response, DEFAULT_MAX_SIZE_KB)
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }


    suspend fun updateShelf(
        name: String,
        desc: String?,
        imageUri: String?,
        imageFile: File?,
        id: String
    ): String? {
        try {
            val token = userPreferences.getAccessToken().first()
            if (token.isNullOrEmpty()) {
                return "Token tidak ditemukan silahkan login ulang"
            }
            val coverPart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("cover", it.name, requestFile)
            }

            val shelf = database.ShelfDao().getShelfById(id)

            val response = try {
                shelf?.serverId?.let {
                    api.updateShelf(
                        token = "Bearer $token",
                        shelfId = it,
                        name = name.toRequestBody("text/plain".toMediaType()),
                        description = desc?.toRequestBody("text/plain".toMediaType()),
                        image = coverPart
                    )
                }
            } catch (e: IOException) {
                return "Network error: ${e.message}"
            } catch (e: Exception) {
                return "API error: ${e.message}"
            }

            if (response != null) {
                if (response.message == "Shelf updated successfully") {
                    val shelfEntity = database.ShelfDao().getShelfById(id)
                    shelfEntity?.let {
                        val updatedShelf = it.copy(
                            name = name,
                            description = desc,
                            imagePath = imageUri
                        )
                        database.ShelfDao().update(
                            description = updatedShelf.description,
                            imagePath = updatedShelf.imagePath,
                            id = updatedShelf.id,
                            name = updatedShelf.name
                        )
                    }
                }
            }
        } catch (e: Exception) {
            return ("Unexpected error: ${e.message}")
        }
        return null
    }

    private fun Uri.compressImage(maxFileSizeKB: Int): File {
        // First verify we can open the URI
        val inputStream = try {
            context.contentResolver.openInputStream(this)
                ?: throw Exception("Tidak dapat membuka file gambar")
        } catch (e: SecurityException) {
            throw Exception("Izin akses gambar ditolak")
        } catch (e: FileNotFoundException) {
            throw Exception("File gambar tidak ditemukan")
        }

        // Read image dimensions
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        try {
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
        } catch (e: Exception) {
            inputStream.close()
            throw Exception("Format gambar tidak didukung")
        }

        // Verify we got valid dimensions
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            throw Exception("File bukan gambar yang valid")
        }

        // Calculate sampling
        options.inSampleSize = calculateInSampleSize(
            options.outWidth,
            options.outHeight,
            MAX_IMAGE_DIMENSION
        )
        options.inJustDecodeBounds = false

        // Load the bitmap with sampling
        val bitmap = try {
            val sampledInputStream = context.contentResolver.openInputStream(this)
                ?: throw Exception("Tidak dapat membaca ulang gambar")

            try {
                BitmapFactory.decodeStream(sampledInputStream, null, options)?.also {
                    sampledInputStream.close()
                } ?: throw Exception("Gagal memproses gambar")
            } catch (e: Exception) {
                sampledInputStream.close()
                throw e
            }
        } catch (e: OutOfMemoryError) {
            throw Exception("Gambar terlalu besar")
        } catch (e: Exception) {
            throw Exception("Gagal memuat gambar: ${e.message ?: "Unknown error"}")
        }

        // Prepare output file
        val outputFile = createTempFile(context)
        var quality = MAX_COMPRESS_QUALITY
        var outputSize: Long

        return try {
            do {
                FileOutputStream(outputFile).use { fos ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos)) {
                        throw Exception("Gagal mengkompres gambar")
                    }
                }
                outputSize = outputFile.length() / 1024

                if (outputSize <= maxFileSizeKB) break
                quality -= QUALITY_STEP
            } while (quality >= MIN_COMPRESS_QUALITY)

            if (outputSize > maxFileSizeKB) {
                throw Exception("Gambar terlalu besar setelah kompresi")
            }

            outputFile
        } catch (e: Exception) {
            outputFile.delete() // Clean up
            throw e
        } finally {
            bitmap.recycle()
        }
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqSize: Int
    ): Int {
        var inSampleSize = 1
        while (width / inSampleSize > reqSize || height / inSampleSize > reqSize) {
            inSampleSize *= 2
        }
        return inSampleSize
    }

    private fun File.toMultipartPart(): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            "image",
            this.name,
            this.asRequestBody("image/*".toMediaType())
        )
    }

    private fun createTempFile(context: Context): File {
        return File.createTempFile(
            "img_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        ).apply { deleteOnExit() }
    }

    private fun handleApiResponse(
        response: Response<*>,
        maxSize: Int
    ): Result<Unit> {
        return when {
            response.isSuccessful -> Result.success(Unit)
            response.code() == 413 -> Result.failure(
                Exception("Image size exceeds limit of $maxSize KB")
            )
            else -> {
                val errorMsg = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: IOException) {
                    "Failed to read error response"
                }
                Result.failure(Exception("Server error (${response.code()}): $errorMsg"))
            }
        }
    }

    private fun File.sizeInKB() = length() / 1024

    private suspend fun saveToLocalDatabase(
        name: String,
        desc: String?,
        imageUri: String?,
        response: JsonObject?
    ) {
        try {
            val serverId = response
                ?.getAsJsonObject("data")
                ?.get("id")
                ?.asString

            val userId = userPreferences.getUserId().first() ?: throw IllegalStateException("User ID is null")

            database.ShelfDao().insert(
                ShelfEntity(
                    userId = userId,
                    serverId = serverId,
                    name = name,
                    description = desc,
                    imagePath = imageUri
                )
            )
        } catch (e: Exception) {
            Log.e("LocalSave", "Failed to save shelf locally", e)
            throw e
        }
    }

    private suspend fun saveImageToInternalStorage(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot open image stream")

            val directory = File(context.filesDir, "shelf_images")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val fileName = "shelf_${System.currentTimeMillis()}.jpg"
            val file = File(directory, fileName)

            try {
                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }
                file.absolutePath
            } catch (e: Exception) {
                throw Exception("Failed to save image: ${e.message}")
            } finally {
                inputStream.close()
            }
        }
    }

//    suspend fun addBookToShelf(token: String, shelfId: String, bookId: String): Boolean {
//        return try {
//            val bookData = JsonObject().apply {
//                addProperty("book_id", bookId)
//            }
//            val response = api.addBookToShelf("Bearer $token", shelfId, bookData)
//            response.isSuccessful
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }

    suspend fun addBookToShelf(shelfId: Int, bookId: Int): Boolean {
        return try {
            val accessToken = userPreferences.getAccessToken().first()
            Log.d("ShelfRepository", "Token: $accessToken")
            if (accessToken.isNullOrEmpty()) {
                Log.e("ShelfRepository", "Access token is missing")
                return false
            }

            val bookLocalId = bookDao.getBookServerIdById(bookId)
            val shelfLocalId = shelfDao.getShelfById(shelfId)

            val response = shelfLocalId?.let {
                api.addBookToShelf(
                    token = "Bearer $accessToken",
                    it,
                    bookId = BookIdRequest(bookId = bookLocalId)
                )
            }

            Log.d("ShelfRepository", "Request: $shelfId $bookId")

            if (response?.message == "Buku berhasil ditambahkan ke rak.") {
                shelfDao.addBookToShelf(shelfId, bookId)
                true
            } else {
                if (response != null) {
                    Log.e("ShelfRepository", "Failed to add book: ${response.message}")
                }
                false
            }
        } catch (e: IOException) {
            Log.e("ShelfRepository", "Network error adding book: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("ShelfRepository", "Error adding book: ${e.message}")
            false
        }
    }

    fun getShelvesWithBooks(userId: Int, shelfId: Int): Flow<ShelfWithBooks?> {
        return database.ShelfDao().getShelvesWithBooks(userId, shelfId)
    }

    fun getAllShelvesWithBooks(userId: Int): Flow<List<ShelfWithBooks>> {
        return database.ShelfDao().getAllShelvesWithBooks(userId)
    }

//    suspend fun deleteShelf(token: String, shelfId: String): Response<JsonObject> {
//        return api.deleteShelf("Bearer $token", shelfId)
//    }
    suspend fun deleteShelf(shelfId: Int): Boolean {
        return try {
            val accessToken = withContext(Dispatchers.IO) {
                userPreferences.getAccessToken().first()
            }
            Log.d("ShelfRepository", "Token: $accessToken")
            if (accessToken.isNullOrEmpty()) {
                Log.e("ShelfRepository", "Access token is missing")
                return false
            }

            val authHeader = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
            Log.d("ShelfRepository", "Auth Header: $authHeader")

            val shelfStrId = shelfDao.getShelfById(shelfId)

            val response = shelfStrId?.let { api.deleteShelf(authHeader, it) }
            if (response?.message == "Shelf deleted successfully.") {
                shelfDao.deleteShelf(shelfId)
                true
            } else {
                if (response != null) {
                    Log.e("ShelfRepository", "Failed to delete shelf: ${response}")
                }
                false
            }
        } catch (e: IOException) {
            Log.e("ShelfRepository", "Network error deleting shelf: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("ShelfRepository", "Error deleting shelf: ${e.message}")
            false
        }
    }
}