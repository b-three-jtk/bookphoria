package com.example.bookphoria.data.remote.responses

import com.example.bookphoria.data.local.entities.UserEntity
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String
)

data class EditProfileResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserEntity
)