package com.example.bookphoria.data.remote.responses

import com.google.gson.annotations.SerializedName

data class FriendRequestResponse(
    @SerializedName("message") val message: String
)

data class DetailFriendResponse(
    @SerializedName("username") val username: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("joined_at") val joinedAt: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("books") val books: List<BookStatusNetworkModel>,
    @SerializedName("friends") val friends: List<UserNetworkModel>,
    @SerializedName("shelves") val shelves: List<ShelfDetailNetworkModel>,
//    @SerializedName("borrow") val borrow: List<BorrowNetworkModel>,
    @SerializedName("reviews") val reviews: List<ReviewNetworkModel>
)
