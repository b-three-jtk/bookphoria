package com.example.bookphoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.dao.ShelfDao
import com.example.bookphoria.data.repository.ShelfRepository

class ShelfDetailViewModelFactory(
    private val shelfDao: ShelfDao,
    private val bookDao: BookDao,
    private val repository: ShelfRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShelfDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShelfDetailViewModel(shelfDao, bookDao, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}