package com.preshan.grocerylist.data.repository

import androidx.room.withTransaction
import com.preshan.grocerylist.data.local.database.AppDatabase

/**
 * Wipes all rows from this app's Room database. Does not touch files outside the app database.
 */
class LocalDataRepository(
    private val database: AppDatabase
) {
    suspend fun clearAllLocalData() {
        database.withTransaction {
            database.clearAllTables()
        }
    }
}
