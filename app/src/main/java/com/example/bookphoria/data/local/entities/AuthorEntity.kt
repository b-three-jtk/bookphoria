package com.example.bookphoria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authors")
data class AuthorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val desc: String,
)