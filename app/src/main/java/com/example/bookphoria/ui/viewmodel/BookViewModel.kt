package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.UserBookCrossRef
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.responses.ReviewNetworkModel
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _selectedBook = MutableStateFlow<BookDetailUIState?>(null)
    val selectedBook: StateFlow<BookDetailUIState?> = _selectedBook

    private val _readingProgress = MutableStateFlow<Int?>(null)
    val readingProgress: StateFlow<Int?> = _readingProgress.asStateFlow()
    private val _reviews = MutableStateFlow<List<ReviewNetworkModel>>(emptyList())
    val reviews: StateFlow<List<ReviewNetworkModel>> = _reviews.asStateFlow()

    private val _bookStatus = MutableStateFlow<String?>(null)
    val bookStatus: StateFlow<String?> = _bookStatus.asStateFlow()

    private val _statusUpdateSuccess = MutableStateFlow(false)
    val statusUpdateSuccess: StateFlow<Boolean> = _statusUpdateSuccess.asStateFlow()

    private val _reviewUpdateSuccess = MutableStateFlow(false)
    val reviewUpdateSuccess: StateFlow<Boolean> = _reviewUpdateSuccess.asStateFlow()

    fun getBookById(bookId: Int) {
        viewModelScope.launch {
            val bookWithRelations = bookRepository.getBookById(bookId)
            Log.d("BookViewModel", "Book with relations: $bookWithRelations")
            if (bookWithRelations != null) {
                _selectedBook.value = BookDetailUIState(
                    book = bookWithRelations.book,
                    author = bookWithRelations.authors.joinToString(", ") { it.name },
                    genres = bookWithRelations.genres.map { it.name }
                )
            }
        }
    }

    fun getReadingProgress(bookId: Int) {
        viewModelScope.launch {
            val userId = userPreferences.getUserId().first() ?: return@launch
            val progress = bookRepository.getReadingProgress(userId, bookId)
            _readingProgress.value = progress
        }
    }

    fun updateReadingProgress(bookId: Int, pagesRead: Int) {
        viewModelScope.launch {
            val userId = userPreferences.getUserId().first() ?: return@launch
            val book = selectedBook.value?.book
            val status = if (book != null && pagesRead >= book.pages) "owned" else "reading"

            bookRepository.updateReadingProgress(
                UserBookCrossRef(
                    userId = userId,
                    bookId = bookId,
                    status = status,
                    pagesRead = pagesRead,
                    startDate = null,
                    endDate = if (status == "owned") LocalDate.now().toString() else null
                )
            )
            _readingProgress.value = pagesRead
            _bookStatus.value = "reading"
        }
    }

    fun addReview(bookId: Int, desc: String, rate: Int) {
        viewModelScope.launch {
            Log.d("BookViewModel", "Adding review: bookId=$bookId, desc=$desc, rate=$rate")
            bookRepository.addReview(bookId, desc, rate)
            _reviewUpdateSuccess.value = true
        }
    }

    fun getReviews(bookId: Int) {
        viewModelScope.launch {
            try {
                val reviews = bookRepository.getReviews(bookId)
                _reviews.value = reviews
                Log.d("BookViewModel", "Reviews: $reviews")
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching reviews: ${e.message}")
            }
        }
    }

    fun getBookStatus(bookId: Int) {
        viewModelScope.launch {
            try {
                val userId = userPreferences.getUserId().first() ?: return@launch
                val status = bookRepository.getBookStatus(userId, bookId)
                _bookStatus.value = status
            } catch (e: Exception) {
                _bookStatus.value = "none"
            }
        }
    }

    fun updateBookStatus(bookId: Int, newStatus: String) {
        viewModelScope.launch {
            try {
                val userId = userPreferences.getUserId().first() ?: return@launch
                _readingProgress.value?.let {
                    bookRepository.updateBookStatus(userId, bookId, newStatus,
                        it
                    )
                }
                _bookStatus.value = newStatus
                _statusUpdateSuccess.value = true
            } catch (e: Exception) {
                _statusUpdateSuccess.value = false
            }
        }
    }

    fun deleteUserBook(bookId: Int) {
        viewModelScope.launch {
            try {
                bookRepository.deleteUserBook(bookId)
                _statusUpdateSuccess.value = true
            } catch (e: Exception) {
                _statusUpdateSuccess.value = false
            }
        }
    }

    fun updateStatusUpdateSuccess(success: Boolean) {
        _statusUpdateSuccess.value = success
    }

    fun updateStatusReview(success: Boolean) {
        _reviewUpdateSuccess.value = success
    }

    data class BookDetailUIState(
        val book: BookEntity,
        val author: String,
        val genres: List<String>
    )
}
