package com.example.bookphoria.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.bookphoria.data.local.AppDatabase
import com.example.bookphoria.data.local.entities.ShelfEntity
import com.example.bookphoria.data.local.entities.ShelfWithBooks
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.ShelfApiServices
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class ShelfRepository @Inject constructor(
    private val api: ShelfApiServices,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
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
        imageUri: Uri?
    ): Result<Unit> {
        return try {
            // Get the token first
            val token = userPreferences.getAccessToken().first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Authentication required"))
            }

            val imagePart = imageUri?.let { uri ->
                try {
                    val compressedFile = uri.compressImage(DEFAULT_MAX_SIZE_KB)
                    Log.d("ImageUpload", "Image size: ${compressedFile.sizeInKB()}KB")
                    compressedFile.toMultipartPart()
                } catch (e: Exception) {
                    Log.e("ImageProcessing", "Error processing image", e)
                    return Result.failure(Exception("Failed to process image: ${e.message}"))
                }
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
        response: retrofit2.Response<*>,
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
        imageUri: Uri?,
        response: JsonObject?
    ) {
        try {
            val localImagePath = imageUri?.let { uri ->
                saveImageToInternalStorage(uri)
            }
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
                    imagePath = localImagePath
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

    fun getShelvesWithBooks(userId: Int, shelfId: Int): Flow<ShelfWithBooks?> {
        return database.ShelfDao().getShelvesWithBooks(userId, shelfId)
    }

    fun getAllShelvesWithBooks(userId: Int): Flow<List<ShelfWithBooks>> {
        return database.ShelfDao().getAllShelvesWithBooks(userId)
    }
}