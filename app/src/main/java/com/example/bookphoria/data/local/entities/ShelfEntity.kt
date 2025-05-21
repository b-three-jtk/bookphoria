package com.example.bookphoria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shelves")
data class ShelfEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serverId: String?,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val createdAt: Long = System.currentTimeMillis()
)