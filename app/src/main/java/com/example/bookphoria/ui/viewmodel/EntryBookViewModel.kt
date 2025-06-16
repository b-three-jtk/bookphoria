package com.example.bookphoria.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.remote.requests.AddBookRequest
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EntryBookViewModel @Inject constructor(
    private val bookDao: BookDao,
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _errorMessage = MutableStateFlow<String?>(null)

    var coverFile by mutableStateOf<File?>(null)
    var coverUrl by mutableStateOf("")
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

    // Fungsi validasi
    fun validateForm(): List<String> {
        val errors = mutableListOf<String>()
        if (title.isBlank()) errors.add("Judul buku belum diisi")
        if (publisher.isBlank()) errors.add("Penerbit belum diisi")
        if (authors.isEmpty()) errors.add("Penulis belum ditambahkan")
        if (publishedDate.isBlank()) errors.add("Tanggal terbit belum diisi")
        if (pageCount.isBlank()) errors.add("Jumlah halaman belum diisi")
        if (isbn.isBlank()) {
            errors.add("ISBN belum diisi")
        } else if (isbn.length != 11 && isbn.length != 13) {
            errors.add("ISBN harus 11 atau 13 digit")
        }
        if (synopsis.isBlank()) errors.add("Sinopsis belum diisi")
        if (genres.isEmpty()) errors.add("Genre belum ditambahkan")
        return errors
    }

    fun addBookToDatabase(
        onSuccess: (Int) -> Unit,
        onError: (Throwable) -> Unit,
        context: Context
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
                    cover = coverFile,
                    authors = authors.toList(),
                    genres = genres.toList(),
                    userStatus = "owned",
                    userPageCount = 0,
                    userStartDate = null,
                    userFinishDate = null
                )
                Log.d("BookViewModel", "Adding book to database: $request")

                val newBookId = bookRepository.addBookFromApi(
                    request
                )
                onSuccess(newBookId)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menambahkan buku: ${e.message}"
                onError(e)
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