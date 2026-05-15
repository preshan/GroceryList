package com.preshan.grocerylist.data.local.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                AppDatabase.DB_NAME
            )
                .addMigrations(*AppDatabase.ALL_MIGRATIONS)
                .build()
                .also { instance = it }
        }
    }
}
