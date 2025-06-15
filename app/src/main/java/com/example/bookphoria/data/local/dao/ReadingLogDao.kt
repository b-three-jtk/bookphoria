package com.example.bookphoria.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookphoria.data.local.entities.ReadingLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingLogDao {

    @Query("""
        SELECT date, SUM(pagesRead) as totalPages
        FROM ReadingLogEntity
        WHERE userId = :userId AND date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
    """)
    fun getPagesReadPerDay(userId: Int, startDate: String, endDate: String): Flow<List<ReadingSummary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ReadingLogEntity)


}

data class ReadingSummary(
    val date: String,
    val totalPages: Int
)
