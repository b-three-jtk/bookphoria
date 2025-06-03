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
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.Result.Companion.failure

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val userApiService: FriendApiService,
    private val userDao: UserDao,
    private val userPreferences: UserPreferences,
) {
    suspend fun login(email: String, password: String): UserEntity? {
        return try {
            val response = apiService.login(LoginRequest(email, password))

            userPreferences.saveLoginData(token = response.accessToken, userId = response.user.id, userName = response.user.username)

            val user = userDao.getUserById(response.user.id)
            if (user == null) {
                val userEntity = UserEntity(
                    id = response.user.id,
                    username = response.user.username,
                    email = response.user.email,
                    firstName = response.user.firstName,
                    lastName = response.user.lastName,
                    profilePicture = response.user.profilePicture
                )
                userDao.insertUser(userEntity)

                return userEntity
            } else {
                val username = user.username
                val res = username?.let {
                    userApiService.getUserByUsername(
                        token = "Bearer ${response.accessToken}",
                        userName = it
                    )
                }
                Log.d("AuthRepository", "Received user: $res")
                if (res != null) {
                    userDao.updateUser(res.user.id, res.user.username, res.user.firstName, res.user.lastName, res.user.email, res.user.profilePicture)
                }
            }

            return user
        } catch (e: Exception) {
            Log.d("AuthRepository", "Error during login: ${e.message}")
            return null
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<Unit> {
        return try {
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                return failure(Exception("Seluruh field harus terisi"))
            }

            val response = apiService.register(RegisterRequest(username, email, password))
            userPreferences.saveLoginData(token = response.accessToken, userId = response.user.id, userName = response.user.username)
            Log.d("AuthRepository", "Received user: $response")

            val userEntity = UserEntity(
                id = response.user.id,
                username = response.user.username,
                email = response.user.email
            )
            userDao.insertUser(userEntity)

            Result.success(Unit)
        } catch (e: Exception) {
            println("Error saat register: ${e.message}")
            failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            Result.success(response.message)
        } catch (e: Exception) {
            failure(e)
        }
    }

    suspend fun resetPassword(token: String, email: String, password: String, confirmPassword: String): Result<String> {
        return try {
            val response = apiService.resetPassword(
                ResetPasswordRequest(token, email, password, confirmPassword)
            )
            Result.success(response.message)
        } catch (e: Exception) {
            failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            userPreferences.clearLoginData()
            userDao.clearUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            failure(e)
        }
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getCurrentUserId(): Int? {
        return userPreferences.getUserId().first()
    }
}