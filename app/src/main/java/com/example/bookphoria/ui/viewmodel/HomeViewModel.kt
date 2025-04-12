package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.FullBookDataWithUserInfo
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.repository.AuthRepository
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BookRepository,
    private val userPreferences: UserPreferences,
    private val userRepository: AuthRepository,
) : ViewModel() {

    private val _currentlyReading = MutableStateFlow<List<FullBookDataWithUserInfo>>(emptyList())
    val currentlyReading: StateFlow<List<FullBookDataWithUserInfo>> = _currentlyReading
    private val _yourBooks = MutableStateFlow<List<BookWithGenresAndAuthors>>(emptyList())
    val yourBooks: StateFlow<List<BookWithGenresAndAuthors>> = _yourBooks

    private val _userName = MutableStateFlow("...")
    val userName: StateFlow<String> = _userName

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userPreferences.getUserId().collect { userId ->
                Log.d("HomeViewModel", "User ID: $userId")
                if (userId != null) {
                    val name = userRepository.getUserNameById(userId)
                    Log.d("HomeViewModel", "User name: $name")
                    _userName.value = name

                    launch {
                        repository.getYourCurrentlyReadingBooks(userId, "reading").collect { book ->
                            Log.d("HomeViewModel", "Currently Reading: ${book.size} books")
                            _currentlyReading.value = book
                        }
                    }

                    launch {
                        repository.getYourBooks(userId).collect { book ->
                            Log.d("HomeViewModel", "Your Books: ${book.size} books")
                            _yourBooks.value = book
                        }
                    }

                } else {
                    Log.w("HomeViewModel", "User ID is null")
                }
            }
        }
    }

}
