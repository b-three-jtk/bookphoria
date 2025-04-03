package com.example.bookphoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//@HiltViewModel
//class SearchViewModel @Inject constructor(
//    private val repository: BookRepository
//) : ViewModel() {
//    val searchResult: Flow<PagingData<BookEntity>> = _query
//        .debounce(500)
//        .filter { it.isNotBlank() }
//        .flatMapLatest { repository.searchBooks(it) }
//
//    fun searchBooks(query: String) {
//        _query.value = query
//    }
//}