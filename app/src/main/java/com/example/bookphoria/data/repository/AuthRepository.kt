package com.example.bookphoria.data.repository

import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.AuthApiService
import com.example.bookphoria.data.remote.api.LoginRequest
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

}