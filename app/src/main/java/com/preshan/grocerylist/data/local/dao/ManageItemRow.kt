package com.preshan.grocerylist.data.local.dao

import androidx.room.ColumnInfo

/** Active catalogue row for manage-items UI (name, category, favorite). */
data class ManageItemRow(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean
)
