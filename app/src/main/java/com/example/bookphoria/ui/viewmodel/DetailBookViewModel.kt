package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.responses.BookNetworkModel
import com.example.bookphoria.data.remote.responses.ReviewNetworkModel
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailBookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val userPreferences: UserPreferences,
    private val bookDao: BookDao
) : ViewModel() {

    private val _selectedBook = MutableStateFlow<BookNetworkModel?>(null)
    val selectedBook: StateFlow<BookNetworkModel?> = _selectedBook
    private val _reviews = MutableStateFlow<List<ReviewNetworkModel>>(emptyList())
    val reviews: StateFlow<List<ReviewNetworkModel>> = _reviews.asStateFlow()
    private val _bookStatus = MutableStateFlow<String?>(null)
    val bookStatus: StateFlow<String?> = _bookStatus.asStateFlow()
    private val _statusUpdateSuccess = MutableStateFlow(false)
    val statusUpdateSuccess: StateFlow<Boolean> = _statusUpdateSuccess.asStateFlow()

    fun getBookById(bookId: String) {
        viewModelScope.launch {
            val bookWithRelations = bookRepository.getBookNetworkById(bookId)
            _selectedBook.value = bookWithRelations
        }
    }

    fun getReviews(bookId: String) {
        viewModelScope.launch {
            try {
                val reviews = bookRepository.getReviewsNetwork(bookId = bookId)
                _reviews.value = reviews
            } catch (e: Exception) {
                Log.e("DetailBookViewModel", "Error fetching reviews: ${e.message}")
            }
        }
    }

    fun getBookStatus(bookId: String) {
        viewModelScope.launch {
            try {
                val userId = userPreferences.getUserId().first() ?: return@launch
                val bookLocalId = bookDao.getBookIdByServerId(bookId)
                Log.d("DetailBookViewModel", "Book local ID: $bookId")
                val status = bookLocalId?.let { bookRepository.getBookStatus(userId, it) }
                Log.d("DetailBookViewModel", "Book status: $status")
                _bookStatus.value = status
            } catch (e: Exception) {
                _bookStatus.value = "none"
            }
        }
    }

    fun updateBookStatus(bookId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                val userId = userPreferences.getUserId().first() ?: return@launch

                bookRepository.updateNewBookStatus(userId, bookId, newStatus)
                _bookStatus.value = newStatus
                _statusUpdateSuccess.value = true
            } catch (e: Exception) {
                _statusUpdateSuccess.value = false
            }
        }
    }

    fun updateStatusUpdateSuccess(success: Boolean) {
        _statusUpdateSuccess.value = success
    }

    fun deleteUserBook(bookId: String) {
        viewModelScope.launch {
            try {
                val bookLocalId = bookDao.getBookIdByServerId(bookId)
                if (bookLocalId != null) {
                    bookRepository.deleteUserBook(bookLocalId)
                }
                _statusUpdateSuccess.value = true
            } catch (e: Exception) {
                _statusUpdateSuccess.value = false
            }
        }
    }
}
