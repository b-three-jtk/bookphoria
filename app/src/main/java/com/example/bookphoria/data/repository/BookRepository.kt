package com.example.bookphoria.data.repository

import android.util.Log
import java.util.Locale
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.entities.AuthorEntity
import com.example.bookphoria.data.local.entities.BookAuthorCrossRef
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.BookGenreCrossRef
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.FullBookDataWithUserInfo
import com.example.bookphoria.data.local.entities.GenreEntity
import com.example.bookphoria.data.local.entities.UserBookCrossRef
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.BookApiService
import com.example.bookphoria.data.remote.pagingsources.BookSearchPagingSource
import com.example.bookphoria.data.remote.requests.AddBookRequest
import com.example.bookphoria.data.remote.requests.AddReviewRequest
import com.example.bookphoria.data.remote.requests.AddUserBookRequest
import com.example.bookphoria.data.remote.requests.EditBookRequest
import com.example.bookphoria.data.remote.responses.BookNetworkModel
import com.example.bookphoria.data.remote.responses.ReviewNetworkModel
import com.example.bookphoria.data.remote.responses.toFullBookData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val apiService: BookApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun addBookFromApi(request: AddBookRequest): Int {
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

            val coverPart = request.cover?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("cover", it.name, requestFile)
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
                val book = BookEntity(
                    serverId = response.book.id,
                    isbn = response.book.isbn,
                    title = response.book.title,
                    publisher = response.book.publisher,
                    publishedDate = response.book.publishedDate,
                    synopsis = response.book.synopsis,
                    pages = response.book.pages,
                    imageUrl = response.book.cover,
                )

                val localBookId = bookDao.insertBook(book).toInt()

                response.authors.forEach { authorNetwork ->
                    val existingId = bookDao.getAuthorId(authorNetwork.id)
                    val authorId = existingId
                        ?: bookDao.insertAuthor(
                            AuthorEntity(
                                name = authorNetwork.name,
                                desc = "",
                                serverId = authorNetwork.id
                            )
                        ).toInt()

                    Log.d("BookRepository", "Author ID: $authorId")

                    bookDao.insertBookAuthorCrossRef(
                        BookAuthorCrossRef(bookId = localBookId, authorId = authorId)
                    )

                }

                response.genres.forEach { genreNetwork ->
                    val existingId = bookDao.getGenreId(genreNetwork.id)
                    val genreId = existingId
                        ?: bookDao.insertGenre(
                            GenreEntity(
                                name = genreNetwork.name,
                                serverId = genreNetwork.id
                            )
                        ).toInt()

                    Log.d("BookRepository", "Genre ID: $genreId")

                    bookDao.insertBookGenreCrossRef(
                        BookGenreCrossRef(bookId = localBookId, genreId = genreId)
                    )
                }
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

    private suspend fun addToUserBooks(bookId: Int, status: String = "owned", pagesRead: Int? = 0, startDate: String? = null, endDate: String? = null) {
        val userId = userPreferences.getUserId().first()

        val userBook = userId?.let {
            UserBookCrossRef(
                userId = it,
                bookId = bookId,
                status = status,
                pagesRead = pagesRead ?: 0,
                startDate = startDate,
                endDate = endDate
            )
        }

        Log.d("BookRepository", "UserBook: $userBook")

        if (userBook != null) {
            bookDao.insertUserBookCrossRef(userBook)
        }
    }

    suspend fun updateBook(updated: EditBookRequest) {
        try {
            val title = updated.title.toRequestBody("text/plain".toMediaTypeOrNull())
            val publisher = updated.publisher?.toRequestBody("text/plain".toMediaTypeOrNull())
            val publishedDate =
                updated.publishedDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val synopsis = updated.synopsis.toRequestBody("text/plain".toMediaTypeOrNull())
            val isbn = updated.isbn.toRequestBody("text/plain".toMediaTypeOrNull())
            val pages = updated.pages.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val authors = updated.authors.map { it.toRequestBody("text/plain".toMediaTypeOrNull()) }
            val genres = updated.genres.map { it.toRequestBody("text/plain".toMediaTypeOrNull()) }

            val coverPart = updated.cover?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("cover", it.name, requestFile)
            }

            val response = apiService.editBook(
                token = "Bearer ${userPreferences.getAccessToken().first()}",
                id = updated.id,
                title = title,
                publisher = publisher,
                publishedDate = publishedDate,
                synopsis = synopsis,
                isbn = isbn,
                pages = pages,
                authors = authors,
                genres = genres,
                cover = coverPart
            )

            Log.d("Raw API response", response.toString())

            if (response.message == "Book updated successfully") {
                val book = BookEntity(
                    serverId = response.book.id,
                    isbn = response.book.isbn,
                    title = response.book.title,
                    publisher = response.book.publisher,
                    publishedDate = response.book.publishedDate,
                    synopsis = response.book.synopsis,
                    pages = response.book.pages,
                    imageUrl = response.book.cover,
                )

                Log.d("BookRepository", "Book Edited: $book")

                val localBookId = bookDao.updateBook(book).toInt()

                bookDao.deleteBookAuthorCrossRefs(localBookId)
                bookDao.deleteBookGenreCrossRefs(localBookId)

                updated.authors.forEach {
                    val existingId = bookDao.getAuthorId(it)
                    existingId?.let { it1 ->
                        BookAuthorCrossRef(localBookId,
                            it1
                        )
                    }?.let { it2 -> bookDao.insertBookAuthorCrossRef(it2) }
                }

                updated.genres.forEach {
                    val existingId = bookDao.getGenreId(it)
                    existingId?.let { it1 ->
                        BookGenreCrossRef(localBookId,
                            it1
                        )
                    }?.let { it2 -> bookDao.insertBookGenreCrossRef(it2) }
                }
            } else {
                throw Exception("Gagal memperbarui buku: ${response.message}")
            }
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat memperbarui buku: ${e.message}")
        }
    }

    suspend fun saveBooksToLocal(books: List<FullBookDataWithUserInfo?>) {
        try {
            books.forEach { bookWithRelations ->
                val bookEntity = bookWithRelations?.book

                val bookID = bookEntity?.let { bookDao.insertBook(it).toInt() }

                bookWithRelations?.authors?.forEach { author ->
                    val existingAuthorId = bookDao.getAuthorId(author.serverId)
                    val authorId = existingAuthorId ?: bookDao.insertAuthor(
                        AuthorEntity(
                            id = author.id, name = author.name, desc = author.desc,
                            serverId = author.serverId
                        )
                    )

                    if (bookID != null) {
                        BookAuthorCrossRef(bookId = bookID, authorId = authorId.toInt())
                            .let {
                                bookDao.insertBookAuthorCrossRef(
                                    it
                                )
                            }
                    }
                }

                bookWithRelations?.genres?.forEach { genre ->
                    val existingGenreId = bookDao.getGenreId(genre.serverId)
                    val genreId = existingGenreId ?: bookDao.insertGenre(
                        GenreEntity(
                            id = genre.id, name = genre.name,
                            serverId = genre.serverId
                        )
                    )

                    bookID?.let { BookGenreCrossRef(bookId = it, genreId = genreId.toInt()) }?.let {
                        bookDao.insertBookGenreCrossRef(
                            it
                        )
                    }
                }

                if (bookID != null) {
                    addToUserBooks(bookID)
                }
            }
        } catch (e: Exception) {
            Log.d("BookRepository", "Book failed: $e")
            throw Exception("Terjadi kesalahan saat menambahkan buku: ${e.message}")
        }
    }

    suspend fun getBookById(bookId: Int): BookWithGenresAndAuthors? {
        val book = bookDao.getBookServerIdById(bookId)
        val fetchedBook = bookDao.getBookById(book)
        Log.d("BookRepository", "Fetched book: $fetchedBook")
        return fetchedBook
    }

    suspend fun getYourBooksRemote(userId: Int): List<FullBookDataWithUserInfo> {
        val accessToken = userPreferences.getAccessToken().first()
            ?: throw Exception("Access token not available")

        val response = apiService.getYourBooks("Bearer $accessToken")
        val books = response.data

        return books.map { book ->
            val bookId = bookDao.getBookIdByServerId(book.id)
            if (bookId != null) {
                addToUserBooks(bookId, "owned")
            }
            val localUserBook = bookId?.let { bookDao.getUserBookCrossRef(userId, it) }

            localUserBook?.let { book.toFullBookData(userId, it.bookId) }!!
        }
    }

    fun getYourBooksLocal(userId: Int): Flow<List<FullBookDataWithUserInfo>> {
        return bookDao.getYourBooks(userId)
    }

    fun getCurrentlyReadingLocal(
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

    suspend fun addReview(bookId: Int, desc: String, rate: Int) {
        try {
            val bookStrId = bookDao.getBookServerIdById(bookId)
            Log.d("BookRepository", "Book ID: $bookStrId")
            val req = AddReviewRequest(
                bookId = bookStrId,
                desc = desc,
                rate = rate
            )
            Log.d("BookRepository", "Request: $req")
            apiService.addReview(
                token = "Bearer ${userPreferences.getAccessToken().first()}",
                request = req
            )
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat menambahkan review buku: ${e.message}")
        }
    }

    suspend fun getReviews(bookId: Int): List<ReviewNetworkModel> {
        try {
            val bookStrId = bookDao.getBookServerIdById(bookId)
            val review = apiService.getReviews(
                token = "Bearer ${userPreferences.getAccessToken().first()}",
                bookId = bookStrId
            )
            return review
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat mengambil review buku: ${e.message}")
        }
    }

    suspend fun getReviewsNetwork(bookId: String): List<ReviewNetworkModel> {
        try {
            val review = apiService.getReviews(
                token = "Bearer ${userPreferences.getAccessToken().first()}",
                bookId = bookId
            )
            return review
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat mengambil review buku: ${e.message}")
        }
    }

    suspend fun getBookNetworkById(bookId: String): BookNetworkModel {
        try {
            val accessToken = userPreferences.getAccessToken().first()

            val book = apiService.getBookById(
                token = "Bearer $accessToken",
                id = bookId
            )
            return book.book
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat mengambil buku: ${e.message}")
        }
    }

    suspend fun getAllAuthors(): List<AuthorEntity> {
        try {
            return bookDao.getAllAuthors()
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat mengambil data: ${e.message}")
        }
    }

    suspend fun getAllGenres(): List<GenreEntity> {
        try {
            return bookDao.getAllGenres()
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat mengambil data: ${e.message}")
        }
    }

    suspend fun getBookStatus(userId: Int, bookId: Int): String {
        return bookDao.getBookStatus(userId, bookId) ?: "none"
    }

    suspend fun updateBookStatus(userId: Int, bookId: Int, newStatus: String, pagesRead: Int = 0) {
        val existingCrossRef = bookDao.getUserBookCrossRef(userId, bookId)
        val token = userPreferences.getAccessToken().first()
        val bookLocalId = bookDao.getBookServerIdById(bookId)

        if (existingCrossRef != null) {
            bookDao.updateBookStatus(userId, bookId, newStatus)
            apiService.updateStatus("Bearer $token", bookLocalId,
                AddUserBookRequest(
                    bookId = bookLocalId,
                    status = newStatus,
                    pagesCount = pagesRead,
                    startDate = null,
                    finishDate = null
                )
            )
        } else {
            bookDao.insertUserBookCrossRef(
                UserBookCrossRef(
                    userId = userId,
                    bookId = bookId,
                    status = newStatus,
                    pagesRead = pagesRead,
                    startDate = null,
                    endDate = null
                )
            )
            apiService.addUserBook(
                token = "Bearer $token",
                request = AddUserBookRequest(
                    bookId = bookLocalId,
                    status = newStatus,
                    pagesCount = pagesRead,
                    startDate = null,
                    finishDate = null
                )
            )
        }
    }

    suspend fun updateNewBookStatus(userId: Int, bookId: String, newStatus: String) {
        val token = userPreferences.getAccessToken().first() ?: throw Exception("Access token not available")
        val bookLocalId = bookDao.getBookIdByServerId(bookId)
        val existingCrossRef = bookLocalId?.let { bookDao.getUserBookCrossRef(userId, it) }
        Log.d("BookRepository", "Book local ID: $bookLocalId, Existing cross-ref: $existingCrossRef")

        try {
            if (existingCrossRef != null) {
                bookDao.updateBookStatus(userId, bookLocalId, newStatus)
                apiService.updateStatus("Bearer $token", bookId,
                    AddUserBookRequest(
                        bookId = bookId,
                        status = newStatus,
                        pagesCount = 0,
                        startDate = null,
                        finishDate = null
                    ))
            } else {
                apiService.addUserBook(
                    token = "Bearer $token",
                    request = AddUserBookRequest(
                        bookId = bookId,
                        status = newStatus,
                        pagesCount = 0,
                        startDate = null,
                        finishDate = null
                    )
                )
                val bookNetwork = apiService.getYourBook(
                    "Bearer $token", bookId
                )

                val book = BookEntity(
                    serverId = bookNetwork.data.book.id,
                    isbn = bookNetwork.data.book.isbn,
                    title = bookNetwork.data.book.title,
                    publisher = bookNetwork.data.book.publisher,
                    publishedDate = bookNetwork.data.book.publishedDate,
                    synopsis = bookNetwork.data.book.synopsis,
                    pages = bookNetwork.data.book.pages,
                    imageUrl = bookNetwork.data.book.cover
                )

                val localBookId = bookDao.insertBook(book).toInt()
                bookNetwork.data.book.authors.forEach { authorNetwork ->
                    val existingId = bookDao.getAuthorId(authorNetwork.id)
                    val authorId = existingId
                        ?: bookDao.insertAuthor(
                            AuthorEntity(
                                name = authorNetwork.name,
                                desc = "",
                                serverId = authorNetwork.id
                            )
                        ).toInt()

                    Log.d("BookRepository", "Author ID: $authorId")

                    bookDao.insertBookAuthorCrossRef(
                        BookAuthorCrossRef(bookId = localBookId, authorId = authorId)
                    )

                }

                bookNetwork.data.book.genres.forEach { genreNetwork ->
                    val existingId = bookDao.getGenreId(genreNetwork.id)
                    val genreId = existingId
                        ?: bookDao.insertGenre(
                            GenreEntity(
                                name = genreNetwork.name,
                                serverId = genreNetwork.id
                            )
                        ).toInt()

                    Log.d("BookRepository", "Genre ID: $genreId")

                    bookDao.insertBookGenreCrossRef(
                        BookGenreCrossRef(bookId = localBookId, genreId = genreId)
                    )
                }
                addToUserBooks(localBookId)
            }
        } catch (e: Exception) {
            Log.e("BookRepository", "Failed to update book status for bookId: $bookId, userId: $userId, status: $newStatus", e)
            throw Exception("Failed to update book status: ${e.message}")
        }
    }

    suspend fun deleteUserBook(bookId: Int) {
        try {
            val token = userPreferences.getAccessToken().first()
            val userId = userPreferences.getUserId().first()
            val bookLocalId = bookDao.getBookServerIdById(bookId)

            apiService.removeUserBook(
                token = "Bearer $token",
                id = bookLocalId
            )
            if (userId != null) {
                bookDao.deleteUserBook(userId, bookId)
                bookDao.deleteBookById(bookId)
            }
        } catch (e: Exception) {
            Log.d("BookRepository", "Failed to delete user book: ${e.message}")
        }
    }
}