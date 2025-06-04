package com.example.bookphoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.BookWithAuthors
import com.example.bookphoria.data.local.entities.ShelfWithBooks
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.repository.AuthRepository
import com.example.bookphoria.data.repository.ShelfRepository
import com.example.bookphoria.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class MyShelfViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val shelfRepository: ShelfRepository,
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _booksWithAuthors = MutableStateFlow<List<BookWithAuthors>>(emptyList())
    val booksWithAuthors: StateFlow<List<BookWithAuthors>> = _booksWithAuthors

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId.asStateFlow()

    private val _userId = MutableStateFlow<Int?>(null)

    private val _shelvesWithBooks = MutableStateFlow<List<ShelfWithBooks>>(emptyList())
    val shelvesWithBooks: StateFlow<List<ShelfWithBooks>> = _shelvesWithBooks.asStateFlow()

    init {
        viewModelScope.launch {
            val uid = userPreferences.getUserId().first()
            _userId.value = uid
            val currentUid = authRepository.getCurrentUserId()
            _currentUserId.value = currentUid
            uid?.let {
                shelfRepository.getAllShelvesWithBooks(it).collect { shelves ->
                    _shelvesWithBooks.value = shelves
                }
            }
        }
    }

    fun loadUserBooks() {
        viewModelScope.launch {
            val uid = userPreferences.getUserId().first()
            uid?.let {
                _booksWithAuthors.value = userRepository.getBooksWithAuthorsByUser(uid)
            }
        }
    }
}