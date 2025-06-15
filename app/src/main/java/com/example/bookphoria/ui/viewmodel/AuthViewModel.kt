package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.repository.AuthRepository
import com.example.bookphoria.data.repository.BookRepository
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
    private val bookRepository: BookRepository,
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
        val validationErrors = validateLoginForm(email, password)
        if (validationErrors.isNotEmpty()) {
            onError(validationErrors.joinToString(", ") { it })
            return
        }

        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)

                if (result != null) {
                    if (rememberMe) {
                        userPreferences.saveCredentials(email, password)
                    } else {
                        userPreferences.clearCredentials()
                    }

                    val remoteBooks = bookRepository.getYourBooksRemote(result.id)
                    Log.d("Login", "Remote Books: $remoteBooks")
                    bookRepository.saveBooksToLocal(remoteBooks)

                    onSuccess()
                } else {
                    onError("Login gagal")
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Error during login: ${e.message}")
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
            try {
                _isLoading.value = true
                val validationErrors = validateLoginForm(email, password) +
                        listOfNotNull(if (name.isBlank()) "Username belum diisi" else null)
                if (validationErrors.isNotEmpty()) {
                    onError(validationErrors.joinToString(", "))
                    _isLoading.value = false
                    return@launch
                }

                val result = authRepository.register(name, email, password)
                result.onSuccess {
                    onSuccess()
                }.onFailure { exception ->
                    onError(exception.message ?: "Registrasi gagal")
                }
            } catch (e: Exception) {
                Log.e("RegisterError", "Error during register: ${e.message}")
                onError(e.message ?: "Registrasi gagal")
            } finally {
                _isLoading.value = false
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

    fun getSavedCredentials(): Flow<Pair<String?, String?>> {
        return userPreferences.getSavedCredentials()
    }

    fun validateLoginForm(email: String, password: String): List<String> {
        val errors = mutableListOf<String>()
        if (email.isBlank()) errors.add("Email belum diisi")
        else if (!isValidEmail(email)) errors.add("Email tidak valid")
        if (password.isBlank()) errors.add("Password belum diisi")
        else if (!isValidPassword(password)) errors.add("Password tidak valid")
        return errors
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([a-zA-Z0-9.-]+\\.)+[a-zA-Z]{2,}$".toRegex()
        val allowedDomains = listOf(
            "gmail.com", "yahoo.com", "outlook.com", "hotmail.com",
            "aol.com", "icloud.com", "protonmail.com", "polban.ac.id"
        )
        if (!email.matches(emailRegex)) return false
        val domain = email.substringAfter("@")
        return allowedDomains.contains(domain) || domain.matches("^[a-zA-Z0-9.-]+\\.edu\\.[a-zA-Z]{2,}$".toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[0-9])(?=.*[!@#\$%^&*])(?=.*[A-Za-z]).{8,}$".toRegex()
        return password.matches(passwordRegex)
    }
}