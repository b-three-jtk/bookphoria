package com.example.bookphoria.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.bookphoria.data.local.entities.BookWithAuthors
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.entities.UserFriendCrossRef
import com.example.bookphoria.data.local.entities.UserWithFriends

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(crossRef: UserFriendCrossRef)

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithFriends(userId: Int): UserWithFriends

    @Transaction
    @Query("""
    SELECT * FROM books 
    INNER JOIN userbookcrossref ON books.id = userbookcrossref.bookId 
    WHERE userbookcrossref.userId = :userId"""
    )
    suspend fun getBooksWithAuthorsByUser(userId: Int): List<BookWithAuthors>
}