package com.preshan.grocerylist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = StoreTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["store_type_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("normalized_name"),
        Index(value = ["category_id", "is_active"]),
        Index(value = ["store_type_id", "is_active"]),
        Index(value = ["is_favorite", "is_active"]),
        Index("purchase_count")
    ]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "normalized_name") val normalizedName: String,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "store_type_id") val storeTypeId: Long? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "purchase_count") val purchaseCount: Int = 0,
    @ColumnInfo(name = "selected_count") val selectedCount: Int = 0,
    @ColumnInfo(name = "last_selected_at") val lastSelectedAt: Long? = null,
    @ColumnInfo(name = "last_purchased_at") val lastPurchasedAt: Long? = null,
    @ColumnInfo(name = "excluded_from_frequent") val excludedFromFrequent: Boolean = false,
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
