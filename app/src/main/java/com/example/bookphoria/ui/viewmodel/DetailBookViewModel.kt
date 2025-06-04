package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.remote.responses.BookNetworkModel
import com.example.bookphoria.data.remote.responses.ReviewNetworkModel
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailBookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
) : ViewModel() {

    private val _selectedBook = MutableStateFlow<BookNetworkModel?>(null)
    val selectedBook: StateFlow<BookNetworkModel?> = _selectedBook
    private val _reviews = MutableStateFlow<List<ReviewNetworkModel>>(emptyList())
    val reviews: StateFlow<List<ReviewNetworkModel>> = _reviews.asStateFlow()

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
}
