package com.example.bookphoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _selectedBook = MutableStateFlow<BookDetailUIState?>(null)
    val selectedBook: StateFlow<BookDetailUIState?> = _selectedBook

    fun getBookById(bookId: Int) {
        viewModelScope.launch {
            val book = bookRepository.getBookById(bookId)
            if (book != null) {
                _selectedBook.value = BookDetailUIState(
                    book = book.book,
                    author = book.authors.joinToString(", ") { it.name },
                    genres = book.genres.map { it.name },
                    status = book.userBookCrossRefs.firstOrNull()?.status ?: ""
                )
            }
        }
    }

    fun deleteBook() {
        val book = _selectedBook.value?.book
        if (book != null) {
            viewModelScope.launch {
                bookRepository.deleteUserBook(3,book.id)
            }
            _selectedBook.value = null
        }
    }

    data class BookDetailUIState(
        val book: BookEntity,
        val author: String,
        val genres: List<String>,
        val status: String
    )
}
