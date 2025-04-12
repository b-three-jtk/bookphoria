package com.example.bookphoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.bookphoria.data.local.preferences.SearchPreferences
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchHistoryManager: SearchPreferences,
    private val repository: BookRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory


    init {
        viewModelScope.launch {
            searchHistoryManager.searchHistory.collect { history ->
                _searchHistory.value = history
            }
        }
    }
    val searchResults = _searchQuery
        .debounce(300)
        .filter { it.isNotEmpty() }
        .flatMapLatest { query ->
            flow {
                val token = userPreferences.getAccessToken().first() ?: ""
                emitAll(repository.searchBook(query, token))
            }
        }
        .cachedIn(viewModelScope)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addToHistory(query: String) {
        viewModelScope.launch {
            if (query.isNotBlank()) {
                searchHistoryManager.addSearchQuery(query)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryManager.clearSearchHistory()
        }
    }
}