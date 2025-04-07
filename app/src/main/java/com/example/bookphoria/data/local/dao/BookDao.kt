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
import com.example.bookphoria.data.local.entities.GenreEntity
import com.example.bookphoria.data.local.entities.UserWithBooks
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: GenreEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookAuthorCrossRef(crossRef: BookAuthorCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookGenreCrossRef(crossRef: BookGenreCrossRef)

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    suspend fun getBookById(bookId: Int): BookWithGenresAndAuthors?

//    @Query("SELECT * FROM books")
//    suspend fun getAllBooks(): List<BookEntity>
//
//    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR isbn LIKE '%' || :query || '%'")
//    suspend fun searchBooksByTitleOrIsbn(query: String): List<BookEntity>
//
//    @Query("SELECT * FROM books WHERE isbn  LIKE '%' || :query || '%'")
//    suspend fun searchBooksByIsbn(query: String): List<BookEntity>

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithBooks(userId: Int): UserWithBooks

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserWithBooksStream(userId: Int): Flow<UserWithBooks>

    @Query("SELECT id FROM authors WHERE name = :name LIMIT 1")
    suspend fun getAuthorIdByName(name: String): Int?

    @Query("SELECT id FROM genres WHERE name = :name LIMIT 1")
    suspend fun getGenreIdByName(name: String): Int?

    @Query("DELETE FROM UserBookCrossRef WHERE userId = :userId AND bookId = :bookId")
    suspend fun deleteUserBook(userId: Int, bookId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBook(book: BookEntity)

    @Query("SELECT id FROM books WHERE isbn = :isbn LIMIT 1")
    suspend fun getBookIdByIsbn(isbn: String): Int


}