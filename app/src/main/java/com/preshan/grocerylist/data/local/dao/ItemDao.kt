package com.preshan.grocerylist.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.preshan.grocerylist.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<ItemEntity>): List<Long>

    @Insert
    suspend fun insertItem(item: ItemEntity): Long

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Query(
        """
        SELECT i.id, i.name, c.name AS category_name, i.is_favorite
        FROM items i
        INNER JOIN categories c ON c.id = i.category_id AND c.is_active = 1
        WHERE i.is_active = 1
        ORDER BY c.sort_order ASC, c.name COLLATE NOCASE ASC, i.name COLLATE NOCASE ASC
        """
    )
    fun observeManageItemRows(): Flow<List<ManageItemRow>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ItemEntity?

    @Query(
        """
        UPDATE items
        SET is_active = 0, updated_at = :updatedAt
        WHERE id = :itemId AND is_active = 1
        """
    )
    suspend fun softDeactivateItem(itemId: Long, updatedAt: Long)

    @Query(
        """
        UPDATE items
        SET is_active = 0, updated_at = :updatedAt
        WHERE id IN (:ids) AND is_active = 1
        """
    )
    suspend fun softDeactivateItems(ids: List<Long>, updatedAt: Long): Int

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM items
            WHERE category_id = :categoryId
              AND normalized_name = :normalizedName
              AND is_active = 1
              AND (:excludeId = 0 OR id != :excludeId)
        )
        """
    )
    suspend fun existsActiveDuplicateInCategory(
        categoryId: Long,
        normalizedName: String,
        excludeId: Long
    ): Boolean

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM items
            WHERE category_id = :categoryId
              AND normalized_name = :normalizedName
        )
        """
    )
    suspend fun existsAnyInCategory(
        categoryId: Long,
        normalizedName: String
    ): Boolean

    @Query(
        """
        UPDATE items
        SET is_active = 1, updated_at = :updatedAt
        WHERE category_id = :categoryId
          AND normalized_name = :normalizedName
          AND is_default = 1
          AND is_active = 0
        """
    )
    suspend fun reactivateDefaultInCategory(
        categoryId: Long,
        normalizedName: String,
        updatedAt: Long
    ): Int

    @Query(
        """
        SELECT i.id, i.name, i.normalized_name, c.name AS category_name,
               i.is_favorite AS is_favorite, i.purchase_count AS purchase_count
        FROM items i
        INNER JOIN categories c ON c.id = i.category_id
        WHERE i.is_active = 1 AND c.is_active = 1
        ORDER BY c.sort_order ASC, c.name COLLATE NOCASE ASC, i.name COLLATE NOCASE ASC
        """
    )
    fun observeActiveItemsWithCategory(): Flow<List<ItemWithCategoryRow>>

    @Query("SELECT * FROM items WHERE id IN (:ids) AND is_active = 1")
    suspend fun getActiveByIds(ids: List<Long>): List<ItemEntity>

    @Query(
        """
        UPDATE items
        SET selected_count = selected_count + 1,
            last_selected_at = :ts,
            updated_at = :ts
        WHERE id IN (:ids)
        """
    )
    suspend fun incrementSelectedForIds(ids: List<Long>, ts: Long)

    @Query(
        """
        UPDATE items
        SET purchase_count = purchase_count + 1,
            last_purchased_at = :ts,
            updated_at = :ts
        WHERE id IN (:ids)
        """
    )
    suspend fun incrementPurchaseForIds(ids: List<Long>, ts: Long)

    @Query(
        """
        UPDATE items
        SET is_favorite = :favorite,
            updated_at = :updatedAt
        WHERE id = :itemId AND is_active = 1
        """
    )
    suspend fun updateFavorite(itemId: Long, favorite: Boolean, updatedAt: Long)

    @Query(
        """
        SELECT id FROM items
        WHERE is_active = 1
          AND excluded_from_frequent = 0
          AND purchase_count >= 3
        """
    )
    suspend fun getFrequentActiveItemIds(): List<Long>

    @Query(
        """
        UPDATE items
        SET purchase_count = 0,
            selected_count = 0,
            last_selected_at = NULL,
            last_purchased_at = NULL,
            updated_at = :updatedAt
        WHERE is_active = 1
        """
    )
    suspend fun resetFrequentItemData(updatedAt: Long)

    @Query(
        """
        SELECT c.name AS category_name, i.name AS item_name, i.is_favorite, i.purchase_count,
               i.selected_count, i.last_selected_at, i.last_purchased_at, i.is_active
        FROM items i
        INNER JOIN categories c ON c.id = i.category_id
        ORDER BY c.sort_order ASC, c.name COLLATE NOCASE ASC, i.name COLLATE NOCASE ASC
        """
    )
    suspend fun getAllItemsForCsvExport(): List<ItemExportRow>

    @Query(
        """
        SELECT * FROM items
        WHERE category_id = :categoryId AND normalized_name = :normalizedName
        ORDER BY is_active DESC, id DESC
        LIMIT 1
        """
    )
    suspend fun getItemForCsvMerge(categoryId: Long, normalizedName: String): ItemEntity?
}

data class ItemExportRow(
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean,
    @ColumnInfo(name = "purchase_count") val purchaseCount: Int,
    @ColumnInfo(name = "selected_count") val selectedCount: Int,
    @ColumnInfo(name = "last_selected_at") val lastSelectedAt: Long?,
    @ColumnInfo(name = "last_purchased_at") val lastPurchasedAt: Long?,
    @ColumnInfo(name = "is_active") val isActive: Boolean
)
