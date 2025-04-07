package com.example.bookphoria.data.remote.responses

import com.google.gson.annotations.SerializedName
import java.util.Date

data class BookSearchResponse(
    @SerializedName("books") val data: List<BookNetworkModel>
)

data class BookNetworkModel(
    @SerializedName("isbn") val isbn: String,
    @SerializedName("title") val title: String,
    @SerializedName("publisher") val publisher: String,
    @SerializedName("published_date") val publishedDate: String,
    @SerializedName("synopsis") val synopsis: String,
    @SerializedName("pages") val pages: Int,
    @SerializedName("id") val id: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("authors") val authors: List<AuthorNetworkModel> = emptyList(),
    @SerializedName("genres") val genres: List<GenreNetworkModel> = emptyList()
)

data class AddBookResponse(
    @SerializedName("message") val message: String,
    @SerializedName("book") val book: BookNetworkModel
)

data class AddBookRequest(
    @SerializedName("title") val title: String,
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("published_date") val publishedDate: String,
    @SerializedName("synopsis") val synopsis: String,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("pages") val pages: Int,
    @SerializedName("cover") val cover: String?,
    @SerializedName("authors") val authors: List<String>,
    @SerializedName("genres") val genres: List<String>,
    @SerializedName("user_status") val userStatus: String,
    @SerializedName("user_page_count") val userPageCount: Int,
    @SerializedName("user_start_date") val userStartDate: Date?,
    @SerializedName("user_finish_date") val userFinishDate: Date?
)

data class AuthorNetworkModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("desc") val desc: String
)

data class GenreNetworkModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)
