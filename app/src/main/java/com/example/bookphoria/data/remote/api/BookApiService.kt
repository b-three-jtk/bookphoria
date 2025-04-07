package com.example.bookphoria.data.remote.api

import com.example.bookphoria.data.remote.responses.AddBookRequest
import com.example.bookphoria.data.remote.responses.AddBookResponse
import com.example.bookphoria.data.remote.responses.BookSearchResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BookApiService {
//    @GET("books")
//    suspend fun getBooks(): List<BookNetworkModel>

//    @GET("books/search")
//    suspend fun getBookByIsbn(@Body request: val isbn: String): BookResponse
//
    @GET("books/search")
    suspend fun getBooksByQuery(@Header("Authorization") authorization: String, @Query("q") query: String, @Query("per_page") perPage: Int,@Query("page") page: Int): BookSearchResponse

    @POST("books")
    suspend fun addBook(@Header("Authorization") token: String, @Body book: AddBookRequest): AddBookResponse
}