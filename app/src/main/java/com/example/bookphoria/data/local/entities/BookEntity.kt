package com.example.bookphoria.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.bookphoria.data.remote.responses.AddBookNetworkModel
import com.example.bookphoria.data.remote.responses.AddBookResponse
import com.example.bookphoria.data.remote.responses.BookNetworkModel

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serverId: String, // UUID dari server
    val title: String,
    val publisher: String,
    val publishedDate: String,
    val synopsis: String,
    val isbn: String,
    val pages: Int,
    val imageUrl: String?
)

fun BookNetworkModel.toBookEntity(): BookEntity = BookEntity(
    serverId = this.id,
    title = this.title,
    publisher = this.publisher,
    publishedDate = this.publishedDate,
    synopsis = this.synopsis,
    isbn = this.isbn,
    pages = this.pages,
    imageUrl = this.cover
)

fun AddBookResponse.toBookWithGenresAndAuthors(): BookWithGenresAndAuthors = BookWithGenresAndAuthors(
    book = BookEntity(
        serverId = this.book.id,
        title = this.book.title,
        publisher = this.book.publisher,
        publishedDate = this.book.publishedDate,
        synopsis = this.book.synopsis,
        isbn = this.book.isbn,
        pages = this.book.pages,
        imageUrl = this.book.cover
    ),
    authors = this.authors.map { AuthorEntity(id = it.id, name = it.name, desc = "") },
    genres = this.genres.map { GenreEntity(id = it.id, name = it.name) }
)

data class BookWithGenresAndAuthors(
    @Embedded val book: BookEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BookAuthorCrossRef::class,
            parentColumn = "bookId",
            entityColumn = "authorId"
        )
    )
    val authors: List<AuthorEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BookGenreCrossRef::class,
            parentColumn = "bookId",
            entityColumn = "genreId"
        )
    )
    val genres: List<GenreEntity>,
)

data class FullBookDataWithUserInfo(
    @Embedded val book: BookEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BookAuthorCrossRef::class,
            parentColumn = "bookId",
            entityColumn = "authorId"
        )
    )
    val authors: List<AuthorEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BookGenreCrossRef::class,
            parentColumn = "bookId",
            entityColumn = "genreId"
        )
    )
    val genres: List<GenreEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    val userBookCrossRefs: UserBookCrossRef
)
