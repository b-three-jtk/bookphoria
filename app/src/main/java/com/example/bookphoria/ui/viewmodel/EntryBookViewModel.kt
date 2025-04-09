package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.remote.responses.AddBookRequest
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryBookViewModel @Inject constructor(
    private val bookDao: BookDao,
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _errorMessage = MutableStateFlow<String?>(null)

    var coverUrl by mutableStateOf<String?>(null)
    var title by mutableStateOf("")
    val authors =  mutableStateListOf<String>()
    var authorInput by mutableStateOf("")
    var publisher by mutableStateOf("")
    var publishedDate by mutableStateOf("")
    var isbn by mutableStateOf("")
    var pageCount by mutableStateOf("")
    var synopsis by mutableStateOf("")
    val genres = mutableStateListOf<String>()
    var genreInput by mutableStateOf("")

    fun addBookToDatabase(
        onSuccess: (Int) -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = AddBookRequest(
                    title = title,
                    publisher = publisher,
                    publishedDate = publishedDate,
                    synopsis = synopsis,
                    isbn = isbn,
                    pages = pageCount.toInt(),
                    cover = coverUrl ?: "", // Handle null case
                    authors = authors.toList(),
                    genres = genres.toList(),
                    userStatus = "owned",
                    userPageCount = 0,
                    userStartDate = null,
                    userFinishDate = null
                )
                Log.d("BookViewModel", "Adding book to database: $request")

                val newBookId = bookRepository.addBookFromApi(request)
                onSuccess(newBookId)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menambahkan buku: ${e.message}"
                onError()
            }
        }
    }

    fun isValid(): Boolean {
        return title.isNotBlank() &&
                publisher.isNotBlank() &&
                publishedDate.isNotBlank() &&
                pageCount.isNotBlank() &&
                synopsis.isNotBlank() &&
                genres.isNotEmpty() &&
                authors.isNotEmpty()
    }
}