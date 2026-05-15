package com.preshan.grocerylist.data.local.dao

import androidx.room.ColumnInfo

data class ItemWithCategoryRow(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "normalized_name") val normalizedName: String,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean,
    @ColumnInfo(name = "purchase_count") val purchaseCount: Int
)
