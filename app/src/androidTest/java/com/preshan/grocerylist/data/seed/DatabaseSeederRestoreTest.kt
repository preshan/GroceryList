package com.preshan.grocerylist.data.seed

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.preshan.grocerylist.support.InMemoryTestDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseSeederRestoreTest {

    private lateinit var db: com.preshan.grocerylist.data.local.database.AppDatabase
    private lateinit var seeder: DatabaseSeeder

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = InMemoryTestDatabase.create(context)
        seeder = InMemoryTestDatabase.createSeeder(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun restore_isIdempotent_andDoesNotDuplicateSeedItems() = runTest {
        InMemoryTestDatabase.useEnglishCatalog(db)
        InMemoryTestDatabase.insertCategory(db, "Food & Grocery", isDefault = true)

        val first = seeder.restoreDefaultItems()
        assertTrue(first.itemsInserted > 0)

        val countAfterFirst = db.itemDao().getAllItemsForCsvExport().size
        val second = seeder.restoreDefaultItems()
        assertEquals(0, second.itemsInserted)
        assertEquals(0, second.itemsReactivated)
        assertEquals(countAfterFirst, db.itemDao().getAllItemsForCsvExport().size)

        val riceCount = db.itemDao().getAllItemsForCsvExport().count {
            it.itemName.equals("Rice", ignoreCase = true) &&
                it.categoryName == "Food & Grocery"
        }
        assertEquals(1, riceCount)
    }

    @Test
    fun restore_doesNotRemoveCustomItems() = runTest {
        InMemoryTestDatabase.useEnglishCatalog(db)
        val categoryId = InMemoryTestDatabase.insertCategory(db, "Food & Grocery", isDefault = true)
        val customId = InMemoryTestDatabase.insertItem(
            db = db,
            name = "My Custom Item",
            categoryId = categoryId,
            isDefault = false
        )

        seeder.restoreDefaultItems()

        val custom = db.itemDao().getById(customId)
        assertNotNull(custom)
        assertTrue(custom!!.isActive)
        assertEquals("My Custom Item", custom.name)
    }

    @Test
    fun restore_doesNotOverwriteUserEditedDefaultItemMetadata() = runTest {
        InMemoryTestDatabase.useEnglishCatalog(db)
        val categoryId = InMemoryTestDatabase.insertCategory(db, "Food & Grocery", isDefault = true)
        val riceId = InMemoryTestDatabase.insertItem(
            db = db,
            name = "Rice",
            categoryId = categoryId,
            isDefault = true,
            isFavorite = true,
            purchaseCount = 2
        )

        seeder.restoreDefaultItems()

        val rice = db.itemDao().getById(riceId)
        assertNotNull(rice)
        assertEquals("Rice", rice!!.name)
        assertTrue(rice.isFavorite)
        assertEquals(2, rice.purchaseCount)
        assertEquals(
            1,
            db.itemDao().getAllItemsForCsvExport().count {
                it.itemName.equals("Rice", ignoreCase = true) &&
                    it.categoryName == "Food & Grocery"
            }
        )
    }

    @Test
    fun restore_preservesRenamedDefaultItem_withoutDeletingIt() = runTest {
        InMemoryTestDatabase.useEnglishCatalog(db)
        val categoryId = InMemoryTestDatabase.insertCategory(db, "Food & Grocery", isDefault = true)
        val editedId = InMemoryTestDatabase.insertItem(
            db = db,
            name = "My Rice",
            categoryId = categoryId,
            isDefault = true
        )

        seeder.restoreDefaultItems()

        val edited = db.itemDao().getById(editedId)
        assertNotNull(edited)
        assertEquals("My Rice", edited!!.name)
        assertEquals("my rice", edited.normalizedName)
    }
}
