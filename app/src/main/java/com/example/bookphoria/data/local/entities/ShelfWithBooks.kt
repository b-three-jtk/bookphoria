package com.example.bookphoria.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ShelfWithBooks(
    @Embedded val shelf: ShelfEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ShelfBookCrossRef::class,
            parentColumn = "shelfId",
            entityColumn = "bookId"
        )
    )
    val books: List<BookEntity>
)
