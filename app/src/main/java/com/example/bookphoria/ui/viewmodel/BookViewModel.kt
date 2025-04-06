package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.remote.responses.AddBookRequest
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)

    fun addBookToDatabase(
        request: AddBookRequest,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        Log.d("BookViewModel", "Adding book to database: $request")
        viewModelScope.launch {
            try {
                bookRepository.addBookFromApi(request)
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menambahkan buku: ${e.message}"
                onError()
            }
        }
    }
}
