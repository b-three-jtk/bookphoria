package com.example.bookphoria.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["bookId", "genreId"])
data class BookGenreCrossRef(
    val bookId: Int,
    val genreId: Int
)
