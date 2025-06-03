package com.example.bookphoria.data.remote.api

import com.example.bookphoria.data.local.entities.UserEntity
import retrofit2.http.GET

interface UserApiService {
    @GET("user/profile")
    suspend fun getUserProfile(): UserEntity
}