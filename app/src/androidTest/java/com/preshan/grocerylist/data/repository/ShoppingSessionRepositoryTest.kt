package com.preshan.grocerylist.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.preshan.grocerylist.support.InMemoryTestDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShoppingSessionRepositoryTest {

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
    fun completeSession_updatesSelectedAndPurchaseStats() = runTest {
        val categoryId = InMemoryTestDatabase.insertCategory(db, "Test Category")
        val itemA = InMemoryTestDatabase.insertItem(db, "Item A", categoryId)
        val itemB = InMemoryTestDatabase.insertItem(db, "Item B", categoryId)
        val itemC = InMemoryTestDatabase.insertItem(db, "Item C", categoryId)

        val sessionId = repository.createActiveSessionWithItems(listOf(itemA, itemB, itemC))
        assertTrue(sessionId > 0)

        repository.completeSessionAndApplyItemStats(
            sessionId = sessionId,
            purchasedItemIds = setOf(itemA, itemB),
            allSelectedItemIds = listOf(itemA, itemB, itemC),
            totalItemCount = 3,
            purchasedCount = 2
        )

        val a = db.itemDao().getById(itemA)!!
        val b = db.itemDao().getById(itemB)!!
        val c = db.itemDao().getById(itemC)!!

        assertEquals(1, a.selectedCount)
        assertEquals(1, b.selectedCount)
        assertEquals(1, c.selectedCount)
        assertNotNull(a.lastSelectedAt)
        assertNotNull(b.lastSelectedAt)
        assertNotNull(c.lastSelectedAt)

        assertEquals(1, a.purchaseCount)
        assertEquals(1, b.purchaseCount)
        assertEquals(0, c.purchaseCount)
        assertNotNull(a.lastPurchasedAt)
        assertNotNull(b.lastPurchasedAt)
        assertNull(c.lastPurchasedAt)
    }
}
