package com.example.bookphoria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,  // ID dari backend
    val name: String,
    val email: String,
    val profilePicture: String? = null
)
