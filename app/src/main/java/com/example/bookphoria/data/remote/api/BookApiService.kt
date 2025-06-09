package com.example.bookphoria.data.remote.api

import com.example.bookphoria.data.remote.requests.AddReviewRequest
import com.example.bookphoria.data.remote.requests.AddUserBookRequest
import com.example.bookphoria.data.remote.responses.AddBookResponse
import com.example.bookphoria.data.remote.responses.BookSearchResponse
import com.example.bookphoria.data.remote.responses.EditBookResponse
import com.example.bookphoria.data.remote.responses.ReviewNetworkModel
import com.example.bookphoria.data.remote.responses.UserBookResponse
import com.example.bookphoria.data.remote.responses.UserBookStatusResponse
import com.example.bookphoria.data.remote.responses.WrapperDetailBookNetworkModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {
    @GET("books/search")
    suspend fun getBooksByQuery(@Header("Authorization") authorization: String, @Query("q") query: String, @Query("per_page") perPage: Int,@Query("page") page: Int): BookSearchResponse

    @GET("user/books/{status}")
    suspend fun getYourBooksReading(@Header("Authorization") authorization: String, @Path("status") status: String): UserBookStatusResponse

    @GET("user/books")
    suspend fun getYourBooks(@Header("Authorization") authorization: String): UserBookStatusResponse

    @GET("user/book/{id}")
    suspend fun getYourBook(@Header("Authorization") authorization: String, @Path("id") id: String): UserBookResponse

    @JvmSuppressWildcards
    @Multipart
    @POST("books")
    suspend fun addBook(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("publisher") publisher: RequestBody?,
        @Part("published_date") publishedDate: RequestBody,
        @Part("synopsis") synopsis: RequestBody,
        @Part("isbn") isbn: RequestBody,
        @Part("pages") pages: RequestBody,
        @Part("authors[]") authors: List<RequestBody>,
        @Part("genres[]") genres: List<RequestBody>,
        @Part("user_status") userStatus: RequestBody,
        @Part("user_page_count") userPageCount: RequestBody?,
        @Part("user_start_date") userStartDate: String?,
        @Part("user_finish_date") userFinishDate: String?,
        @Part cover: MultipartBody.Part?
    ): AddBookResponse

    @JvmSuppressWildcards
    @Multipart
    @POST("book/{id}")
    suspend fun editBook(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("publisher") publisher: RequestBody?,
        @Part("published_date") publishedDate: RequestBody,
        @Part("synopsis") synopsis: RequestBody,
        @Part("isbn") isbn: RequestBody,
        @Part("pages") pages: RequestBody,
        @Part("authors[]") authors: List<RequestBody>,
        @Part("genres[]") genres: List<RequestBody>,
        @Part cover: MultipartBody.Part?,
    ): EditBookResponse

    @POST("review")
    suspend fun addReview(
        @Header("Authorization") token: String,
        @Body request: AddReviewRequest
    )

    @GET("reviews/{bookId}")
    suspend fun getReviews(
        @Header("Authorization") token: String,
        @Path("bookId") bookId: String
    ) : List<ReviewNetworkModel>

    @GET("book/{id}")
    suspend fun getBookById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : WrapperDetailBookNetworkModel

    @POST("user/books/update/{id}")
    suspend fun updateStatus(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    )

    @POST("user/books")
    suspend fun addUserBook(
        @Header("Authorization") token: String,
        @Body request: AddUserBookRequest
    )

    @DELETE("user/book/{id}/remove")
    suspend fun removeUserBook(
        @Header("Authorization") token: String,
        @Path("id") id: String
    )
}