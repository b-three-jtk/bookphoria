package com.example.bookphoria.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.bookphoria.data.local.entities.AuthorEntity
import com.example.bookphoria.data.local.entities.BookAuthorCrossRef
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.BookGenreCrossRef
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.FullBookDataWithUserInfo
import com.example.bookphoria.data.local.entities.GenreEntity
import com.example.bookphoria.data.local.entities.UserBookCrossRef
import com.example.bookphoria.data.local.entities.UserWithBooks
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: GenreEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookAuthorCrossRef(crossRef: BookAuthorCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookGenreCrossRef(crossRef: BookGenreCrossRef)

    @Transaction
    @Query("SELECT * FROM books WHERE serverId = :serverId LIMIT 1")
    suspend fun getBookById(serverId: String): BookWithGenresAndAuthors?

    @Query("SELECT * FROM books WHERE serverId = :bookId LIMIT 1")
    suspend fun getBookByNetworkId(bookId: String): BookEntity

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithBooks(userId: Int): UserWithBooks

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserWithBooksStream(userId: Int): Flow<UserWithBooks>

    @Query("SELECT id FROM authors WHERE serverId = :id LIMIT 1")
    suspend fun getAuthorId(id: String): Int?

    @Query("SELECT id FROM genres WHERE serverId = :id LIMIT 1")
    suspend fun getGenreId(id: String): Int?

    @Query("SELECT * FROM UserBookCrossRef WHERE bookId = :bookId AND userId = :userId LIMIT 1")
    suspend fun getUserBookCrossRef(userId: Int, bookId: Int): UserBookCrossRef?

    @Query("DELETE FROM UserBookCrossRef WHERE userId = :userId AND bookId = :bookId")
    suspend fun deleteUserBook(userId: Int, bookId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBook(book: BookEntity): Long

    @Query("SELECT id FROM books WHERE isbn = :isbn LIMIT 1")
    suspend fun getBookIdByIsbn(isbn: String): Int?

    @Query("SELECT id FROM books WHERE serverId = :bookId LIMIT 1")
    suspend fun getBookIdByServerId(bookId: String): Int?

    @Query("SELECT serverId FROM books WHERE id = :bookId LIMIT 1")
    suspend fun getBookServerIdById(bookId: Int): String

    @Query("DELETE FROM BookAuthorCrossRef WHERE bookId = :bookId")
    suspend fun deleteBookAuthorCrossRefs(bookId: Int)

    @Query("DELETE FROM BookGenreCrossRef WHERE bookId = :bookId")
    suspend fun deleteBookGenreCrossRefs(bookId: Int)

    //delete book
    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserBookCrossRef(crossRef: UserBookCrossRef)

    @Transaction
    @Query("SELECT * FROM books WHERE id IN ( SELECT bookId FROM userbookcrossref WHERE userId = :userId )")
    fun getYourBooks(userId: Int): Flow<List<FullBookDataWithUserInfo>>

    @Transaction
    @Query("SELECT * FROM books WHERE id IN ( SELECT bookId FROM userbookcrossref WHERE userId = :userId AND status = :status )")
    fun getYourCurrentlyReadingBooks(userId: Int, status: String): Flow<List<FullBookDataWithUserInfo>>

    @Query("SELECT pagesRead FROM UserBookCrossRef WHERE userId = :userId AND bookId = :bookId")
    suspend fun getReadingProgress(userId: Int, bookId: Int): Int?

    @Query("SELECT status FROM UserBookCrossRef WHERE userId = :userId AND bookId = :bookId")
    suspend fun getBookStatus(userId: Int, bookId: Int): String?

    @Query("UPDATE UserBookCrossRef SET status = :newStatus WHERE userId = :userId AND bookId = :bookId")
    suspend fun updateBookStatus(userId: Int, bookId: Int, newStatus: String)

    @Query("SELECT * FROM authors")
    suspend fun getAllAuthors(): List<AuthorEntity>

    @Query("SELECT * FROM genres")
    suspend fun getAllGenres(): List<GenreEntity>

    // GET FULL BOOK DATA WITH NETWORK ID
    @Transaction
    @Query("SELECT * FROM books WHERE serverId = :serverId LIMIT 1")
    suspend fun getFullBookDataWithNetworkId(serverId: String): FullBookDataWithUserInfo?
}