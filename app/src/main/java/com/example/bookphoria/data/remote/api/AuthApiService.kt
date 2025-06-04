package com.example.bookphoria.data.remote.api

import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.remote.responses.AuthResponse
import com.example.bookphoria.data.remote.responses.EditProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface AuthApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): MessageResponse

    @POST("reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): MessageResponse

    @Multipart
    @POST("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("username") username: RequestBody,
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part avatar: MultipartBody.Part?,
    ): Response<EditProfileResponse>

    @GET("user")
    suspend fun getCurrentUser(): retrofit2.Response<UserStatsResponse>

    @GET("user/profile")
    suspend fun getUserProfile(): UserEntity
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val token: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class MessageResponse(
    val message: String
)

data class AuthResponse(
    val accessToken: String,
    val user: UserResponse
)

data class UserResponse(
    val id: Int,
    val username: String?,
    val email: String
)

data class UserStatsResponse(
    val id: Int,
    val username: String?,
    val email: String,
    val first_name: String?,
    val last_name: String?,
    val profile_picture: String?,
    val book_count: Int,
    val reading_list_count: Int,
    val friend_count: Int
)