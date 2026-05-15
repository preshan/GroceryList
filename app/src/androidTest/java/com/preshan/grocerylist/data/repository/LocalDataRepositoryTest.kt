package com.preshan.grocerylist.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.preshan.grocerylist.data.AppSettingKeys
import com.preshan.grocerylist.data.local.entity.ShoppingSessionEntity
import com.preshan.grocerylist.support.InMemoryTestDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataRepositoryTest {

    private lateinit var db: com.preshan.grocerylist.data.local.database.AppDatabase
    private lateinit var repository: LocalDataRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = InMemoryTestDatabase.create(context)
        repository = LocalDataRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun clearAllLocalData_removesCatalogueSessionsAndSettings() = runTest {
        InMemoryTestDatabase.useEnglishCatalog(db)
        val seeder = InMemoryTestDatabase.createSeeder(db)
        seeder.restoreDefaultItems()

        val categoryId = InMemoryTestDatabase.insertCategory(db, "Custom Category")
        InMemoryTestDatabase.insertItem(db, "Custom Item", categoryId)

        val now = System.currentTimeMillis()
        db.shoppingSessionDao().insert(
            ShoppingSessionEntity(
                status = "active",
                totalItems = 2,
                purchasedItems = 0,
                createdAt = now,
                updatedAt = now
            )
        )

        assertTrue(db.itemDao().getAllItemsForCsvExport().isNotEmpty())
        assertTrue(db.categoryDao().getActiveCategories().isNotEmpty())
        assertTrue(db.appSettingDao().getByKey(AppSettingKeys.SELECTED_LANGUAGE) != null)
        assertTrue(db.shoppingSessionDao().getLatestByStatus("active") != null)

        repository.clearAllLocalData()

        assertTrue(db.itemDao().getAllItemsForCsvExport().isEmpty())
        assertTrue(db.categoryDao().getActiveCategories().isEmpty())
        assertNull(db.appSettingDao().getByKey(AppSettingKeys.SELECTED_LANGUAGE))
        assertNull(db.appSettingDao().getByKey(AppSettingKeys.SELECTED_COUNTRY_REGION))
        assertNull(db.shoppingSessionDao().getLatestByStatus("active"))
        assertNull(db.shoppingSessionDao().getLatestByStatus("completed"))
    }
}
