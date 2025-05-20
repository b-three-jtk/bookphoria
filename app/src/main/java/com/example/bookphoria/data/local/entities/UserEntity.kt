package com.example.bookphoria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    @SerializedName("username")
    val username: String?,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("email")
    val email: String,
    @SerializedName("avatar")
    val profilePicture: String? = null
)
