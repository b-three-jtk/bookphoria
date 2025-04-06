package com.example.bookphoria.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["bookId", "authorId"])
data class BookAuthorCrossRef(
    val bookId: Int,
    val authorId: Int
)
