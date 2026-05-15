package com.preshan.grocerylist.data.repository

import androidx.room.withTransaction
import com.preshan.grocerylist.data.local.database.AppDatabase
import com.preshan.grocerylist.data.local.entity.ShoppingSessionEntity
import com.preshan.grocerylist.data.local.entity.ShoppingSessionItemEntity
import com.preshan.grocerylist.ui.navigation.GroceryItem
import java.util.Locale

class ShoppingSessionRepository(
    private val database: AppDatabase
) {

    suspend fun createActiveSessionWithItems(selectedItemIds: List<Long>): Long {
        if (selectedItemIds.isEmpty()) return -1L
        return database.withTransaction {
            val now = System.currentTimeMillis()
            val entities = database.itemDao().getActiveByIds(selectedItemIds)
            val ordered = selectedItemIds.mapNotNull { id -> entities.find { it.id == id } }
            if (ordered.isEmpty()) return@withTransaction -1L

            val sessionId = database.shoppingSessionDao().insert(
                ShoppingSessionEntity(
                    title = null,
                    status = "active",
                    groupBy = "category",
                    totalItems = ordered.size,
                    purchasedItems = 0,
                    startedAt = now,
                    completedAt = null,
                    createdAt = now,
                    updatedAt = now
                )
            )

            val categoryNames = database.categoryDao().getAllLookupRows().associateBy { it.id }
            val lines = ordered.mapIndexed { index, item ->
                ShoppingSessionItemEntity(
                    shoppingSessionId = sessionId,
                    itemId = item.id,
                    itemNameSnapshot = item.name,
                    categoryIdSnapshot = item.categoryId,
                    categoryNameSnapshot = categoryNames[item.categoryId]?.name.orEmpty(),
                    storeTypeIdSnapshot = item.storeTypeId,
                    isSelected = true,
                    isPurchased = false,
                    sortOrder = index,
                    purchasedAt = null,
                    createdAt = now,
                    updatedAt = now
                )
            }
            database.shoppingSessionItemDao().insertAll(lines)
            sessionId
        }
    }

    suspend fun setLinePurchased(sessionId: Long, itemId: Long, purchased: Boolean) {
        val now = System.currentTimeMillis()
        database.shoppingSessionItemDao().updatePurchasedState(
            sessionId = sessionId,
            itemId = itemId,
            purchased = purchased,
            purchasedAt = now,
            updatedAt = now
        )
    }

    suspend fun completeSessionAndApplyItemStats(
        sessionId: Long,
        purchasedItemIds: Set<Long>,
        allSelectedItemIds: List<Long>,
        totalItemCount: Int,
        purchasedCount: Int
    ) {
        val now = System.currentTimeMillis()
        database.withTransaction {
            database.shoppingSessionDao().markCompleted(
                id = sessionId,
                purchasedItems = purchasedCount,
                totalItems = totalItemCount,
                completedAt = now,
                updatedAt = now
            )
            if (allSelectedItemIds.isNotEmpty()) {
                database.itemDao().incrementSelectedForIds(allSelectedItemIds, now)
            }
            val purchasedList = purchasedItemIds.toList()
            if (purchasedList.isNotEmpty()) {
                database.itemDao().incrementPurchaseForIds(purchasedList, now)
            }
        }
    }

    suspend fun cancelActiveSessionIfNeeded(sessionId: Long) {
        val now = System.currentTimeMillis()
        database.shoppingSessionDao().markCancelledIfActive(sessionId, now)
    }

    suspend fun getSessionItems(sessionId: Long): List<ShoppingSessionItemEntity> =
        database.shoppingSessionItemDao().getBySessionId(sessionId)

    /**
     * Builds shopping UI/share data from persisted session snapshots (not live catalogue rows).
     */
    suspend fun getSessionItemsGroupedForDisplay(sessionId: Long): Map<String, List<GroceryItem>> {
        val lines = getSessionItems(sessionId)
        if (lines.isEmpty()) return emptyMap()
        val grouped = linkedMapOf<String, MutableList<GroceryItem>>()
        for (line in lines) {
            val category = line.categoryNameSnapshot.ifBlank { "—" }
            val item = GroceryItem(
                id = line.itemId,
                name = line.itemNameSnapshot,
                category = category,
                normalizedName = line.itemNameSnapshot.trim().lowercase(Locale.getDefault())
            )
            grouped.getOrPut(category) { mutableListOf() }.add(item)
        }
        return grouped.mapValues { (_, items) -> items.toList() }
    }
}
