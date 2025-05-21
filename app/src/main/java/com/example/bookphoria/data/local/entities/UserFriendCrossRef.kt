package com.example.bookphoria.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(tableName = "user_friends", primaryKeys = ["userId", "friendId"])
data class UserFriendCrossRef(
    val userId: Int,
    val friendId: Int,
    val isApproved: Boolean
)

data class UserWithFriends(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = UserFriendCrossRef::class,
            parentColumn = "userId",
            entityColumn = "friendId"
        )
    )
    val friends: List<UserEntity>
)

data class FriendWithUsers(
    @Embedded val friend: UserFriendCrossRef,
    @Relation(
        parentColumn = "friendId",
        entityColumn = "id"
    )
    val user: UserEntity
)