package com.example.bookphoria.data.repository

import android.util.Log
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.entities.AuthorEntity
import com.example.bookphoria.data.local.entities.BookAuthorCrossRef
import com.example.bookphoria.data.local.entities.BookGenreCrossRef
import com.example.bookphoria.data.local.entities.GenreEntity
import com.example.bookphoria.data.local.entities.toBookEntity
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.BookApiService
import com.example.bookphoria.data.remote.responses.AddBookRequest
import com.example.bookphoria.data.remote.responses.BookNetworkModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val apiService: BookApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun insertBook(
        bookNetworkModel: BookNetworkModel
    ) {
        Log.d("BookRepository", "Book added: ${bookNetworkModel.title}")
        val bookEntity = bookNetworkModel.toBookEntity()
        bookDao.insertBook(bookEntity)

        val localBookId = bookDao.getBookIdByIsbn(bookEntity.isbn)

        bookNetworkModel.authors.forEach { authorNetwork ->
            val existingId = bookDao.getAuthorIdByName(authorNetwork.name)
            val authorId = existingId ?: bookDao.insertAuthor(
                AuthorEntity(name = authorNetwork.name, desc = "")
            ).toInt()

            bookDao.insertBookAuthorCrossRef(BookAuthorCrossRef(bookId = localBookId, authorId = authorId))
        }

        bookNetworkModel.genres.forEach { genreNetwork ->
            val existingId = bookDao.getGenreIdByName(genreNetwork.name)
            val genreId = existingId ?: bookDao.insertGenre(
                GenreEntity(name = genreNetwork.name)
            ).toInt()

            bookDao.insertBookGenreCrossRef(BookGenreCrossRef(bookId = localBookId, genreId = genreId))
        }
    }

    suspend fun addBookFromApi(request: AddBookRequest) {
        val accessToken = userPreferences.getAccessToken().first()
        Log.d("BookRepository", "Token: $accessToken")

        if (accessToken != null) {
            try {
                val response = apiService.addBook("Bearer $accessToken", request)

                if (response.message == "Book added successfully") {
                    val bookNetworkModel = response.book
                    insertBook(bookNetworkModel)
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


}