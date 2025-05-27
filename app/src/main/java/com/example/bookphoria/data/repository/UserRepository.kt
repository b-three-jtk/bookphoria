package com.example.bookphoria.data.repository

import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.BookWithAuthors
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.entities.UserWithBooks
import com.example.bookphoria.data.local.entities.toBookEntity
import com.example.bookphoria.data.remote.api.UserApiService
import com.example.bookphoria.data.remote.responses.BookNetworkModel
import javax.inject.Inject

class UserRepository @Inject constructor (private val userDao: UserDao) {
    suspend fun getBooksWithAuthorsByUser(userId: Int): List<BookWithAuthors> {
        return userDao.getBooksWithAuthorsByUser(userId)
    }

    suspend fun getUserById(userId: Int): UserEntity? {
        return userDao.getUserById(userId)
    }

}