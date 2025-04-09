package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.remote.responses.AddBookRequest
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
            val bookWithRelations = bookRepository.getBookById(bookId)
            if (bookWithRelations != null) {
                _selectedBook.value = BookDetailUIState(
                    book = bookWithRelations.book,
                    author = bookWithRelations.authors.joinToString(", ") { it.name },
                    genres = bookWithRelations.genres.map { it.name }
                )
            }
        }
    }

    data class BookDetailUIState(
        val book: BookEntity,
        val author: String,
        val genres: List<String>
    )
}
