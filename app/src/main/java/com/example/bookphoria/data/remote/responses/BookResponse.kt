package com.example.bookphoria.data.remote.responses

import com.example.bookphoria.data.local.entities.AuthorEntity
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.FullBookDataWithUserInfo
import com.example.bookphoria.data.local.entities.GenreEntity
import com.example.bookphoria.data.local.entities.UserBookCrossRef
import com.google.gson.annotations.SerializedName
import java.io.File
import java.util.Date

data class BookSearchResponse(
    @SerializedName("books") val data: List<BookNetworkModel>
)

data class UserBookStatusResponse(
    @SerializedName("data") val data: List<BookStatusNetworkModel>
)

data class BookStatusNetworkModel(
    @SerializedName("user_book_id") val id: String,
    @SerializedName("status") val status: String,
    @SerializedName("page_count") val pagesRead: Int,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("finish_date") val endDate: String,
    @SerializedName("book") val book: BookNetworkModel,
)

fun BookStatusNetworkModel.toFullBookData(userId: Int, bookId: Int): FullBookDataWithUserInfo {
    return FullBookDataWithUserInfo(
        book = BookEntity(
            serverId = this.id,
            isbn = this.book.isbn,
            title = this.book.title,
            publisher = this.book.publisher,
            publishedDate = this.book.publishedDate,
            synopsis = this.book.synopsis,
            pages = this.book.pages,
            imageUrl = this.book.cover,
        ),
        authors = this.book.authors.map {
            AuthorEntity(
                id = it.id,
                name = it.name,
                desc = it.desc?: ""
            )
            },

        genres = this.book.genres.map {
            GenreEntity(
                id = it.id,
                name = it.name
            )
        },
        userBookCrossRefs = UserBookCrossRef(
            userId = userId,
            bookId = bookId,
            status = this.status,
            pagesRead = this.pagesRead,
            startDate = this.startDate,
            endDate = this.endDate
        )

    )
}

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
    @SerializedName("book") val book: AddBookNetworkModel,
    @SerializedName("authors") val authors: List<AuthorNetworkModel> = emptyList(),
    @SerializedName("genres") val genres: List<GenreNetworkModel> = emptyList()
)

data class AddBookNetworkModel(
    @SerializedName("isbn") val isbn: String,
    @SerializedName("title") val title: String,
    @SerializedName("publisher") val publisher: String,
    @SerializedName("published_date") val publishedDate: String,
    @SerializedName("synopsis") val synopsis: String,
    @SerializedName("pages") val pages: Int,
    @SerializedName("id") val id: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("created_at") val createdAt: String
)

data class EditBookResponse(
    @SerializedName("message") val message: String,
    @SerializedName("book") val book: BookNetworkModel,
    @SerializedName("authors") val authors: List<AuthorNetworkModel> = emptyList(),
    @SerializedName("genres") val genres: List<GenreNetworkModel> = emptyList()
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
