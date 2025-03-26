package com.example.bookphoria.data.remote.responses

data class AuthResponse(
    val accessToken: String,
    val tokenType: String,
    val user: UserResponse
)
