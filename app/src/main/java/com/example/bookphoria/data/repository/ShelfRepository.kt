package com.example.bookphoria.data.repository

import android.content.Context
import android.net.Uri
import com.example.bookphoria.data.remote.api.ShelfApiServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ShelfRepository @Inject constructor(
    private val api: ShelfApiServices,
    private val context: Context
) {
    suspend fun createShelf(
        name: String,
        description: String?,
        imageUri: Uri?
    ): Result<Unit> {
        return try {
            val imagePart = imageUri?.let { uri ->
                try {
                    uri.toMultipartBodyPart()
                } catch (e: Exception) {
                    return Result.failure(Exception("Gagal memproses gambar: ${e.message}"))
                }
            }

            val response = api.createShelf(
                name = name.toRequestBody("text/plain".toMediaType()),
                description = description?.toRequestBody("text/plain".toMediaType()),
                image = imagePart
            )

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Server error: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    private fun Uri.toMultipartBodyPart(): MultipartBody.Part {
        return try {
            val inputStream = context.contentResolver.openInputStream(this)
                ?: throw Exception("Tidak bisa membuka file")

            val tempFile = File.createTempFile(
                "shelf_img_${System.currentTimeMillis()}",
                ".jpg",
                context.cacheDir
            ).apply {
                outputStream().use { output ->
                    inputStream.copyTo(output)
                }
            }

            MultipartBody.Part.createFormData(
                "image",
                tempFile.name,
                tempFile.asRequestBody("image/*".toMediaType())
            )
        } catch (e: Exception) {
            throw Exception("Gagal mengkonversi gambar: ${e.message}")
        }
    }
}