package com.preshan.grocerylist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.preshan.grocerylist.data.local.entity.ShoppingSessionItemEntity

@Dao
interface ShoppingSessionItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingSessionItemEntity>)

    @Update
    suspend fun update(item: ShoppingSessionItemEntity)

    @Query("SELECT * FROM shopping_session_items WHERE shopping_session_id = :sessionId ORDER BY sort_order ASC, id ASC")
    suspend fun getBySessionId(sessionId: Long): List<ShoppingSessionItemEntity>

    @Query(
        """
        UPDATE shopping_session_items
        SET is_purchased = :purchased,
            purchased_at = CASE WHEN :purchased = 1 THEN :purchasedAt ELSE NULL END,
            updated_at = :updatedAt
        WHERE shopping_session_id = :sessionId AND item_id = :itemId
        """
    )
    suspend fun updatePurchasedState(
        sessionId: Long,
        itemId: Long,
        purchased: Boolean,
        purchasedAt: Long,
        updatedAt: Long
    )
}
