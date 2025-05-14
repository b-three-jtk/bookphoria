package com.example.bookphoria.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.ShelfApiServices
import com.example.bookphoria.data.repository.ShelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val repository: ShelfRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShelfUiState>(ShelfUiState.Idle)
    val uiState: StateFlow<ShelfUiState> = _uiState

    fun createShelf(name: String, desc: String?, imageUri: Uri?) {
        _uiState.value = ShelfUiState.Loading

        viewModelScope.launch {
            repository.createShelf(name, desc, imageUri)
                .onSuccess {
                    _uiState.value = ShelfUiState.Success
                }
                .onFailure { e ->
                    _uiState.value = ShelfUiState.Error(
                        message = when {
                            e.message?.contains("Network error") == true ->
                                "No internet connection"

                            e.message?.contains("Server error") == true ->
                                "Server problem: ${e.message?.substringAfter("Server error: ")}"

                            else -> "Failed to save: ${e.message}"
                        }
                    )
                }
        }
    }

    fun resetState() {
        _uiState.value = ShelfUiState.Idle
    }
}

sealed class ShelfUiState {
    object Idle : ShelfUiState()
    object Loading : ShelfUiState()
    object Success : ShelfUiState()
    data class Error(val message: String) : ShelfUiState()
}