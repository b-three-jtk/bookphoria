package com.example.bookphoria.data.local.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(primaryKeys = ["bookId", "authorId"],
    indices = [
        Index(value = ["bookId"]),
        Index(value = ["authorId"])
    ])

data class BookAuthorCrossRef(
    val bookId: Int,
    val authorId: Int
)
