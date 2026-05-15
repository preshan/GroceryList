package com.preshan.grocerylist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.preshan.grocerylist.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE is_active = 1 ORDER BY sort_order ASC, name COLLATE NOCASE ASC")
    suspend fun getActiveCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE is_active = 1 ORDER BY sort_order ASC, name COLLATE NOCASE ASC")
    fun observeActiveCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>): List<Long>

    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("SELECT COALESCE(MAX(sort_order), 0) FROM categories")
    suspend fun getMaxSortOrder(): Int

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM categories
            WHERE is_active = 1
            AND (:excludeId = 0 OR id != :excludeId)
            AND LOWER(TRIM(name)) = LOWER(TRIM(:name))
        )
        """
    )
    suspend fun existsOtherActiveWithSameName(name: String, excludeId: Long): Boolean

    @Query(
        """
        SELECT COUNT(*) FROM items
        WHERE category_id = :categoryId AND is_active = 1
        """
    )
    suspend fun countActiveItemsInCategory(categoryId: Long): Int

    @Query(
        """
        UPDATE categories
        SET is_active = 0, updated_at = :updatedAt
        WHERE id = :id AND is_default = 0 AND is_active = 1
        """
    )
    suspend fun softDeactivateUserCategory(id: Long, updatedAt: Long): Int

    @Query("SELECT id, name FROM categories")
    suspend fun getIdNamePairs(): List<CategoryIdNameRow>

    @Query("SELECT id, name, is_active AS isActive FROM categories")
    suspend fun getAllLookupRows(): List<CategoryLookupRow>

    @Query(
        """
        UPDATE categories
        SET is_active = 1, updated_at = :updatedAt
        WHERE name = :name AND is_default = 1 AND is_active = 0
        """
    )
    suspend fun reactivateDefaultByName(name: String, updatedAt: Long): Int
}

data class CategoryIdNameRow(
    val id: Long,
    val name: String
)

data class CategoryLookupRow(
    val id: Long,
    val name: String,
    val isActive: Boolean
)
