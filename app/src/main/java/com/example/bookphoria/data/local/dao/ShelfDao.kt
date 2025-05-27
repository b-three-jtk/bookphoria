package com.example.bookphoria.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.bookphoria.data.local.entities.ShelfBookCrossRef
import com.example.bookphoria.data.local.entities.ShelfEntity
import com.example.bookphoria.data.local.entities.ShelfWithBooks
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {
    @Insert
    suspend fun insert(shelf: ShelfEntity): Long

    @Query("SELECT * FROM shelves ORDER BY createdAt DESC")
    fun getAllShelves(): Flow<List<ShelfEntity>>

    @Delete
    suspend fun delete(shelf: ShelfEntity)

    @Transaction
    @Query("SELECT * FROM shelves WHERE userId = :userId")
    fun getShelvesWithBooks(userId: Int): Flow<List<ShelfWithBooks>>

    @Insert
    suspend fun addBookToShelf(crossRef: ShelfBookCrossRef)

    @Delete
    suspend fun removeBookFromShelf(crossRef: ShelfBookCrossRef)
}