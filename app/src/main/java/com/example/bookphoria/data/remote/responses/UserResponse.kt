package com.example.bookphoria.data.remote.responses

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val profilePicture: String? = null
)