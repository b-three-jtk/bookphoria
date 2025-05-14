package com.example.bookphoria.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class BookWithAuthors(
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
    val authors: List<AuthorEntity>
)
