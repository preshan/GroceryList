package com.preshan.grocerylist.data.repository

import com.preshan.grocerylist.data.local.dao.ItemDao
import com.preshan.grocerylist.data.seed.DatabaseSeeder
import com.preshan.grocerylist.ui.navigation.GroceryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CatalogRepositoryImpl(
    private val itemDao: ItemDao,
    private val seeder: DatabaseSeeder
) : CatalogRepository {

    override fun observeActiveCatalogueGrouped(): Flow<Map<String, List<GroceryItem>>> {
        return itemDao.observeActiveItemsWithCategory().map { rows ->
            rows.groupBy(
                keySelector = { it.categoryName },
                valueTransform = { row ->
                    GroceryItem(
                        id = row.id,
                        name = row.name,
                        category = row.categoryName,
                        normalizedName = row.normalizedName,
                        isFavorite = row.isFavorite,
                        purchaseCount = row.purchaseCount
                    )
                }
            )
        }
    }

    override suspend fun seedDefaultsIfNeeded() {
        seeder.seedIfNeeded()
    }
}
