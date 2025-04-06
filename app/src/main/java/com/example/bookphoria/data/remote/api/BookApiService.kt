package com.example.bookphoria.data.remote.api

import com.example.bookphoria.data.remote.responses.AddBookRequest
import com.example.bookphoria.data.remote.responses.AddBookResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BookApiService {
//    @GET("books")
//    suspend fun getBooks(): List<BookNetworkModel>

//    @GET("books/search")
//    suspend fun getBookByIsbn(@Body request: val isbn: String): BookResponse
//
//    @GET("books/search")
//    suspend fun getBooksByQuery(@Query("q") query: String): BookSearchResponse

    @POST("books")
    suspend fun addBook(@Header("Authorization") authorization: String, @Body book: AddBookRequest): AddBookResponse
}