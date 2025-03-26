package com.example.bookphoria.data.repository

import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.remote.api.UserApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: UserApiService,
    private val userDao: UserDao
) {


}