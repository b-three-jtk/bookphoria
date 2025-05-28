package com.example.bookphoria.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.repository.AuthRepository
import com.example.bookphoria.data.repository.BookRepository
import com.example.bookphoria.data.repository.FriendRepository
import com.example.bookphoria.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userData = MutableStateFlow<UserEntity?>(null)
    val userData: StateFlow<UserEntity?> = _userData

    private val _bookCount = MutableStateFlow(0)
    val bookCount: StateFlow<Int> = _bookCount

    private val _readingListCount = MutableStateFlow(0)
    val readingListCount: StateFlow<Int> = _readingListCount

    private val _friendCount = MutableStateFlow(0)
    val friendCount: StateFlow<Int> = _friendCount

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchUserData() // Fetch user data when ViewModel is initialized
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val userId = authRepository.getCurrentUserId()
                Log.d("ProfileViewModel", "Fetched userId: $userId")
                if (userId != null) {
                    val user = userRepository.getUserById(userId)
                    Log.d("ProfileViewModel", "Fetched user: id=${user?.id}, username=${user?.username}, email=${user?.email}")
                    _userData.value = user
                } else {
                    _error.value = "User not logged in"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load user data: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun getProfile(username: String, onSuccess: (UserEntity) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.getUserByUsername(username)
            result?.let {
                onSuccess(it)
            } ?: onError(Exception("Profile not found"))
        }
    }

    fun editProfile(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        avatar: File?,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.editProfile(username, firstName, lastName, email, avatar)
                .collect { result ->
                    result.onSuccess { onSuccess() }
                        .onFailure { onError(it) }
                }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.logout()
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Logout failed: ${e.message}"
            }
        }
    }
}