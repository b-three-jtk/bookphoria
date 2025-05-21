package com.example.bookphoria.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bookphoria.data.local.dao.BookDao
import com.example.bookphoria.data.local.dao.ShelfDao
import com.example.bookphoria.data.local.dao.UserDao
import com.example.bookphoria.data.local.entities.AuthorEntity
import com.example.bookphoria.data.local.entities.BookAuthorCrossRef
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.BookGenreCrossRef
import com.example.bookphoria.data.local.entities.GenreEntity
import com.example.bookphoria.data.local.entities.ShelfEntity
import com.example.bookphoria.data.local.entities.UserBookCrossRef
import com.example.bookphoria.data.local.entities.UserEntity
import com.example.bookphoria.data.local.entities.UserFriendCrossRef
import kotlinx.coroutines.CoroutineScope

@Database(entities = [
    UserEntity::class,
    BookEntity::class,
    AuthorEntity::class,
    GenreEntity::class,
    ShelfEntity::class,
    BookAuthorCrossRef::class,
    BookGenreCrossRef::class,
    UserBookCrossRef::class], version = 3,
    exportSchema = false
)

abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
    abstract fun ShelfDao(): ShelfDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bookphoria"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Tambahkan kolom baru (nullable, jadi tidak perlu default value)
        database.execSQL("ALTER TABLE users ADD COLUMN firstName TEXT")
        database.execSQL("ALTER TABLE users ADD COLUMN lastName TEXT")

        // Rename 'name' ke 'username' — Room tidak mendukung langsung rename, jadi harus lewat langkah duplikat
        // 1. Buat tabel baru sementara
        database.execSQL("""
            CREATE TABLE users_new (
                id INTEGER PRIMARY KEY NOT NULL,
                username TEXT,
                firstName TEXT,
                lastName TEXT,
                email TEXT NOT NULL,
                profilePicture TEXT
            )
        """.trimIndent())

        // 2. Copy data lama ke tabel baru, anggap 'username' berasal dari kolom lama 'name'
        database.execSQL("""
            INSERT INTO users_new (id, username, firstName, lastName, email, profilePicture)
            SELECT id, name, NULL, NULL, email, profilePicture FROM users
        """.trimIndent())

        // 3. Hapus tabel lama dan ganti nama tabel baru
        database.execSQL("DROP TABLE users")
        database.execSQL("ALTER TABLE users_new RENAME TO users")
    }
}

