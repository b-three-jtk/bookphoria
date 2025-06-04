package com.example.bookphoria.data.local.entities

import androidx.room.PrimaryKey

data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val desc: String,
    val rate: Int,
    val createdAt: String,
    val user: UserEntity
)

