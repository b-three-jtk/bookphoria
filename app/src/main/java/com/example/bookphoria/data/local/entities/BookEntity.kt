package com.example.bookphoria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val description: String,
    val imageUrl: String
)
