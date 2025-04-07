package com.example.bookphoria.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.entities.AuthorEntity
import com.example.bookphoria.data.local.entities.BookAuthorCrossRef
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.BookGenreCrossRef
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.GenreEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBookViewModel @Inject constructor(
    private val bookDao: BookDao
) : ViewModel() {

    var book by mutableStateOf<BookWithGenresAndAuthors?>(null)
        private set

    var allAuthors by mutableStateOf<List<AuthorEntity>>(emptyList())
        private set

    var allGenres by mutableStateOf<List<GenreEntity>>(emptyList())
        private set

    var selectedAuthorIds by mutableStateOf<List<Int>>(emptyList())
    var selectedGenreIds by mutableStateOf<List<Int>>(emptyList())

    var title by mutableStateOf("")
    var publisher by mutableStateOf("")
    var publishedDate by mutableStateOf("")
    var synopsis by mutableStateOf("")
    var isbn by mutableStateOf("")
    var pages by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var serverId by mutableStateOf("")

    var isLoading by mutableStateOf(false)

    fun loadBook(bookId: Int) {
        viewModelScope.launch {
            val bookEntity = bookDao.getBookById(bookId)
            val authors = bookDao.getAllAuthors()
            val genres = bookDao.getAllGenres()

            book = bookEntity
            allAuthors = authors
            allGenres = genres

            bookEntity?.let {
                title = it.book.title
                serverId = it.book.serverId
                publisher = it.book.publisher
                publishedDate = it.book.publishedDate
                synopsis = it.book.synopsis
                isbn = it.book.isbn
                pages = it.book.pages.toString()
                imageUrl = it.book.imageUrl ?: ""

                selectedAuthorIds = it.authors.map { author -> author.id }
                selectedGenreIds = it.genres.map { genre -> genre.id }
            }
        }
    }

    fun updateBook(bookId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val updated = BookEntity(
                id = bookId,
                serverId = serverId,
                title = title,
                publisher = publisher,
                publishedDate = publishedDate,
                synopsis = synopsis,
                isbn = isbn,
                pages = pages.toIntOrNull() ?: 0,
                imageUrl = imageUrl.ifBlank { null }
            )
            bookDao.insertBook(updated)

            // Hapus relasi lama
            bookDao.deleteBookAuthorCrossRefs(bookId)
            bookDao.deleteBookGenreCrossRefs(bookId)

            // Tambah relasi baru
            selectedAuthorIds.forEach {
                bookDao.insertBookAuthorCrossRef(BookAuthorCrossRef(bookId, it))
            }
            selectedGenreIds.forEach {
                bookDao.insertBookGenreCrossRef(BookGenreCrossRef(bookId, it))
            }

            onSuccess()
        }
    }

    fun isValid(): Boolean {
        return title.isNotBlank() &&
                publisher.isNotBlank() &&
                publishedDate.isNotBlank() &&
                pages.isNotBlank() &&
                synopsis.isNotBlank() &&
                selectedAuthorIds.isNotEmpty() &&
                selectedGenreIds.isNotEmpty()
    }
}

