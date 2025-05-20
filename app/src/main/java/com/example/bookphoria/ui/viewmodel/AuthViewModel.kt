package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.repository.AuthRepository
import com.example.bookphoria.ui.helper.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _resetPasswordState = MutableStateFlow<Result<String>?>(null)
    private val _forgotPasswordState = MutableStateFlow<Result<String>?>(null)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val resetPasswordState: StateFlow<Result<String>?> = _resetPasswordState.asStateFlow()
    val forgotPasswordState: StateFlow<Result<String>?> = _forgotPasswordState.asStateFlow()
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError
    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError
    val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedIn()

    fun isUserLoggedIn(): Flow<Boolean> {
        return userPreferences.isLoggedIn()
    }

    fun login(
        email: String,
        password: String,
        rememberMe: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        validateLoginInput(email, password)

        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)

                if (result.isSuccess) {
                    if (rememberMe) {
                        userPreferences.saveCredentials(email, password)
                    } else {
                        userPreferences.clearCredentials()
                    }
                    onSuccess()
                } else {
                    onError(result.exceptionOrNull()?.message ?: "Login gagal")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Login gagal")
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

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.forgotPassword(email)
                _forgotPasswordState.value = result
            } catch (e: Exception) {
                _forgotPasswordState.value = Result.failure(e)
                Log.e("ForgotPassword", "Error: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(token: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.resetPassword(token, email, password, confirmPassword)
            _resetPasswordState.value = result
            _isLoading.value = false
        }
    }

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

    fun getSavedCredentials(): Flow<Pair<String?, String?>> {
        return userPreferences.getSavedCredentials()
    }

    private fun validateLoginInput(email: String, password: String) {
        val emailError = InputValidator.validateEmail(email)
        val passwordError = InputValidator.validatePassword(password)
        _emailError.value = emailError
        _passwordError.value = passwordError
    }

}