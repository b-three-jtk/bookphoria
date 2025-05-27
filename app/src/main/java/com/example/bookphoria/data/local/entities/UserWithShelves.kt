package com.example.bookphoria.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// Shelves yang dimiliki user
class UserWithShelves(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val shelves: List<ShelfEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId",
        associateBy = Junction(UserFriendCrossRef::class)
    )
    val friends: List<UserEntity>
)