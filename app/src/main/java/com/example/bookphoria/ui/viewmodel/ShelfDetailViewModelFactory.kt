package com.example.bookphoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.dao.ShelfDao

class ShelfDetailViewModelFactory(
    private val shelfDao: ShelfDao,
    private val bookDao: BookDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShelfDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShelfDetailViewModel(shelfDao, bookDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}