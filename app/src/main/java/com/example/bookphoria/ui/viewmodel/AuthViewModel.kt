package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("Login", "Login attempt with email: $email")
                val result = authRepository.login(email, password)

                // Log result untuk melihat apa yang diterima dari repository
                Log.d("Login", "Result: $result")

                if (result.isSuccess) {
                    onSuccess()
                } else {
                    Log.e("LoginError", "Login failed with message: ${result.exceptionOrNull()?.message}")
                    onError(result.exceptionOrNull()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Error during login: ${e.message}")
                onError(e.message ?: "Login failed")
            }
        }
    }


    fun register(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.register(name, email, password)
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                onError(exception.message ?: "Registration failed")
            }
        }
    }
}