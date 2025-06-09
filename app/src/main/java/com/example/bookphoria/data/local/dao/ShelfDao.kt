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
    @Query("""SELECT * FROM shelves WHERE userId = :userId AND id = :shelfId LIMIT 1""")
    fun getShelvesWithBooks(userId: Int, shelfId: Int): Flow<ShelfWithBooks?>

    @Transaction
    @Query("""SELECT * FROM shelves WHERE userId = :userId ORDER BY createdAt DESC""")
    fun getAllShelvesWithBooks(userId: Int): Flow<List<ShelfWithBooks>>

    @Insert
    suspend fun addBookToShelf(crossRef: ShelfBookCrossRef)

    @Delete
    suspend fun removeBookFromShelf(crossRef: ShelfBookCrossRef)

    @Query("SELECT bookId FROM ShelfBookCrossRef WHERE shelfId = :shelfId")
    suspend fun getBookIdsInShelf(shelfId: Int): List<Int>

    @Query("INSERT INTO ShelfBookCrossRef (shelfId, bookId) VALUES (:shelfId, :bookId)")
    suspend fun addBookToShelf(shelfId: Int, bookId: Int)

    @Query("SELECT id FROM shelves WHERE serverId = :serverId")
    suspend fun  getShelfByServerId(serverId: String): Int?

    @Query("SELECT serverId FROM shelves WHERE id = :id")
    suspend fun  getShelfById(id: Int): String?

    @Query("DELETE FROM shelves WHERE id = :shelfId")
    suspend fun deleteShelf(shelfId: Int)
}