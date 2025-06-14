package com.example.bookphoria.data.remote.requests

import com.google.gson.annotations.SerializedName
import java.io.File
import java.util.Date

data class AddBookRequest(
    @SerializedName("title") val title: String,
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("published_date") val publishedDate: String,
    @SerializedName("synopsis") val synopsis: String,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("pages") val pages: Int,
    @SerializedName("cover") val cover: File?,
    @SerializedName("authors") val authors: List<String>,
    @SerializedName("genres") val genres: List<String>,
    @SerializedName("user_status") val userStatus: String,
    @SerializedName("user_page_count") val userPageCount: Int,
    @SerializedName("user_start_date") val userStartDate: Date?,
    @SerializedName("user_finish_date") val userFinishDate: Date?
)

data class EditBookRequest(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("published_date") val publishedDate: String,
    @SerializedName("synopsis") val synopsis: String,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("pages") val pages: Int,
    @SerializedName("cover") val cover: File?,
    @SerializedName("authors") val authors: List<String>,
    @SerializedName("genres") val genres: List<String>,
)

data class AddReviewRequest(
    @SerializedName("book_id") val bookId: String,
    @SerializedName("rate") val rate: Int,
    @SerializedName("desc") val desc: String
)

data class AddUserBookRequest(
    @SerializedName("book_id") val bookId: String,
    @SerializedName("status") val status: String,
    @SerializedName("page_count") val pagesCount: Int,
    @SerializedName("start_date") val startDate: Date?,
    @SerializedName("finish_date") val finishDate: Date?
)
