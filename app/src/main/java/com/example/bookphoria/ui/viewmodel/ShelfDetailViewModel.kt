package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.dao.ShelfDao
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.ShelfWithBooks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelfDetailViewModel @Inject constructor(
    private val shelfDao: ShelfDao,
    private val bookDao: BookDao,
) : ViewModel() {

    private val _shelfWithBooks = MutableStateFlow<ShelfWithBooks?>(null)
    val shelfWithBooks: StateFlow<ShelfWithBooks?> = _shelfWithBooks

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun loadShelfWithBooks(userId: Int, shelfId: Int) {
        _loadingState.value = true
        viewModelScope.launch {
            try {
                shelfDao.getShelvesWithBooks(userId, shelfId).collect { shelf ->
                    _shelfWithBooks.value = shelf
                    _loadingState.value = false
                }
            } catch (e: Exception) {
                _errorState.value = "Failed to load shelf: ${e.message}"
                _loadingState.value = false
                Log.e("ShelfDetailVM", "Error loading shelf", e)
            }
        }
    }

    suspend fun getBookById(bookId: Int): BookWithGenresAndAuthors? {
        return try {
            val bookStrId = bookDao.getBookServerIdById(bookId)
            bookDao.getBookById(bookStrId)
        } catch (e: Exception) {
            Log.e("ShelfDetailVM", "Error getting book by id", e)
            null
        }
    }

    suspend fun getReadingProgress(userId: Int, bookId: Int): Boolean {
        return try {
            val pagesRead = bookDao.getReadingProgress(userId, bookId) ?: 0
            val bookStrId = bookDao.getBookServerIdById(bookId)
            val bookWithDetails = bookDao.getBookById(bookStrId)
            val totalPages = bookWithDetails?.book?.pages ?: 0

            // Jika total pages 0 atau tidak ada, anggap belum selesai
            if (totalPages <= 0) {
                false
            } else {
                pagesRead >= totalPages
            }
        } catch (e: Exception) {
            Log.e("ShelfDetailVM", "Error getting reading progress", e)
            false
        }
    }

    suspend fun getBookAuthor(bookId: Int): String {
        return try {
            val bookStrId = bookDao.getBookServerIdById(bookId)
            val bookWithDetails = bookDao.getBookById(bookStrId)
            bookWithDetails?.authors?.joinToString(", ") ?: "Unknown Author"
        } catch (e: Exception) {
            Log.e("ShelfDetailVM", "Error getting book author", e)
            "Unknown Author"
        }
    }

    fun clearError() {
        _errorState.value = null
    }

    fun refreshShelf(userId: Int, shelfId: Int) {
        loadShelfWithBooks(userId, shelfId)
    }
}