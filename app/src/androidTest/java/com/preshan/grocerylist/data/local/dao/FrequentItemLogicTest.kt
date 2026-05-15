package com.preshan.grocerylist.data.local.dao

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.preshan.grocerylist.support.InMemoryTestDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FrequentItemLogicTest {

    private lateinit var db: com.preshan.grocerylist.data.local.database.AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = InMemoryTestDatabase.create(context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun frequentItems_requiresPurchaseCountAtLeastThree() = runTest {
        val categoryId = InMemoryTestDatabase.insertCategory(db, "Test")
        val itemId = InMemoryTestDatabase.insertItem(
            db = db,
            name = "Often Bought",
            categoryId = categoryId,
            purchaseCount = 2
        )

        assertFalse(db.itemDao().getFrequentActiveItemIds().contains(itemId))

        db.itemDao().incrementPurchaseForIds(listOf(itemId), System.currentTimeMillis())

        assertTrue(db.itemDao().getFrequentActiveItemIds().contains(itemId))
    }

    @Test
    fun resetFrequentItemData_clearsCountersAndTimestamps() = runTest {
        val categoryId = InMemoryTestDatabase.insertCategory(db, "Test")
        val now = System.currentTimeMillis()
        val itemId = InMemoryTestDatabase.insertItem(
            db = db,
            name = "Tracked",
            categoryId = categoryId,
            purchaseCount = 4,
            selectedCount = 2,
            lastSelectedAt = now,
            lastPurchasedAt = now
        )

        db.itemDao().resetFrequentItemData(now + 1)

        val item = db.itemDao().getById(itemId)!!
        assertEquals(0, item.purchaseCount)
        assertEquals(0, item.selectedCount)
        assertNull(item.lastSelectedAt)
        assertNull(item.lastPurchasedAt)
        assertFalse(db.itemDao().getFrequentActiveItemIds().contains(itemId))
    }
}
