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
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val repository: ShelfRepository,
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShelfUiState>(ShelfUiState.Idle)
    val uiState: StateFlow<ShelfUiState> = _uiState
    val localShelves: Flow<List<ShelfEntity>> = database.ShelfDao().getAllShelves()


    fun createShelf(name: String, desc: String?, imageUri: Uri?) {
        _uiState.value = ShelfUiState.Loading

        viewModelScope.launch {
            repository.createShelf(name, desc, imageUri)
                .onSuccess {
                    _uiState.value = ShelfUiState.Success
                }
                .onFailure { e ->
                    _uiState.value = when {
                        e.message?.contains("Tidak dapat", ignoreCase = true) == true ->
                            ShelfUiState.Error(e.message ?: "Gambar tidak valid")
                        e.message?.contains("Izin", ignoreCase = true) == true ->
                            ShelfUiState.Error("Izin akses gambar diperlukan")
                        e.message?.contains("Network error", ignoreCase = true) == true ->
                            ShelfUiState.Error("Koneksi internet bermasalah")
                        e.message?.contains("Authentication", ignoreCase = true) == true ->
                            ShelfUiState.Error("Silakan login kembali")
                        else -> ShelfUiState.Error("Gagal membuat shelf: ${e.message ?: "Error tidak diketahui"}")
                    }
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