package com.preshan.grocerylist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_session_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["shopping_session_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("shopping_session_id"),
        Index("item_id")
    ]
)
data class ShoppingSessionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "shopping_session_id") val shoppingSessionId: Long,
    @ColumnInfo(name = "item_id") val itemId: Long,
    @ColumnInfo(name = "item_name_snapshot") val itemNameSnapshot: String,
    @ColumnInfo(name = "category_id_snapshot") val categoryIdSnapshot: Long,
    @ColumnInfo(name = "store_type_id_snapshot") val storeTypeIdSnapshot: Long? = null,
    @ColumnInfo(name = "is_selected") val isSelected: Boolean = true,
    @ColumnInfo(name = "is_purchased") val isPurchased: Boolean = false,
    @ColumnInfo(name = "sort_order") val sortOrder: Int = 0,
    @ColumnInfo(name = "purchased_at") val purchasedAt: Long? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
