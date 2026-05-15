package com.preshan.grocerylist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.preshan.grocerylist.data.local.entity.ShoppingSessionEntity

@Dao
interface ShoppingSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ShoppingSessionEntity): Long

    @Update
    suspend fun update(session: ShoppingSessionEntity)

    @Query("SELECT * FROM shopping_sessions WHERE status = :status ORDER BY updated_at DESC LIMIT 1")
    suspend fun getLatestByStatus(status: String): ShoppingSessionEntity?

    @Query("SELECT * FROM shopping_sessions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ShoppingSessionEntity?

    @Query(
        """
        UPDATE shopping_sessions
        SET status = 'completed',
            completed_at = :completedAt,
            purchased_items = :purchasedItems,
            total_items = :totalItems,
            updated_at = :updatedAt
        WHERE id = :id AND status IN ('active', 'draft')
        """
    )
    suspend fun markCompleted(
        id: Long,
        purchasedItems: Int,
        totalItems: Int,
        completedAt: Long,
        updatedAt: Long
    )

    @Query(
        """
        UPDATE shopping_sessions
        SET status = 'cancelled',
            updated_at = :updatedAt
        WHERE id = :id AND status = 'active'
        """
    )
    suspend fun markCancelledIfActive(id: Long, updatedAt: Long): Int
}
