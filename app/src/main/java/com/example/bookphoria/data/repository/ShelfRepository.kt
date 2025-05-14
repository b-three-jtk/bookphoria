package com.example.bookphoria.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.bookphoria.data.remote.api.ShelfApiServices
import com.google.android.gms.common.api.Response
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.sqrt

class ShelfRepository @Inject constructor(
    private val api: ShelfApiServices,
    @ApplicationContext private val context: Context
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
                    name = name.toRequestBody("text/plain".toMediaType()),
                    description = desc?.toRequestBody("text/plain".toMediaType()),
                    image = imagePart
                )
            } catch (e: IOException) {
                return Result.failure(Exception("Network error: ${e.message}"))
            }

            handleApiResponse(response, DEFAULT_MAX_SIZE_KB)
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    private fun Uri.compressImage(maxFileSizeKB: Int): File {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }

        context.contentResolver.openInputStream(this)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        } ?: throw Exception("Cannot read image")

        options.inSampleSize = calculateInSampleSize(
            options.outWidth,
            options.outHeight,
            MAX_IMAGE_DIMENSION
        )

        val bitmap = try {
            context.contentResolver.openInputStream(this)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            } ?: throw Exception("Failed to load image")
        } catch (e: OutOfMemoryError) {
            throw Exception("Not enough memory to process image")
        }

        val outputFile = createTempFile(context)
        var quality = MAX_COMPRESS_QUALITY
        var outputSize: Long

        return try {
            do {
                FileOutputStream(outputFile).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos)
                }
                outputSize = outputFile.length() / 1024

                if (outputSize <= maxFileSizeKB) break
                quality -= QUALITY_STEP
            } while (quality >= MIN_COMPRESS_QUALITY)

            if (outputSize > maxFileSizeKB) {
                throw Exception("Failed to compress below $maxFileSizeKB KB")
            }

            outputFile
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
}