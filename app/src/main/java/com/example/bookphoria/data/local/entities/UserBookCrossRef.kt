package com.example.bookphoria.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["userId", "bookId"])
data class UserBookCrossRef(
    val userId: Int,
    val bookId: Int,
    val status: String,
    val pagesRead: Int,
    val startDate: String?,
    val endDate: String?
)

data class UserWithBooks(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id", // ini kolom dari UserEntity
        entityColumn = "id", // ini kolom dari BookEntity
        associateBy = Junction(
            value = UserBookCrossRef::class,
            parentColumn = "userId", // kolom penghubung ke UserEntity
            entityColumn = "bookId"  // kolom penghubung ke BookEntity
        )
    )
    val books: List<BookEntity>
)
