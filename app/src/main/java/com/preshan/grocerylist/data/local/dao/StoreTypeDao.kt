package com.preshan.grocerylist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.preshan.grocerylist.data.local.entity.StoreTypeEntity

@Dao
interface StoreTypeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(storeTypes: List<StoreTypeEntity>): List<Long>

    @Query("SELECT id, name FROM store_types")
    suspend fun getIdNamePairs(): List<StoreTypeIdNameRow>

    @Query("SELECT id, name, is_active AS isActive FROM store_types")
    suspend fun getAllLookupRows(): List<StoreTypeLookupRow>

    @Query(
        """
        UPDATE store_types
        SET is_active = 1, updated_at = :updatedAt
        WHERE name = :name AND is_default = 1 AND is_active = 0
        """
    )
    suspend fun reactivateDefaultByName(name: String, updatedAt: Long): Int

    @Query(
        """
        SELECT * FROM store_types
        WHERE is_active = 1
        ORDER BY sort_order ASC, name COLLATE NOCASE ASC
        """
    )
    suspend fun getActiveStoreTypes(): List<StoreTypeEntity>
}

data class StoreTypeIdNameRow(
    val id: Long,
    val name: String
)

data class StoreTypeLookupRow(
    val id: Long,
    val name: String,
    val isActive: Boolean
)
