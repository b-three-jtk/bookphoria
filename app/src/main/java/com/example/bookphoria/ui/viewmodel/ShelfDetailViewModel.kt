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
import com.example.bookphoria.data.repository.ShelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ShelfDetailViewModel @Inject constructor(
    private val shelfDao: ShelfDao,
    private val bookDao: BookDao,
    private val repository: ShelfRepository
) : ViewModel() {

    private val _shelfWithBooks = MutableStateFlow<ShelfWithBooks?>(null)
    val shelfWithBooks: StateFlow<ShelfWithBooks?> = _shelfWithBooks

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _addBookResult = MutableStateFlow<Result<Boolean>?>(null)
    val addBookResult: StateFlow<Result<Boolean>?> = _addBookResult

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _deleteResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteResult: StateFlow<Result<Unit>?> = _deleteResult

    fun deleteShelf(shelfId: Int) {
        viewModelScope.launch {
            try {
                val success = repository.deleteShelf(shelfId)
                if (success) {
                    _deleteResult.value = Result.success(Unit)
                } else {
                    _deleteResult.value = Result.failure(Exception("Failed to delete shelf"))
                    _errorState.value = "Gagal menghapus rak buku"
                }
            } catch (e: Exception) {
                _deleteResult.value = Result.failure(e)
                _errorState.value = "Error menghapus rak: ${e.message}"
                Log.e("ShelfDetailVM", "Error deleting shelf: ${e.message}")
            }
        }
    }

    fun resetDeleteResult() {
        _deleteResult.value = null
    }

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

    suspend fun updateShelf(
        name: String? = null,
        desc: String? = null,
        imageUri: String? = null,
        imageFile: File? = null
    ) {
        try {
            if (name != null) {
                _shelfWithBooks.value!!.shelf.serverId?.let {
                    repository.updateShelf(name, desc, imageUri, imageFile,
                        it
                    )
                }

                _shelfWithBooks.value!!.shelf.name = name
                _shelfWithBooks.value!!.shelf.description = desc
                _shelfWithBooks.value!!.shelf.imagePath = imageUri
                _shelfWithBooks.value = _shelfWithBooks.value
            }
        } catch (e: Exception) {
            Log.e("ShelfDetailVM", "Error updating shelf", e)
        }
    }

    fun clearError() {
        _errorState.value = null
    }

    fun addBookToShelf(shelfId: Int, bookId: Int) {
        viewModelScope.launch {
            try {
                val success = repository.addBookToShelf(shelfId, bookId)
                if (success) {
                    _addBookResult.value = Result.success(true)
                    _shelfWithBooks.value?.shelf?.let { shelf ->
                        loadShelfWithBooks(shelf.userId, shelf.id)
                    }
                } else {
                    _addBookResult.value = Result.failure(Throwable("Failed to add book"))
                    _errorState.value = "Gagal menambahkan buku ke rak"
                }
            } catch (e: Exception) {
                _addBookResult.value = Result.failure(e)
                _errorState.value = "Error menambahkan buku: ${e.message}"
                Log.e("ShelfDetailVM", "Error adding book: ${e.message}")
            }
        }
    }

    fun resetAddBookResult() {
        _addBookResult.value = null
    }

    fun refreshShelf(userId: Int, shelfId: Int) {
        loadShelfWithBooks(userId, shelfId)
    }
}