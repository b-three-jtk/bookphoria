package com.example.bookphoria.data.repository

import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.AuthApiService
import com.example.bookphoria.data.remote.api.ForgotPasswordRequest
import com.example.bookphoria.data.remote.api.LoginRequest
import com.example.bookphoria.data.remote.api.RegisterRequest
import com.example.bookphoria.data.remote.api.ResetPasswordRequest
import com.example.bookphoria.data.remote.responses.AuthResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) {
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))

            userPreferences.saveAccessToken(response.accessToken)

            val userEntity = UserEntity(
                id = response.user.id,
                name = response.user.name,
                email = response.user.email
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
            userPreferences.saveAccessToken(response.accessToken)

            val userEntity = UserEntity(
                id = response.user.id,
                name = response.user.name,
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
            userPreferences.clearAccessToken()
            userDao.clearUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}