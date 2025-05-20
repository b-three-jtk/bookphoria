package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.FriendWithUsers
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val repository: FriendRepository,
) : ViewModel() {
    private val _friends = mutableStateOf<List<UserEntity>>(emptyList())
    val friends: State<List<UserEntity>> get() = _friends
    private val _friendRequest = mutableStateOf<List<FriendWithUsers>>(emptyList())
    val friendRequest: State<List<FriendWithUsers>> get() = _friendRequest
    private val _friendDetail = mutableStateOf<UserEntity?>(null)
    val friendDetail: State<UserEntity?> = _friendDetail
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFriends() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAllFriends()
                _friends.value = result
                Log.d("FriendViewModel", "Friends: ${result.size}")
            } catch (e: Exception) {
                Log.e("FriendViewModel", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun List<UserEntity>.isContains(userName: String): Boolean {
        return any { it.username == userName }
    }

    fun loadRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAllPendingFriendRequests()
                _friendRequest.value = result
                Log.d("FriendViewModel", "FriendsReq: ${result.size}")
            } catch (e: Exception) {
                Log.e("FriendViewModel", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getFriendById(friendId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getFriendById(friendId)
                _friendDetail.value = result
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Error: ${e.message}")
            }
        }
    }

    fun sendFriendRequest(
        userName: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.sendFriendRequest(userName)
                onSuccess()
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Error: ${e.message}")
                onError(e)
            }
        }
    }

    fun approveRequest(
        friendId: Int,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.acceptFriendRequest(friendId)
                loadRequests()
                onSuccess()
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Error: ${e.message}")
                onError(e)
            }
        }
    }

    fun rejectRequest(
        friendId: Int,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.rejectFriendRequest(friendId)
                loadRequests()
                onSuccess()
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Error: ${e.message}")
                onError(e)
            }
        }
    }

    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            try {
                val result = repository.getUserByUsername(username)
                _friendDetail.value = result
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Error: ${e.message}")
            }
        }
    }
}