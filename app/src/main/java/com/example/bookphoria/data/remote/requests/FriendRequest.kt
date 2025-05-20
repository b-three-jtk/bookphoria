package com.example.bookphoria.data.remote.requests

import com.google.gson.annotations.SerializedName

data class FriendRequest(
    @SerializedName("username") val userName: String
)
