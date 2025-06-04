package com.example.bookphoria.data.remote.api

import com.example.bookphoria.data.local.entities.FriendWithUsers
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.remote.requests.FriendRequest
import com.example.bookphoria.data.remote.responses.FriendRequestResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendApiService {
    @POST("friends/request")
    suspend fun sendFriendRequest(@Header("Authorization") token: String, @Body request: FriendRequest): FriendRequestResponse

    @POST("friends/accept/{friendId}")
    suspend fun acceptFriendRequest(@Header("Authorization") token: String, @Path("friendId") friendId: Int): FriendRequestResponse

    @POST("friends/reject/{friendId}")
    suspend fun rejectFriendRequest(@Header("Authorization") token: String, @Path("friendId") friendId: Int): FriendRequestResponse

    @GET("friends")
    suspend fun getAllFriends(@Header("Authorization") token: String): List<UserEntity>

    @GET("friends/{friendId}")
    suspend fun getFriendById(@Header("Authorization") token: String, @Path("friendId") friendId: Int): UserEntity

    @GET("friends/pending")
    suspend fun getFriendRequests(@Header("Authorization") token: String): List<FriendWithUsers>

    @GET("user/{userName}")
    suspend fun getUserByUsername(
        @Header("Authorization") token: String,
        @Path("userName") userName: String
    ): UserWrapperResponse
}

data class UserWrapperResponse(
    @SerializedName("user") val user: UserEntity
)
