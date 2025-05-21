package com.example.bookphoria.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.entities.AuthorEntity
import com.example.bookphoria.data.local.entities.BookAuthorCrossRef
import com.example.bookphoria.data.local.entities.BookGenreCrossRef
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.FullBookDataWithUserInfo
import com.example.bookphoria.data.local.entities.GenreEntity
import com.example.bookphoria.data.local.entities.UserBookCrossRef
import com.example.bookphoria.data.local.entities.toBookEntity
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.BookApiService
import com.example.bookphoria.data.remote.pagingsources.BookSearchPagingSource
import com.example.bookphoria.data.remote.responses.AddBookRequest
import com.example.bookphoria.data.remote.responses.BookNetworkModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val apiService: BookApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun insertBook(
        bookNetworkModel: BookNetworkModel
    ): Int {
        Log.d("BookRepository", "Book added: ${bookNetworkModel.title}")
        val bookEntity = bookNetworkModel.toBookEntity()

        try {
            bookDao.insertBook(bookEntity)
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat menambahkan buku: ${e.message}")
            Log.d("BookRepository", "Book failed: ${e}")
        }
        val localBookId = bookDao.getBookIdByIsbn(bookEntity.isbn)

        bookNetworkModel.authors.forEach { authorNetwork ->
            val existingId = bookDao.getAuthorIdByName(authorNetwork.name)
            val authorId = existingId ?: bookDao.insertAuthor(
                AuthorEntity(name = authorNetwork.name, desc = "")
            ).toInt()

            bookDao.insertBookAuthorCrossRef(
                BookAuthorCrossRef(bookId = localBookId, authorId = authorId)
            )
        }

        // Insert genres
        bookNetworkModel.genres.forEach { genreNetwork ->
            val existingId = bookDao.getGenreIdByName(genreNetwork.name)
            val genreId = existingId ?: bookDao.insertGenre(
                GenreEntity(name = genreNetwork.name)
            ).toInt()

            bookDao.insertBookGenreCrossRef(
                BookGenreCrossRef(bookId = localBookId, genreId = genreId)
            )
        }

        return localBookId
    }


    suspend fun addBookFromApi(request: AddBookRequest): Int {
        val accessToken = userPreferences.getAccessToken().first()
        Log.d("BookRepository", "Token: $accessToken")

        if (accessToken != null) {
            try {
                val response = apiService.addBook("Bearer $accessToken", request)

                if (response.message == "Book added successfully") {
                    val bookNetworkModel = response.book
                    val localBookId = insertBook(bookNetworkModel)

                    addToUserBooks(localBookId)

                    return localBookId
                } else {
                    throw Exception("Gagal menambahkan buku: ${response.message}")
                }
            } catch (e: Exception) {
                throw Exception("Terjadi kesalahan saat menambahkan buku: ${e.message}")
            }
        } else {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }
    }

    suspend fun addToUserBooks(bookId: Int, status: String = "Belum dibaca") {
        val userId = userPreferences.getUserId().first()

        val userBook = userId?.let {
            UserBookCrossRef(
                userId = it,
                bookId = bookId,
                status = status,
                pagesRead = 0,
                startDate = null,
                endDate = null
            )
        }

        if (userBook != null) {
            bookDao.insertUserBookCrossRef(userBook)
        }
    }

    suspend fun getBookById(bookId: Int): BookWithGenresAndAuthors? {
        return bookDao.getBookById(bookId)
    }

    fun getYourBooks(
        userId: Int
    ): Flow<List<BookWithGenresAndAuthors>> {
        return bookDao.getYourBooks(
            userId
        )
    }

    fun getYourCurrentlyReadingBooks(
        userId: Int,
        status: String
    ): Flow<List<FullBookDataWithUserInfo>> {
        return bookDao.getYourCurrentlyReadingBooks(
            userId,
            status
        )
    }

    fun searchBook(query: String, token: String): Flow<PagingData<BookNetworkModel>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = {
                BookSearchPagingSource(apiService, query, token, bookRepository = this)
            }
        ).flow
    }


    suspend fun updateReadingProgress(crossRef: UserBookCrossRef) {
        bookDao.insertUserBookCrossRef(crossRef)
    }

    suspend fun getReadingProgress(userId: Int, bookId: Int): Int? {
        return bookDao.getReadingProgress(userId, bookId)
    }
}