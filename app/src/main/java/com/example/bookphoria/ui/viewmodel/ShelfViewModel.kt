package com.example.bookphoria.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.ShelfApiServices
import com.example.bookphoria.data.repository.ShelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val shelfRepository: ShelfRepository
) : ViewModel() {

    fun createShelf(
        name: String,
        description: String?,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                shelfRepository.createShelf(
                    name = name,
                    description = description,
                    imageUri = imageUri
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error tidak diketahui")
            }
        }
    }
}