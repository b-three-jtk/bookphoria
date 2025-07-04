package com.example.bookphoria.data.remote.api

import com.example.bookphoria.data.remote.responses.updateShelfResponse
import com.example.bookphoria.data.remote.responses.AddBookToShelfResponse
import com.example.bookphoria.data.remote.responses.AddShelfResponse
import com.example.bookphoria.data.remote.responses.BookIdRequest
import com.example.bookphoria.data.remote.responses.ShelfDetailNetworkModel
import com.example.bookphoria.data.remote.responses.ShelfResponse
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
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
    ): AddShelfResponse

    @POST("shelves/{shelf_id}/books")
    suspend fun addBookToShelf(
        @Header("Authorization") token: String,
        @Path("shelf_id") shelfId: String,
        @Body bookId: BookIdRequest
    ): AddBookToShelfResponse

    @DELETE("shelves/{shelf_id}/books/{book_id}")
    suspend fun deleteBookFromShelf(
        @Header("Authorization") token: String,
        @Path("shelf_id") shelfId: String,
        @Path("book_id") bookId: String
    ): ShelfResponse

    @DELETE("shelves/{shelf_id}")
    suspend fun deleteShelf(
        @Header("Authorization") token: String,
        @Path("shelf_id") shelfId: String
    ): AddBookToShelfResponse

    @Multipart
    @POST("shelves/update/{shelf_id}")
    suspend fun updateShelf(
        @Header("Authorization") token: String,
        @Path("shelf_id") shelfId: String,
        @Part("name") name: RequestBody,
        @Part("desc") description: RequestBody?,
        @Part image: MultipartBody.Part?
    ): updateShelfResponse

    @GET("shelves/{shelf_id}")
    suspend fun getShefById(
        @Header("Authorization") token: String,
        @Path("shelf_id") shelfId: String
    ): ShelfDetailNetworkModel

}
