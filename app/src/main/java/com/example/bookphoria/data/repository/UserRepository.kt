package com.example.bookphoria.data.repository

import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.BookWithAuthors
import com.example.bookphoria.data.local.entities.UserWithBooks
import javax.inject.Inject

class UserRepository @Inject constructor (private val userDao: UserDao) {
    suspend fun getBooksWithAuthorsByUser(userId: Int): List<BookWithAuthors> {
        return userDao.getBooksWithAuthorsByUser(userId)
    }
}