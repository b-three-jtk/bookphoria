package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.DeepLinkHolder.token
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.dao.ShelfDao
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.ShelfWithBooks
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.responses.ShelfDetailNetworkModel
import com.example.bookphoria.data.repository.ShelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ShelfDetailNetworkViewModel @Inject constructor(
    private val repository: ShelfRepository
) : ViewModel() {

    private val _shelfWithBooks = MutableStateFlow<ShelfDetailNetworkModel?>(null)
    val shelfWithBooks: StateFlow<ShelfDetailNetworkModel?> = _shelfWithBooks

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private var _pageFinished = MutableStateFlow(0)
    val pageFinished: StateFlow<Int> = _pageFinished

    suspend fun loadShelf(shelfId: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val shelfWithBooks = repository.getShelfById(shelfId)
                _shelfWithBooks.value = shelfWithBooks
            } catch (e: Exception) {
                Log.e("ShelfDetailViewModel", "Error loading shelf: ${e.message}")
            }
        }
    }

    suspend fun getBookById(bookId: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val book = repository.getBookById(bookId)
                _pageFinished.value = book?.pagesRead!!
            } catch (e: Exception) {
                Log.e("ShelfDetailViewModel", "Error loading shelf: ${e.message}")
            }
        }
    }
}