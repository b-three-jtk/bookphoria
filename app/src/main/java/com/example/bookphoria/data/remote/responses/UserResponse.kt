package com.example.bookphoria.data.remote.responses

import com.example.bookphoria.data.local.entities.UserEntity
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("avatar") val profilePicture: String
)

data class EditProfileResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserEntity
)