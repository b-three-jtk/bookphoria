package com.example.bookphoria.ui.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.BookWithAuthors
import com.example.bookphoria.data.local.entities.UserWithBooks
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyShelfViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _booksWithAuthors = MutableStateFlow<List<BookWithAuthors>>(emptyList())
    val booksWithAuthors: StateFlow<List<BookWithAuthors>> = _booksWithAuthors

    private var userId: Int? = null

    init {
        viewModelScope.launch {
            userId = userPreferences.getUserId().first()
            userId?.let {
                loadUserBooks()
            }
        }
    }

    fun loadUserBooks() {
        viewModelScope.launch {
            userId?.let {
                _booksWithAuthors.value = userRepository.getBooksWithAuthorsByUser(it)
            }
        }
    }
}