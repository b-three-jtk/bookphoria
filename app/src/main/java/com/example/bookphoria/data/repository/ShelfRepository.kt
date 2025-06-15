package com.example.bookphoria.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.bookphoria.data.local.AppDatabase
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.dao.ShelfDao
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.ShelfBookCrossRef
import com.example.bookphoria.data.local.entities.ShelfEntity
import com.example.bookphoria.data.local.entities.ShelfWithBooks
import com.example.bookphoria.data.local.entities.UserBookCrossRef
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.ShelfApiServices
import com.example.bookphoria.data.remote.responses.BookIdRequest
import com.example.bookphoria.data.remote.responses.ShelfDetailNetworkModel
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


class ShelfRepository @Inject constructor(
    private val api: ShelfApiServices,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val shelfDao: ShelfDao,
    private val bookDao: BookDao
) {
    suspend fun createShelf(
        name: String,
        desc: String?,
        imageUri: String?,
        imageFile: File?
    ) {
         try {
            val token = userPreferences.getAccessToken().first()
            val coverPart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            val response = api.createShelf(
                                token = "Bearer $token",
                                name = name.toRequestBody("text/plain".toMediaType()),
                                description = desc?.toRequestBody("text/plain".toMediaType()),
                                image = coverPart
                            )
             Log.d("ShelfRepository","Response: $response")
             if (response.message == "Shelf created successfully") {
                 saveToLocalDatabase(name, desc, response.data.image, response.data.id)
             } else {
                 throw Exception(response.message ?: "Gagal membuat shelf")
             }
         } catch (e: Exception) {
             Log.d("ShelfRepository", "Unexpected error: ${e.message}")
             throw Exception("Gagal membuat shelf: ${e.message}")
         }
    }

    private suspend fun saveToLocalDatabase(
        name: String,
        desc: String?,
        imageUri: String?,
        serverId: String
    ) {
        try {

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

            val response = try {
                api.updateShelf(
                    token = "Bearer $token",
                    shelfId = id,
                    name = name.toRequestBody("text/plain".toMediaType()),
                    description = desc?.toRequestBody("text/plain".toMediaType()),
                    image = coverPart
                )
            } catch (e: IOException) {
                return "Network error: ${e.message}"
            } catch (e: Exception) {
                return "API error: ${e.message}"
            }

            if (response.message == "Shelf updated successfully") {
                val shelfEntity = database.ShelfDao().getShelfById(id)
                shelfEntity?.let {
                    val updatedShelf = it.copy(
                        name = name,
                        description = desc,
                        imagePath = imageUri
                    )
                    shelfDao.update(
                        description = updatedShelf.description,
                        imagePath = updatedShelf.imagePath,
                        id = updatedShelf.id,
                        name = updatedShelf.name
                    )
                }
            }
        } catch (e: Exception) {
            return ("Unexpected error: ${e.message}")
        }
        return null
    }


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

    suspend fun deleteBookFromShelf(shelfId: Int, bookId: Int): Boolean {
        try {
            val accessToken = userPreferences.getAccessToken().first()
            if (accessToken.isNullOrEmpty()) {
                Log.e("ShelfRepository", "Access token is missing")
                throw IllegalStateException("Access token is missing")
            }

            val bookServerId = bookDao.getBookServerIdById(bookId)
                ?: throw IllegalStateException("Book server ID not found for bookId: $bookId")
            val shelfServerId = shelfDao.getShelfById(shelfId)
                ?: throw IllegalStateException("Shelf server ID not found for shelfId: $shelfId")

            val shelfResponse = api.deleteBookFromShelf(
                token = "Bearer $accessToken",
                shelfId = shelfServerId,
                bookId = bookServerId
            )

            Log.d("ShelfRepository", "Request: shelfId=$shelfId, bookId=$bookId, response=$shelfResponse, rawMessage=${shelfResponse.message}")

            // Check if message matches or handle null case
            if (shelfResponse.message == "Buku berhasil dihapus dari rak.") {
                shelfDao.removeBookFromShelf(ShelfBookCrossRef(shelfId = shelfId, bookId = bookId))
                return true
            } else {
                Log.e("ShelfRepository", "Failed to remove book: ${shelfResponse.message ?: "No message returned"}")
                throw Exception("Failed to remove book: ${shelfResponse.message ?: "No message returned"}")
            }
        } catch (e: IOException) {
            Log.e("ShelfRepository", "Network error removing book: ${e.message}")
            throw Exception("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("ShelfRepository", "Error removing book: ${e.message}")
            throw e
        }
    }

    fun getAllShelvesWithBooks(userId: Int): Flow<List<ShelfWithBooks>> {
        return database.ShelfDao().getAllShelvesWithBooks(userId)
    }

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

    suspend fun getShelfById(shelfId: String): ShelfDetailNetworkModel? {
        try {
            val token = userPreferences.getAccessToken().first()

            return api.getShefById(
                token = "Bearer $token",
                shelfId = shelfId
            )
        } catch (e: Exception) {
            Log.e("ShelfRepository", "Error getting shelf by ID: ${e.message}")
            return null
        }
    }

    suspend fun getBookById(bookId: String): UserBookCrossRef? {
        val localID = bookDao.getBookIdByServerId(bookId)
        val userId = userPreferences.getUserId().first() ?: throw IllegalStateException("User ID is null")
        return localID?.let { bookDao.getUserBookCrossRef(userId, it) }
    }
}