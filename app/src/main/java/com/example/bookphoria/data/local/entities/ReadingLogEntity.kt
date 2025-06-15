package com.example.bookphoria.data.local.entities

import androidx.room.Entity

@Entity(
    primaryKeys = ["userId", "bookId", "date"]
)
data class ReadingLogEntity(
    val userId: Int,
    val bookId: Int,
    val date: String, // yyyy-MM-dd
    val pagesRead: Int
)
