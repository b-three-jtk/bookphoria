package com.example.bookphoria.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.AppDatabase
import com.example.bookphoria.data.local.entities.ShelfEntity
import com.example.bookphoria.data.repository.ShelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val repository: ShelfRepository,
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShelfUiState>(ShelfUiState.Idle)
    val uiState: StateFlow<ShelfUiState> = _uiState
    val localShelves: Flow<List<ShelfEntity>> = database.ShelfDao().getAllShelves()

    fun createShelf(name: String, desc: String?, imageUri: String?, imageFile: File?) {
        _uiState.value = ShelfUiState.Loading // Set ke Loading
        viewModelScope.launch {
            try {
                repository.createShelf(name, desc, imageUri, imageFile)
                _uiState.value = ShelfUiState.Success // Set ke Success setelah sukses
            } catch (e: Exception) {
                _uiState.value = ShelfUiState.Error(e.message ?: "Gagal membuat shelf") // Set ke Error jika gagal
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