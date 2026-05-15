package com.preshan.grocerylist.data.repository

import com.preshan.grocerylist.ui.navigation.GroceryItem
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun observeActiveCatalogueGrouped(): Flow<Map<String, List<GroceryItem>>>
    suspend fun seedDefaultsIfNeeded()
}
