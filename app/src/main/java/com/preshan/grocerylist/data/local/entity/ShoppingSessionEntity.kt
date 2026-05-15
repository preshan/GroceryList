package com.preshan.grocerylist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_sessions",
    indices = [Index("status")]
)
data class ShoppingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String? = null,
    @ColumnInfo(name = "status") val status: String = "draft",
    @ColumnInfo(name = "group_by") val groupBy: String = "category",
    @ColumnInfo(name = "total_items") val totalItems: Int = 0,
    @ColumnInfo(name = "purchased_items") val purchasedItems: Int = 0,
    @ColumnInfo(name = "started_at") val startedAt: Long? = null,
    @ColumnInfo(name = "completed_at") val completedAt: Long? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
