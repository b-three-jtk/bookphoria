package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.responses.BookNetworkModel
import com.example.bookphoria.data.remote.responses.BookStatusNetworkModel
import com.example.bookphoria.data.remote.responses.ShelfNetworkModel
import com.example.bookphoria.data.remote.responses.UserNetworkModel
import com.example.bookphoria.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFriendViewModel @Inject constructor(
    private val repository: FriendRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _friendDetail = MutableStateFlow<UserEntity?>(null)
    val friendDetail: StateFlow<UserEntity?> = _friendDetail
    private val _friendBooks = MutableStateFlow<List<BookStatusNetworkModel>>(emptyList())
    val friendBooks: StateFlow<List<BookStatusNetworkModel>> = _friendBooks
    private val _shelfBooks = MutableStateFlow<List<ShelfNetworkModel>>(emptyList())
    val shelfBooks: StateFlow<List<ShelfNetworkModel>> = _shelfBooks
    private val _friends = MutableStateFlow<List<UserNetworkModel>>(emptyList())
    val friends: StateFlow<List<UserNetworkModel>> = _friends
    private val _friendCount = MutableStateFlow(0)
    val friendCount: StateFlow<Int> = _friendCount
    private val _bookCount = MutableStateFlow(0)
    val bookCount: StateFlow<Int> = _bookCount
    private val _listCount = MutableStateFlow(0)
    val listCount: StateFlow<Int> = _listCount
    private val _userId = MutableStateFlow(0)
    val userId: StateFlow<Int> = _userId

    fun getUserId() {
        viewModelScope.launch {
            _userId.value = userPreferences.getUserId().first()!!
        }
    }

    fun getFriendById(friendId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getFriendById(friendId)
                _friendDetail.value = UserEntity(
                    id = 0,
                    username = result.username ?: "",
                    firstName = result.firstName,
                    lastName = result.lastName,
                    email = result.email ?: "",
                    profilePicture = result.avatar
                )
                _friends.value = result.friends ?: emptyList()
                _friendBooks.value = result.books.filter { true } ?: emptyList()
                _shelfBooks.value = result.shelves ?: emptyList()
                _friendCount.value = result.friends.size ?: 0
                _bookCount.value = result.books.size ?: 0
                _listCount.value = result.shelves.size ?: 0
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Error: ${e.message}")
                _friendBooks.value = emptyList()
                _shelfBooks.value = emptyList()
                _friends.value = emptyList()
                _friendCount.value = 0
                _bookCount.value = 0
                _listCount.value = 0
            }
        }
    }
}

fun List<UserNetworkModel>.isContains(userName: String): Boolean {
    return any { it.username == userName }
}