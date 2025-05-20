package com.example.bookphoria.data.remote.api

import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.remote.responses.AuthResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): MessageResponse

    @POST("reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): MessageResponse
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val token: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class MessageResponse(
    val message: String
)