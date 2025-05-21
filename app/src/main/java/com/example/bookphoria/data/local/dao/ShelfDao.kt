package com.example.bookphoria.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.bookphoria.data.local.entities.ShelfEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {
    @Insert
    suspend fun insert(shelf: ShelfEntity)

    @Query("SELECT * FROM shelves ORDER BY createdAt DESC")
    fun getAllShelves(): Flow<List<ShelfEntity>>

    @Delete
    suspend fun delete(shelf: ShelfEntity)
}