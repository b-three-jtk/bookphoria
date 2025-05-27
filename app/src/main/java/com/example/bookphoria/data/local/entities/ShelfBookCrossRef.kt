package com.example.bookphoria.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["shelfId", "bookId"],
    foreignKeys = [
        ForeignKey(
            entity = ShelfEntity::class,
            parentColumns = ["id"],
            childColumns = ["shelfId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("shelfId"), Index("bookId")]
)
data class ShelfBookCrossRef(
    val shelfId: Int,
    val bookId: Int
)
