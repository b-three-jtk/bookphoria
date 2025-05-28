package com.example.bookphoria.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.repository.AuthRepository
import com.example.bookphoria.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun logout(onLogoutSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.logout()
            result.onSuccess {
                onLogoutSuccess()
            }.onFailure {
                onError(it)
            }
        }
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
}
