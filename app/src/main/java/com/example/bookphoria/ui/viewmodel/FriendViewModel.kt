package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.FriendWithUsers
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.remote.api.UserWrapperResponse
import com.example.bookphoria.data.remote.responses.FriendRequestResponse
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
    private val _friendRequest = MutableStateFlow<List<FriendWithUsers>>(emptyList())
    val friendRequest: StateFlow<List<FriendWithUsers>> = _friendRequest
    private val _friendSearchDetail = mutableStateOf<UserWrapperResponse?>(null)
    val friendSearchDetail: State<UserWrapperResponse?> = _friendSearchDetail
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFriends() {
        viewModelScope.launch {
            _friends.value = emptyList()
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

    fun loadRequests() {
        viewModelScope.launch {
            _friendRequest.value = emptyList()
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

    fun removeRequestLocally(userId: Int) {
        _friendRequest.value = _friendRequest.value.filterNot { it.user.id == userId }
    }

    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            try {
                val result = repository.getUserByUsername(username)
                Log.d("FriendViewModel", "Result User: $result")
                _friendSearchDetail.value = result
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Error: ${e.message}")
            }
        }
    }
}