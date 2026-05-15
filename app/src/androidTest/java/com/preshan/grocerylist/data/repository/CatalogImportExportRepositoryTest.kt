package com.preshan.grocerylist.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.preshan.grocerylist.support.InMemoryTestDatabase
import com.preshan.grocerylist.util.CsvRfc4180
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogImportExportRepositoryTest {

    private lateinit var db: com.preshan.grocerylist.data.local.database.AppDatabase
    private lateinit var repository: CatalogImportExportRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = InMemoryTestDatabase.create(context)
        repository = CatalogImportExportRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun exportImport_preservesSinhalaAndTamilUtf8() = runTest {
        val categoryId = InMemoryTestDatabase.insertCategory(db, "ප්‍රවර්ගය")
        InMemoryTestDatabase.insertItem(db, "සම්බ සහල්", categoryId, isFavorite = true, purchaseCount = 3)
        InMemoryTestDatabase.insertItem(db, "தமிழ் பொருள்", categoryId)

        val exported = repository.buildExportCsvUtf8()
        assertTrue(exported.contains("සම්බ සහල්"))
        assertTrue(exported.contains("தமிழ் பொருள்"))

        db.clearAllTables()

        val result = repository.importFromCsvUtf8(
            exported,
            CsvImportOptions(
                importFavorites = true,
                importFrequentData = true,
                importInactiveItems = true
            )
        )
        assertTrue(result is CsvImportResult.Success)

        val rows = db.itemDao().getAllItemsForCsvExport()
        assertEquals(2, rows.size)
        assertTrue(rows.any { it.itemName == "සම්බ සහල්" })
        assertTrue(rows.any { it.itemName == "தமிழ் பொருள்" })
        val rice = rows.first { it.itemName == "සම්බ සහල්" }
        assertTrue(rice.isFavorite)
        assertEquals(3, rice.purchaseCount)
    }

    @Test
    fun import_malformedCsv_doesNotCrash() = runTest {
        val malformed = """
            ${CsvRfc4180.formatRow(CatalogImportExportRepository.HEADER_CELLS)}
            "unclosed quote,item
            Food & Grocery,Rice
        """.trimIndent()

        val result = repository.importFromCsvUtf8(
            malformed,
            CsvImportOptions()
        )
        assertTrue(result is CsvImportResult.Success)
        val summary = (result as CsvImportResult.Success).summary
        assertTrue(summary.rowsFailed >= 1)
    }

    @Test
    fun import_invalidHeader_returnsInvalidHeader() = runTest {
        val result = repository.importFromCsvUtf8(
            "wrong,headers\na,b",
            CsvImportOptions()
        )
        assertEquals(CsvImportResult.InvalidHeader, result)
    }

    @Test
    fun import_duplicateRowsInFile_areSkipped() = runTest {
        val csv = buildString {
            appendLine(CsvRfc4180.formatRow(CatalogImportExportRepository.HEADER_CELLS))
            appendLine(CsvRfc4180.formatRow(listOf("Snacks", "Chips", "false", "0", "0", "", "", "true")))
            appendLine(CsvRfc4180.formatRow(listOf("Snacks", "Chips", "false", "0", "0", "", "", "true")))
        }

        val result = repository.importFromCsvUtf8(csv, CsvImportOptions()) as CsvImportResult.Success
        assertEquals(1, result.summary.itemsAdded)
        assertEquals(1, result.summary.duplicatesSkipped)
        assertEquals(1, db.itemDao().getAllItemsForCsvExport().size)
    }

    @Test
    fun importOptions_controlFavoritesAndFrequentData() = runTest {
        val csv = buildString {
            appendLine(CsvRfc4180.formatRow(CatalogImportExportRepository.HEADER_CELLS))
            appendLine(
                CsvRfc4180.formatRow(
                    listOf("Pantry", "Beans", "true", "5", "2", "100", "200", "true")
                )
            )
        }

        val result = repository.importFromCsvUtf8(
            csv,
            CsvImportOptions(
                importFavorites = false,
                importFrequentData = false,
                importInactiveItems = true
            )
        ) as CsvImportResult.Success
        assertEquals(1, result.summary.itemsAdded)

        val row = db.itemDao().getAllItemsForCsvExport().single()
        assertEquals(false, row.isFavorite)
        assertEquals(0, row.purchaseCount)
        assertEquals(0, row.selectedCount)
        assertEquals(null, row.lastSelectedAt)
        assertEquals(null, row.lastPurchasedAt)
    }
}
