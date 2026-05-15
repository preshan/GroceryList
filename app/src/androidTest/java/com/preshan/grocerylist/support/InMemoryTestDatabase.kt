package com.preshan.grocerylist.support

import android.content.Context
import androidx.room.Room
import com.preshan.grocerylist.data.AppSettingKeys
import com.preshan.grocerylist.data.local.database.AppDatabase
import com.preshan.grocerylist.data.local.entity.AppSettingEntity
import com.preshan.grocerylist.data.local.entity.CategoryEntity
import com.preshan.grocerylist.data.local.entity.ItemEntity
import com.preshan.grocerylist.data.seed.DatabaseSeeder
import java.util.Locale

object InMemoryTestDatabase {

    fun create(context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    fun createSeeder(db: AppDatabase): DatabaseSeeder =
        DatabaseSeeder(
            categoryDao = db.categoryDao(),
            storeTypeDao = db.storeTypeDao(),
            itemDao = db.itemDao(),
            appSettingDao = db.appSettingDao()
        )

    suspend fun useEnglishCatalog(db: AppDatabase) {
        val now = System.currentTimeMillis()
        val dao = db.appSettingDao()
        dao.upsert(
            AppSettingEntity(
                key = AppSettingKeys.SELECTED_COUNTRY_REGION,
                value = "International",
                updatedAt = now
            )
        )
        dao.upsert(
            AppSettingEntity(
                key = AppSettingKeys.SELECTED_LANGUAGE,
                value = "English",
                updatedAt = now
            )
        )
    }

    suspend fun insertCategory(
        db: AppDatabase,
        name: String,
        sortOrder: Int = 1,
        isDefault: Boolean = false
    ): Long {
        val now = System.currentTimeMillis()
        return db.categoryDao().insert(
            CategoryEntity(
                name = name,
                description = null,
                sortOrder = sortOrder,
                isDefault = isDefault,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    suspend fun insertItem(
        db: AppDatabase,
        name: String,
        categoryId: Long,
        isDefault: Boolean = false,
        isFavorite: Boolean = false,
        purchaseCount: Int = 0,
        selectedCount: Int = 0,
        lastSelectedAt: Long? = null,
        lastPurchasedAt: Long? = null
    ): Long {
        val now = System.currentTimeMillis()
        return db.itemDao().insertItem(
            ItemEntity(
                name = name,
                normalizedName = name.trim().lowercase(Locale.getDefault()),
                categoryId = categoryId,
                storeTypeId = null,
                notes = null,
                isFavorite = isFavorite,
                purchaseCount = purchaseCount,
                selectedCount = selectedCount,
                lastSelectedAt = lastSelectedAt,
                lastPurchasedAt = lastPurchasedAt,
                excludedFromFrequent = false,
                isDefault = isDefault,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
        )
    }

}
