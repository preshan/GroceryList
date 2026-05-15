package com.preshan.grocerylist.data.seed

import com.preshan.grocerylist.data.AppSettingKeys
import com.preshan.grocerylist.data.local.dao.AppSettingDao
import com.preshan.grocerylist.data.local.dao.CategoryDao
import com.preshan.grocerylist.data.local.dao.ItemDao
import com.preshan.grocerylist.data.local.dao.StoreTypeDao
import com.preshan.grocerylist.data.local.entity.AppSettingEntity
import com.preshan.grocerylist.data.local.entity.CategoryEntity
import com.preshan.grocerylist.data.local.entity.ItemEntity
import com.preshan.grocerylist.data.local.entity.StoreTypeEntity
import java.util.Locale

class DatabaseSeeder(
    private val categoryDao: CategoryDao,
    private val storeTypeDao: StoreTypeDao,
    private val itemDao: ItemDao,
    private val appSettingDao: AppSettingDao
) {

    data class RestoreDefaultsResult(
        val categoriesInserted: Int,
        val categoriesReactivated: Int,
        val storeTypesInserted: Int,
        val storeTypesReactivated: Int,
        val itemsInserted: Int,
        val itemsReactivated: Int
    )

    suspend fun seedIfNeeded() {
        val onboardingDone =
            appSettingDao.getByKey(AppSettingKeys.FIRST_LAUNCH_COMPLETED)?.value == "true"
        if (!onboardingDone) return

        val existingSeed = appSettingDao.getByKey(DefaultSeedData.SEED_VERSION_KEY)
        if (existingSeed?.value == DefaultSeedData.SEED_VERSION_VALUE) return

        val seedItems = activeDefaultItems()

        val now = System.currentTimeMillis()

        val existingCategories = categoryDao.getAllLookupRows().associateBy { it.name }
        val categoriesToInsert = DefaultSeedData.categories
            .filter { existingCategories[it.name] == null }
            .map {
                CategoryEntity(
                    name = it.name,
                    sortOrder = it.sortOrder,
                    isDefault = true,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )
            }
        if (categoriesToInsert.isNotEmpty()) {
            categoryDao.insertAll(categoriesToInsert)
        }

        val existingStoreTypes = storeTypeDao.getAllLookupRows().associateBy { it.name }
        val storeTypesToInsert = DefaultSeedData.storeTypes
            .filter { existingStoreTypes[it.name] == null }
            .map {
                StoreTypeEntity(
                    name = it.name,
                    sortOrder = it.sortOrder,
                    isDefault = true,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )
            }
        if (storeTypesToInsert.isNotEmpty()) {
            storeTypeDao.insertAll(storeTypesToInsert)
        }

        val categoryMap = categoryDao.getAllLookupRows().associateBy { it.name }
        val storeTypeMap = storeTypeDao.getAllLookupRows().associateBy { it.name }

        val itemsToInsert = seedItems.mapNotNull { seed ->
            val category = categoryMap[seed.categoryName] ?: return@mapNotNull null
            if (!category.isActive) return@mapNotNull null

            val normalizedName = seed.name.normalize()
            val exists = itemDao.existsAnyInCategory(category.id, normalizedName)
            if (exists) return@mapNotNull null

            val storeTypeId = seed.storeTypeName
                ?.let { storeTypeMap[it] }
                ?.takeIf { it.isActive }
                ?.id

            ItemEntity(
                name = seed.name,
                normalizedName = normalizedName,
                categoryId = category.id,
                storeTypeId = storeTypeId,
                isDefault = true,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
        }
        if (itemsToInsert.isNotEmpty()) {
            itemDao.insertAll(itemsToInsert)
        }

        appSettingDao.upsert(
            AppSettingEntity(
                key = DefaultSeedData.SEED_VERSION_KEY,
                value = DefaultSeedData.SEED_VERSION_VALUE,
                updatedAt = now
            )
        )
    }

    suspend fun restoreDefaultItems(): RestoreDefaultsResult {
        val now = System.currentTimeMillis()
        var categoriesInserted = 0
        var categoriesReactivated = 0
        var storeTypesInserted = 0
        var storeTypesReactivated = 0
        var itemsInserted = 0
        var itemsReactivated = 0

        val existingCategories = categoryDao.getAllLookupRows().associateBy { it.name }
        DefaultSeedData.categories.forEach { seed ->
            val existing = existingCategories[seed.name]
            when {
                existing == null -> {
                    categoryDao.insertAll(
                        listOf(
                            CategoryEntity(
                                name = seed.name,
                                sortOrder = seed.sortOrder,
                                isDefault = true,
                                isActive = true,
                                createdAt = now,
                                updatedAt = now
                            )
                        )
                    )
                    categoriesInserted++
                }
                !existing.isActive -> {
                    categoriesReactivated += categoryDao.reactivateDefaultByName(seed.name, now)
                }
            }
        }

        val existingStoreTypes = storeTypeDao.getAllLookupRows().associateBy { it.name }
        DefaultSeedData.storeTypes.forEach { seed ->
            val existing = existingStoreTypes[seed.name]
            when {
                existing == null -> {
                    storeTypeDao.insertAll(
                        listOf(
                            StoreTypeEntity(
                                name = seed.name,
                                sortOrder = seed.sortOrder,
                                isDefault = true,
                                isActive = true,
                                createdAt = now,
                                updatedAt = now
                            )
                        )
                    )
                    storeTypesInserted++
                }
                !existing.isActive -> {
                    storeTypesReactivated += storeTypeDao.reactivateDefaultByName(seed.name, now)
                }
            }
        }

        val categoryMap = categoryDao.getAllLookupRows().associateBy { it.name }
        val storeTypeMap = storeTypeDao.getAllLookupRows().associateBy { it.name }

        val itemsToInsert = mutableListOf<ItemEntity>()
        activeDefaultItems().forEach { seed ->
            val category = categoryMap[seed.categoryName] ?: return@forEach
            if (!category.isActive) return@forEach
            val normalizedName = seed.name.normalize()

            val reactivated = itemDao.reactivateDefaultInCategory(
                categoryId = category.id,
                normalizedName = normalizedName,
                updatedAt = now
            )
            if (reactivated > 0) {
                itemsReactivated += reactivated
                return@forEach
            }

            if (itemDao.existsAnyInCategory(category.id, normalizedName)) {
                return@forEach
            }

            val storeTypeId = seed.storeTypeName
                ?.let { storeTypeMap[it] }
                ?.takeIf { it.isActive }
                ?.id

            itemsToInsert += ItemEntity(
                name = seed.name,
                normalizedName = normalizedName,
                categoryId = category.id,
                storeTypeId = storeTypeId,
                isDefault = true,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
        }

        if (itemsToInsert.isNotEmpty()) {
            val insertResults = itemDao.insertAll(itemsToInsert)
            itemsInserted += insertResults.count { it > 0 }
        }

        appSettingDao.upsert(
            AppSettingEntity(
                key = DefaultSeedData.SEED_VERSION_KEY,
                value = DefaultSeedData.SEED_VERSION_VALUE,
                updatedAt = now
            )
        )

        return RestoreDefaultsResult(
            categoriesInserted = categoriesInserted,
            categoriesReactivated = categoriesReactivated,
            storeTypesInserted = storeTypesInserted,
            storeTypesReactivated = storeTypesReactivated,
            itemsInserted = itemsInserted,
            itemsReactivated = itemsReactivated
        )
    }

    private suspend fun useSinhalaPack(): Boolean {
        val country = appSettingDao.getByKey(AppSettingKeys.SELECTED_COUNTRY_REGION)?.value
        val lang = appSettingDao.getByKey(AppSettingKeys.SELECTED_LANGUAGE)?.value
        return country == "Sri Lanka" || lang == "Sinhala"
    }

    private suspend fun activeDefaultItems(): List<SeedItem> {
        return if (useSinhalaPack()) DefaultSeedData.items else EnglishPlaceholderSeed.items
    }

    private fun String.normalize(): String {
        return trim().lowercase(Locale.getDefault())
    }
}
