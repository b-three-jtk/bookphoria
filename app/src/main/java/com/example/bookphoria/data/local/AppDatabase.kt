package com.example.bookphoria.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.UserEntity
import kotlinx.coroutines.CoroutineScope

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bookphoria"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}