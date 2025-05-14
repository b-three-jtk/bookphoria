package com.example.bookphoria.data.remote.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ShelfApiServices {
    @Multipart
    @POST("shelves")
    suspend fun createShelf(
        @Part("name") name: RequestBody,
        @Part("desc") description: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<JsonObject>
}