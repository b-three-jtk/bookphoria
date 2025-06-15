package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.dao.ReadingSummary
import com.example.bookphoria.data.local.entities.FullBookDataWithUserInfo
import com.example.bookphoria.data.local.entities.ReadingLogEntity
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
import java.time.LocalDate
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

    private val _isLoadingBooks = MutableStateFlow(false)
    val isLoadingBooks = _isLoadingBooks

    private val _readingSummary = MutableStateFlow<List<ReadingSummary>>(emptyList())
    val readingSummary: StateFlow<List<ReadingSummary>> = _readingSummary

    fun loadUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val userName = userPreferences.getUserName().firstOrNull() ?: return@launch
            val user = userRepository.getUserByUsername(userName)

            Log.d("HomeViewModel", "User: $user")

            _userName.value = user?.firstName ?: user?.username ?: ""
            _avatar.value = user?.profilePicture ?: ""
        }
    }

    fun loadBooks() {
        viewModelScope.launch {
            val userId = userPreferences.getUserId().first() ?: return@launch

            repository.getYourBooksLocal(userId).collect { books ->
                _yourBooks.value = books
            }

            Log.d("HomeViewModel", "Your Books: ${_yourBooks.value}")
        }
    }

    fun loadCurrentlyReading() {
        viewModelScope.launch {
            val userId = userPreferences.getUserId().first() ?: return@launch

            repository.getCurrentlyReadingLocal(userId, "reading").collect { books ->
                _currentlyReading.value = books
            }
            Log.d("HomeViewModel", "Currently Reading: $_currentlyReading")
        }
    }

    fun loadReadingSummary(startDate: String, endDate: String) {
        viewModelScope.launch {
            val userId = userPreferences.getUserId().first() ?: return@launch
            repository.getReadingSummary(userId, startDate, endDate).collect {
                Log.d("HomeViewModel", "Reading Summary: $it")
                _readingSummary.value = it
            }
        }
    }

//    fun seedDummyLogs() {
//        viewModelScope.launch {
//            val userId = userPreferences.getUserId().firstOrNull() ?: return@launch
//            val bookId = 1
//
//            val today = LocalDate.now()
//            val logs = listOf(
//                ReadingLogEntity(userId, bookId, today.minusDays(6).toString(), 10),
//                ReadingLogEntity(userId, bookId, today.minusDays(5).toString(), 5),
//                ReadingLogEntity(userId, bookId, today.minusDays(4).toString(), 7),
//                ReadingLogEntity(userId, bookId, today.minusDays(3).toString(), 0),
//                ReadingLogEntity(userId, bookId, today.minusDays(2).toString(), 8),
//                ReadingLogEntity(userId, bookId, today.minusDays(1).toString(), 3),
//                ReadingLogEntity(userId, bookId, today.toString(), 12),
//            )
//
//            logs.forEach {
//                repository.insertReadingLog(it)
//            }
//        }
//    }

}

