package com.preshan.grocerylist.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.preshan.grocerylist.support.InMemoryTestDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShoppingSessionSnapshotTest {

    private lateinit var db: com.preshan.grocerylist.data.local.database.AppDatabase
    private lateinit var repository: ShoppingSessionRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = InMemoryTestDatabase.create(context)
        repository = ShoppingSessionRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun sessionDisplay_usesSnapshots_afterCatalogRename() = runTest {
        val categoryId = InMemoryTestDatabase.insertCategory(db, "Vegetables")
        val itemId = InMemoryTestDatabase.insertItem(db, "Carrot", categoryId)

        val sessionId = repository.createActiveSessionWithItems(listOf(itemId))
        val displayBefore = repository.getSessionItemsGroupedForDisplay(sessionId)
        assertEquals("Carrot", displayBefore["Vegetables"]?.single()?.name)

        val item = db.itemDao().getById(itemId)!!
        db.itemDao().updateItem(item.copy(name = "Orange Carrot", normalizedName = "orange carrot"))
        val category = db.categoryDao().getById(categoryId)!!
        db.categoryDao().update(category.copy(name = "Fresh Vegetables"))

        val displayAfter = repository.getSessionItemsGroupedForDisplay(sessionId)
        assertEquals("Carrot", displayAfter["Vegetables"]?.single()?.name)
        assertNotEquals("Orange Carrot", displayAfter.values.flatten().single().name)
        assertEquals(listOf("Vegetables"), displayAfter.keys.toList())
    }
}
