package com.example.bookphoria.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.entities.AuthorEntity
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.data.local.entities.GenreEntity
import com.example.bookphoria.data.remote.requests.EditBookRequest
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditBookViewModel @Inject constructor(
    val bookRepository: BookRepository
) : ViewModel() {

    var book by mutableStateOf<BookWithGenresAndAuthors?>(null)
        private set

    var allAuthors by mutableStateOf<List<AuthorEntity>>(emptyList())
        private set

    var allGenres by mutableStateOf<List<GenreEntity>>(emptyList())
        private set

    var selectedAuthorIds by mutableStateOf<List<String>>(emptyList())
    var selectedGenreIds by mutableStateOf<List<String>>(emptyList())

    var bookNetworkId by mutableStateOf("")
    var title by mutableStateOf("")
    var publisher by mutableStateOf("")
    var publishedDate by mutableStateOf("")
    var synopsis by mutableStateOf("")
    var isbn by mutableStateOf("")
    var pages by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var imageFile by mutableStateOf<File?>(null)
    var serverId by mutableStateOf("")

    fun validateForm(): List<String> {
        val errors = mutableListOf<String>()
        if (title.isBlank()) errors.add("Judul buku belum diisi")
        if (publisher.isBlank()) errors.add("Penerbit belum diisi")
        if (publishedDate.isBlank()) errors.add("Tanggal terbit belum diisi")
        if (pages.isBlank()) errors.add("Jumlah halaman belum diisi")
        if (isbn.isBlank()) errors.add("ISBN belum diisi")
        if (synopsis.isBlank()) errors.add("Sinopsis belum diisi")
        if (selectedAuthorIds.isEmpty()) errors.add("Penulis belum dipilih")
        if (selectedGenreIds.isEmpty()) errors.add("Genre belum dipilih")
        return errors
    }

    fun loadBook(bookId: Int) {
        viewModelScope.launch {
            val bookEntity = bookRepository.getBookById(bookId)
            val authors = bookRepository.getAllAuthors()
            val genres = bookRepository.getAllGenres()

            allAuthors = authors
            allGenres = genres

            bookEntity?.let {
                bookNetworkId = it.book.serverId
                title = it.book.title
                serverId = it.book.serverId
                publisher = it.book.publisher
                publishedDate = it.book.publishedDate
                synopsis = it.book.synopsis
                isbn = it.book.isbn
                pages = it.book.pages.toString()
                imageUrl = it.book.imageUrl ?: ""

                selectedAuthorIds = it.authors.map { author -> author.serverId }
                selectedGenreIds = it.genres.map { genre -> genre.serverId }
            }

            if (bookEntity != null) {
                Log.d("EditBookViewModel", "Selected Author IDs: ${bookEntity.authors}")
            }
        }
    }

    fun updateBook(bookNetworkId: String, onSuccess: () -> Unit, onError: (String?) -> Unit = {}) {
        viewModelScope.launch {
            try {
                // Validasi pages sebelum konversi
                val pageCount = pages.toIntOrNull() ?: run {
                    onError("Jumlah halaman tidak valid")
                    return@launch
                }

                val request = EditBookRequest(
                    id = bookNetworkId,
                    title = title,
                    publisher = publisher,
                    publishedDate = publishedDate,
                    synopsis = synopsis,
                    isbn = isbn,
                    pages = pageCount,
                    cover = imageFile,
                    authors = selectedAuthorIds.toList(),
                    genres = selectedGenreIds.toList(),
                )
                Log.d("EditBookViewModel", "Request: $request")

                // Panggil repository
                bookRepository.updateBook(request)
                onSuccess() // Jika tidak ada eksepsi, anggap sukses
            } catch (e: Exception) {
                Log.e("EditBookViewModel", "Error updating book", e)
                onError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun isValid(): Boolean {
        return title.isNotBlank() &&
                publisher.isNotBlank() &&
                publishedDate.isNotBlank() &&
                pages.isNotBlank() &&
                synopsis.isNotBlank() &&
                selectedAuthorIds.isNotEmpty() &&
                selectedGenreIds.isNotEmpty()
    }
}

