package com.example.bookphoria.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import java.util.Locale
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
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
            Log.d("BookRepository", "Book failed: $e")
            throw Exception("Terjadi kesalahan saat menambahkan buku: ${e.message}")
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


    suspend fun addBookFromApi(request: AddBookRequest, context: Context): Int {
        val accessToken = userPreferences.getAccessToken().first()
        Log.d("BookRepository", "Token: $accessToken")

        if (accessToken == null) {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }

        try {
            val title = request.title.toRequestBody("text/plain".toMediaTypeOrNull())
            val publisher = request.publisher?.toRequestBody("text/plain".toMediaTypeOrNull())
            val publishedDate = request.publishedDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val synopsis = request.synopsis.toRequestBody("text/plain".toMediaTypeOrNull())
            val isbn = request.isbn.toRequestBody("text/plain".toMediaTypeOrNull())
            val pages = request.pages.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val userStatus = request.userStatus.toRequestBody("text/plain".toMediaTypeOrNull())
            val userPageCount = request.userPageCount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Sesuaikan format

            val userStartDate = request.userStartDate?.let { dateFormat.format(it) }
            val userFinishDate = request.userFinishDate?.let { dateFormat.format(it) }
            val authors = request.authors.map { it.toRequestBody("text/plain".toMediaTypeOrNull()) }
            val genres = request.genres.map { it.toRequestBody("text/plain".toMediaTypeOrNull()) }

            val coverPart: MultipartBody.Part? = if (request.cover?.isNotEmpty() == true) {
                val uri = Uri.parse(request.cover)
                val file = uri.toFile(context) // Convert Uri to File
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("cover", file.name, requestFile)
            } else {
                null
            }

            val response = apiService.addBook(
                token = "Bearer $accessToken",
                title = title,
                publisher = publisher,
                publishedDate = publishedDate,
                synopsis = synopsis,
                isbn = isbn,
                pages = pages,
                authors = authors,
                genres = genres,
                userStatus = userStatus,
                userPageCount = userPageCount,
                userStartDate = userStartDate,
                userFinishDate = userFinishDate,
                cover = coverPart
            )

            Log.d("Raw API response", response.toString())

            if (response.message == "Book added successfully") {
                val bookNetworkModel = response.book
                val localBookId = insertBook(bookNetworkModel)
                addToUserBooks(localBookId)
                return localBookId
            } else {
                throw Exception("Gagal menambahkan buku: ${response.message}")
            }
        } catch (e: Exception) {
            Log.d("BookRepository", "Book failed: $e")
            throw Exception("Terjadi kesalahan saat menambahkan buku: ${e.message}")

        }
    }

    // Helper function to convert Uri to File
    private fun Uri.toFile(context: Context): File {
        val file = File(context.cacheDir, "cover_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(this)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private suspend fun addToUserBooks(bookId: Int, status: String = "Belum dibaca") {
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

    suspend fun getUserBooksCount(userId: Int): Int {
        return getYourBooks(userId).first().size
    }
}