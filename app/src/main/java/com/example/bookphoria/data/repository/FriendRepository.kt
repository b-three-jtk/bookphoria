package com.example.bookphoria.data.repository

import android.util.Log
import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.FriendWithUsers
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.entities.UserWithFriends
import com.example.bookphoria.data.local.preferences.UserPreferences
import com.example.bookphoria.data.remote.api.FriendApiService
import com.example.bookphoria.data.remote.api.UserWrapperResponse
import com.example.bookphoria.data.remote.requests.FriendRequest
import com.example.bookphoria.data.remote.responses.DetailFriendResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FriendRepository @Inject constructor(
    private val apiService: FriendApiService,
    private val userPreferences: UserPreferences,
) {
    suspend fun acceptFriendRequest(friendId: Int): Boolean {
        val accessToken = userPreferences.getAccessToken().first()

        if (accessToken != null) {
            try {
                val response = apiService.acceptFriendRequest("Bearer $accessToken", friendId)

                if (response.message == "Friend request accepted.") {
                    return true
                } else {
                    return false
                }
            } catch (e: Exception) {
                throw Exception("Terjadi kesalahan saat menerima permintaan pertemanan: ${e.message}")
            }
        } else {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }
    }

    suspend fun rejectFriendRequest(friendId: Int): Boolean {
        val accessToken = userPreferences.getAccessToken().first()

        if (accessToken != null) {
            try {
                val response = apiService.rejectFriendRequest("Bearer $accessToken", friendId)

                if (response.message == "Friend request rejected.") {
                    return true
                } else {
                    return false
                }
            } catch (e: Exception) {
                throw Exception("Terjadi kesalahan saat menolak permintaan pertemanan: ${e.message}")
            }
        } else {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }
    }

    suspend fun getAllFriends(): List<UserEntity> {
        val accessToken = userPreferences.getAccessToken().first()
        Log.d("FriendRepository", "Access Token: $accessToken")

        if (accessToken != null) {
            try {
                return apiService.getAllFriends("Bearer $accessToken")
            } catch (e: Exception) {
                throw Exception("Terjadi kesalahan saat mengambil daftar pertemanan: ${e.message}")
            }
        } else {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }
    }

    suspend fun getAllPendingFriendRequests(): List<FriendWithUsers> {
        val accessToken = userPreferences.getAccessToken().first()
        Log.d("FriendRepository", "Access Token: $accessToken")

        if (accessToken != null) {
            try {
                return apiService.getFriendRequests("Bearer $accessToken")
            } catch (e: Exception) {
                throw Exception("Terjadi kesalahan saat mengambil daftar permintaan pertemanan: ${e.message}")
            }
        } else {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }
    }

    suspend fun getFriendById(friendId: Int): DetailFriendResponse {
        val accessToken = userPreferences.getAccessToken().first()

        if (accessToken != null) {
            try {
                return apiService.getFriendById("Bearer $accessToken", friendId)
            } catch (e: Exception) {
                throw Exception("Terjadi kesalahan saat mengambil data teman: ${e.message}")
            }
        } else {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }
    }

    suspend fun sendFriendRequest(friendId: String): Boolean {
        val accessToken = userPreferences.getAccessToken().first()

        if (accessToken != null) {
            try {
                val response = apiService.sendFriendRequest("Bearer $accessToken", FriendRequest(friendId))

                if (response.message == "Friend request sent.") {
                    return true
                } else {
                    return false
                }
            } catch (e: Exception) {
                throw Exception("Terjadi kesalahan saat mengirim permintaan pertemanan: ${e.message}")
            }
        } else {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }
    }

    suspend fun getUserByUsername(username: String): UserWrapperResponse {
        val accessToken = userPreferences.getAccessToken().first()

        if (accessToken != null) {
            try {
                val data = apiService.getUserByUsername("Bearer $accessToken", username)
                Log.d("FriendRepository", "Data User: $data")
                return data
            } catch (e: Exception) {
                throw Exception("Terjadi kesalahan saat mengambil data pengguna: ${e.message}")
            }
        } else {
            throw Exception("Token tidak ditemukan. Mohon login ulang.")
        }
    }
}