package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.FullBookDataWithUserInfo
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.repository.AuthRepository
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BookRepository,
    private val userPreferences: UserPreferences,
    private val userRepository: AuthRepository,
) : ViewModel() {
    private val _currentlyReading = MutableStateFlow<List<FullBookDataWithUserInfo>>(emptyList())
    val currentlyReading: StateFlow<List<FullBookDataWithUserInfo>> = _currentlyReading

    private val _yourBooks = MutableStateFlow<List<FullBookDataWithUserInfo>>(emptyList())
    val yourBooks: StateFlow<List<FullBookDataWithUserInfo>> = _yourBooks

    private val _userName = MutableStateFlow("...")
    val userName: StateFlow<String> = _userName

    private val _avatar = MutableStateFlow("...")
    val avatar: StateFlow<String> = _avatar

    fun loadUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val userName = userPreferences.getUserName().firstOrNull() ?: return@launch
            val user = userRepository.getUserByUsername(userName)

            Log.d("HomeViewModel", "User: $user")

            withContext(Dispatchers.Main) {
                _userName.value = user?.firstName ?: user?.username ?: ""
                _avatar.value = user?.profilePicture ?: ""
            }
        }
    }

    fun loadBooks() {
        viewModelScope.launch {
            val userId = userPreferences.getUserId().first() ?: return@launch

            repository.getYourBooksLocal(userId).collect { books ->
                _yourBooks.value = books
            }

            repository.getCurrentlyReadingLocal(userId, "reading").collect { books ->
                _currentlyReading.value = books
            }
        }
    }
}