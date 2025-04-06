package com.example.bookphoria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
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
