package com.example.bookphoria.data.remote.api

import com.example.bookphoria.data.remote.responses.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}

data class LoginRequest(
    val email: String,
    val password: String
)