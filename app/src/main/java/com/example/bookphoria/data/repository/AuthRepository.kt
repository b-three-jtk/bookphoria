package com.example.bookphoria.data.repository

import android.util.Log
import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.AuthApiService
import com.example.bookphoria.data.remote.api.ForgotPasswordRequest
import com.example.bookphoria.data.remote.api.FriendApiService
import com.example.bookphoria.data.remote.api.LoginRequest
import com.example.bookphoria.data.remote.api.RegisterRequest
import com.example.bookphoria.data.remote.api.ResetPasswordRequest
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.example.bookphoria.data.remote.api.UserStatsResponse
import com.google.android.gms.common.api.Response
import kotlinx.coroutines.flow.first
import javax.inject.Singleton
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val userApiService: FriendApiService,
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) {
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            Log.d("AuthRepo", "API User Data - id: ${response.user.id}, username: ${response.user.username}, email: ${response.user.email}")

            if (response.accessToken.isNullOrEmpty()) {
                return Result.failure(Exception("Access token dari server kosong atau null"))
            }
            Log.d("AuthRepository", "Saving token: ${response.accessToken}")

            userPreferences.saveLoginData(token = response.accessToken, userId = response.user.id, userName = response.user.username)

            val userEntity = UserEntity(
                id = response.user.id,
                username = response.user.username,
                email = response.user.email,
                firstName = response.user.firstName,
                lastName = response.user.lastName,
                profilePicture = response.user.profilePicture
            )
            userDao.insertUser(userEntity)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<Unit> {
        return try {
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                return Result.failure(Exception("Seluruh field harus terisi"))
            }

            val response = apiService.register(RegisterRequest(username, email, password))
            userPreferences.saveLoginData(response.accessToken, response.user.id, response.user.username)

            val userEntity = UserEntity(
                id = response.user.id,
                username = response.user.username,
                email = response.user.email
            )
            userDao.insertUser(userEntity)

            Result.success(Unit)
        } catch (e: Exception) {
            println("Error saat register: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(token: String, email: String, password: String, confirmPassword: String): Result<String> {
        return try {
            val response = apiService.resetPassword(
                ResetPasswordRequest(token, email, password, confirmPassword)
            )
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            userPreferences.clearLoginData()
            userDao.clearUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        val accessToken = userPreferences.getAccessToken().first() ?: return null

        val userLocal = userDao.getUserByUsername(username)
        if (userLocal != null) {
            return userLocal
        }

        val response = userApiService.getUserByUsername(
            token = "Bearer ${accessToken}",
            userName = username
        )
        Log.d("AuthRepository", "Received user: $response")
        if (response != null) {
            userDao.updateUser(response.id, response.username, response.firstName, response.lastName, response.email, response.profilePicture)
            return response
        } else {
            Log.e("AuthRepository", "User not found in response")
            return null
        }
    }

    suspend fun getCurrentUserId(): Int? {
        return userPreferences.getUserId().first()
    }

    suspend fun getCurrentUser(): Result<UserStatsResponse> {
        return try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful) {
                response.body()?.let { userStats ->
                    // Update local database
                    userDao.insertUser(
                        UserEntity(
                            id = userStats.id,
                            username = userStats.username,
                            email = userStats.email
                        )
                    )
                    Result.success(userStats)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}