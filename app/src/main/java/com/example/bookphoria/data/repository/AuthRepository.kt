package com.example.bookphoria.data.repository

import android.util.Log
import retrofit2.HttpException
import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.AuthApiService
import com.example.bookphoria.data.remote.api.ForgotPasswordRequest
import com.example.bookphoria.data.remote.api.LoginRequest
import com.example.bookphoria.data.remote.api.RegisterRequest
import com.example.bookphoria.data.remote.api.ResetPasswordRequest
import com.google.gson.Gson
import okhttp3.ResponseBody
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

            if (response.accessToken.isNullOrEmpty()) {
                return Result.failure(Exception("Access token dari server kosong atau null"))
            }
            Log.d("AuthRepository", "Saving token: ${response.accessToken}")

            userPreferences.saveLoginData(token = response.accessToken, userId = response.user.id)

            val userEntity = UserEntity(
                id = response.user.id,
                username = response.user.username,
                email = response.user.email
            )
            userDao.insertUser(userEntity)

            Result.success(Unit)
        } catch (e: HttpException) {
            val errorMessages = parseErrorMessage(e.response()?.errorBody())
            Result.failure(Exception(errorMessages))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan"))
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<Unit> {
        return try {
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                return Result.failure(Exception("Seluruh field harus terisi"))
            }

            val response = apiService.register(RegisterRequest(username, email, password))
            userPreferences.saveLoginData(response.accessToken, response.user.id)

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

    suspend fun getUserNameById(userId: Int): String {
        return userDao.getUserById(userId)?.username ?: "Reader"
    }

    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        if (errorBody == null) return "Terjadi kesalahan."

        val raw = try {
            errorBody.string()
        } catch (e: Exception) {
            return "Terjadi kesalahan membaca respons."
        }

        return try {
            val gson = Gson()
            val errorResponse = gson.fromJson(raw, LaravelErrorResponse::class.java)

            val rawMessage = errorResponse.errors?.values?.firstOrNull()?.firstOrNull()
                ?: errorResponse.message
                ?: "Terjadi kesalahan."

            translateErrorMessage(rawMessage)
        } catch (e: Exception) {
            Log.e("ParseError", "JSON parse failed: ${e.message}")
            raw.removeSurrounding("\"")
        }
    }


    private fun translateErrorMessage(message: String): String {
        val translations = mapOf(
            "Invalid credentials" to "Email atau password salah.",
            "The given data was invalid." to "Data yang diberikan tidak valid.",
            "The email field is required." to "Email wajib diisi.",
            "The password field is required." to "Password wajib diisi.",
            "The password must be at least 8 characters." to "Password minimal 8 karakter.",
            "The email must be a valid email address." to "Format email tidak valid."
        )
        return translations[message] ?: message // fallback ke original kalau gak ada terjemahan
    }


    data class LaravelErrorResponse(
        val message: String?,
        val errors: Map<String, List<String>>?
    )
}