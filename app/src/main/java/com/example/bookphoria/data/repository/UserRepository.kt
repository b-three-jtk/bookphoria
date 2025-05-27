package com.example.bookphoria.data.repository

import android.util.Log
import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.BookWithAuthors
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.AuthApiService
import com.example.bookphoria.data.remote.responses.EditProfileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class UserRepository @Inject constructor (
    private val userDao: UserDao,
    private val apiService: AuthApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun getBooksWithAuthorsByUser(userId: Int): List<BookWithAuthors> {
        return userDao.getBooksWithAuthorsByUser(userId)
    }

    fun editProfile(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        avatar: File?
    ): Flow<Result<EditProfileResponse>> = flow {
        val accessToken = userPreferences.getAccessToken().first()
        Log.d("BookRepository", "Token: $accessToken")

        if (accessToken == null) {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }
        try {
            val usernamePart = username.toRequestBody("text/plain".toMediaTypeOrNull())
            val firstNamePart = firstName.toRequestBody("text/plain".toMediaTypeOrNull())
            val lastNamePart = lastName.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val avatarPart = avatar?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("avatar", it.name, requestFile)
            }

            val response = apiService.updateProfile(
                token = "Bearer $accessToken",
                username = usernamePart,
                firstName = firstNamePart,
                lastName = lastNamePart,
                email = emailPart,
                avatar = avatarPart
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    body.user.profilePicture?.let {
                        userDao.updateUser(body.user.id, username, firstName, lastName, email, it)
                    }
                    userPreferences.saveCredentials(email, null)
                    emit(Result.success(body))
                }

            } else {
                emit(Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error")))
            }

        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getProfile(): UserEntity? {
        val userId = userPreferences.getUserId().first()
        return userId?.let { userDao.getUserById(it) }
    }
}