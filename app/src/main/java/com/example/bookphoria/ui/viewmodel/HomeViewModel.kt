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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    private val _isLoading = MutableStateFlow(false)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadUserProfile()
                loadBooks()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadUserProfile() {
        val userName = userPreferences.getUserName().first()
        val userId = userPreferences.getUserId().first()

        if (userName != null && userId != null) {
            val user = withContext(Dispatchers.IO) {
                userRepository.getUserByUsername(userName)
            }

            _userName.value = user?.firstName ?: user?.username ?: ""
            _avatar.value = user?.profilePicture ?: ""
        }
    }

    private suspend fun loadBooks() {
        val userId = userPreferences.getUserId().first() ?: return

        val remoteBooks = withContext(Dispatchers.IO) {
            repository.getYourBooksRemote(userId)
        }

        Log.d("HomeViewModel", "Remote Books: $remoteBooks")

        withContext(Dispatchers.IO) {
            repository.saveBooksToLocal(remoteBooks)
        }

        viewModelScope.launch {
            repository.getYourBooksLocal(userId).collect { books ->
                _yourBooks.value = books
                Log.d("HomeViewModel", "Your Books: $books")
            }
        }

        viewModelScope.launch {
            repository.getCurrentlyReadingLocal(userId, "reading").collect { books ->
                _currentlyReading.value = books
            }
        }
    }
}