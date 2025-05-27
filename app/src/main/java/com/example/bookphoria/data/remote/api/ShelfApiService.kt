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
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("desc") description: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<JsonObject>

    @POST("shelves/{shelf_id}/books")
    suspend fun addBookToShelf(
        @Header("Authorization") token: String,
        @Path("shelf_id") shelfId: String,
        @Body bookData: JsonObject
    ): Response<JsonObject>

    @DELETE("shelves/{shelf_id}/books/{book_id}")
    suspend fun deleteBookFromShelf(
        @Header("Authorization") token: String,
        @Path("shelf_id") shelfId: String,
        @Path("book_id") bookId: String
    ): Response<JsonObject>

    @DELETE("shelves/{shelf_id}")
    suspend fun deleteShelf(
        @Header("Authorization") token: String,
        @Path("shelf_id") shelfId: String
    ): Response<JsonObject>
}