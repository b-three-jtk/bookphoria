package com.example.bookphoria.data.local.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["bookId", "genreId"],
    indices = [Index(value = ["genreId"])]
)
data class BookGenreCrossRef(
    val bookId: Int,
    val genreId: Int
)
