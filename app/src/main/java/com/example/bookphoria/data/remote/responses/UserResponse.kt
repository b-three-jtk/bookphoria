package com.example.bookphoria.data.remote.responses

import com.google.gson.annotations.SerializedName

data class UserResponse(
//    val profilePicture: String? = null,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)