package com.example.bookphoria.data.remote.responses

import com.google.gson.annotations.SerializedName

data class updateShelfResponse(
    @SerializedName("message") val message: String,
    @SerializedName("shelf") val shelf: ShelfNetworkModel
)

data class ShelfNetworkModel(
    @SerializedName("name") val name: String,
    @SerializedName("desc") val desc: String,
    @SerializedName("image") val image: String?,
)

data class ShelfDetailNetworkModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("desc") val desc: String,
    @SerializedName("image") val image: String?,
    @SerializedName("books") val books: List<BookNetworkModel>
)

data class ShelfResponse(
    @SerializedName("message")
    val message: String
)