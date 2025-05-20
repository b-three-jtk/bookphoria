package com.example.bookphoria.data.remote.responses

import com.google.gson.annotations.SerializedName

data class FriendRequestResponse(
    @SerializedName("message") val message: String
)
