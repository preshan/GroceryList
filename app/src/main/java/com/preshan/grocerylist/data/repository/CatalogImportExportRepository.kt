package com.preshan.grocerylist.data.repository

import androidx.room.withTransaction
import com.preshan.grocerylist.data.local.dao.CategoryLookupRow
import com.preshan.grocerylist.data.local.dao.CategoryDao
import com.preshan.grocerylist.data.local.dao.ItemDao
import com.preshan.grocerylist.data.local.database.AppDatabase
import com.preshan.grocerylist.data.local.entity.CategoryEntity
import com.preshan.grocerylist.data.local.entity.ItemEntity
import com.preshan.grocerylist.util.CsvRfc4180
import java.util.Locale

data class CsvImportOptions(
    val importFavorites: Boolean = true,
    val importFrequentData: Boolean = true,
    val importInactiveItems: Boolean = true
)

data class CsvImportSummary(
    val categoriesAdded: Int,
    val itemsAdded: Int,
    val itemsUpdated: Int,
    val duplicatesSkipped: Int,
    val rowsFailed: Int
)

sealed class CsvImportResult {
    data class Success(val summary: CsvImportSummary) : CsvImportResult()
    data object InvalidHeader : CsvImportResult()
    data object EmptyFile : CsvImportResult()
}

class CatalogImportExportRepository(
    private val database: AppDatabase
) {
    private val categoryDao: CategoryDao = database.categoryDao()
    private val itemDao: ItemDao = database.itemDao()

    companion object {
        const val COL_CATEGORY_NAME = "category_name"
        const val COL_ITEM_NAME = "item_name"
        const val COL_IS_FAVORITE = "is_favorite"
        const val COL_PURCHASE_COUNT = "purchase_count"
        const val COL_SELECTED_COUNT = "selected_count"
        const val COL_LAST_SELECTED_AT = "last_selected_at"
        const val COL_LAST_PURCHASED_AT = "last_purchased_at"
        const val COL_IS_ACTIVE = "is_active"

        val HEADER_CELLS = listOf(
            COL_CATEGORY_NAME,
            COL_ITEM_NAME,
            COL_IS_FAVORITE,
            COL_PURCHASE_COUNT,
            COL_SELECTED_COUNT,
            COL_LAST_SELECTED_AT,
            COL_LAST_PURCHASED_AT,
            COL_IS_ACTIVE
        )
    }

    suspend fun buildExportCsvUtf8(): String {
        val rows = itemDao.getAllItemsForCsvExport()
        val lines = mutableListOf<String>()
        lines.add(CsvRfc4180.formatRow(HEADER_CELLS))
        for (r in rows) {
            lines.add(
                CsvRfc4180.formatRow(
                    listOf(
                        r.categoryName,
                        r.itemName,
                        r.isFavorite.toString(),
                        r.purchaseCount.toString(),
                        r.selectedCount.toString(),
                        r.lastSelectedAt?.toString() ?: "",
                        r.lastPurchasedAt?.toString() ?: "",
                        r.isActive.toString()
                    )
                )
            )
        }
        return lines.joinToString("\r\n")
    }

    suspend fun importFromCsvUtf8(text: String, options: CsvImportOptions): CsvImportResult {
        val stripped = CsvRfc4180.stripBom(text.trim())
        if (stripped.isEmpty()) return CsvImportResult.EmptyFile

        val logicalLines = CsvRfc4180.splitLogicalLines(stripped)
        if (logicalLines.isEmpty()) return CsvImportResult.EmptyFile

        val headerCells = CsvRfc4180.parseLine(logicalLines.first()).map { it.trim().lowercase(Locale.ROOT) }
        val idxCategory = headerCells.indexOf(COL_CATEGORY_NAME)
        val idxItem = headerCells.indexOf(COL_ITEM_NAME)
        if (idxCategory < 0 || idxItem < 0) return CsvImportResult.InvalidHeader

        val optionalIdx = mapOf(
            COL_IS_FAVORITE to headerCells.indexOf(COL_IS_FAVORITE),
            COL_PURCHASE_COUNT to headerCells.indexOf(COL_PURCHASE_COUNT),
            COL_SELECTED_COUNT to headerCells.indexOf(COL_SELECTED_COUNT),
            COL_LAST_SELECTED_AT to headerCells.indexOf(COL_LAST_SELECTED_AT),
            COL_LAST_PURCHASED_AT to headerCells.indexOf(COL_LAST_PURCHASED_AT),
            COL_IS_ACTIVE to headerCells.indexOf(COL_IS_ACTIVE)
        )

        var categoriesAdded = 0
        var itemsAdded = 0
        var itemsUpdated = 0
        var duplicatesSkipped = 0
        var rowsFailed = 0

        val seenKeysInFile = mutableSetOf<String>()
        val dataLines = logicalLines.drop(1)

        database.withTransaction {
            val now = System.currentTimeMillis()
            var categoryLookup = categoryDao.getAllLookupRows()

            fun findCategoryRow(name: String): CategoryLookupRow? {
                val matches = categoryLookup.filter {
                    it.name.trim().equals(name.trim(), ignoreCase = true)
                }
                return matches.firstOrNull { it.isActive } ?: matches.firstOrNull()
            }

            suspend fun resolveCategoryId(csvCategoryName: String): Long? {
                val trimmedCat = csvCategoryName.trim()
                if (trimmedCat.isEmpty()) return null
                val existing = findCategoryRow(trimmedCat)
                if (existing != null) {
                    if (!existing.isActive) {
                        val entity = categoryDao.getById(existing.id) ?: return null
                        categoryDao.update(
                            entity.copy(
                                name = trimmedCat,
                                isActive = true,
                                updatedAt = now
                            )
                        )
                        categoryLookup = categoryDao.getAllLookupRows()
                    }
                    return existing.id
                }
                val sortOrder = categoryDao.getMaxSortOrder() + 1
                categoryDao.insert(
                    CategoryEntity(
                        name = trimmedCat,
                        description = null,
                        sortOrder = sortOrder,
                        isDefault = false,
                        isActive = true,
                        createdAt = now,
                        updatedAt = now
                    )
                )
                categoriesAdded++
                categoryLookup = categoryDao.getAllLookupRows()
                return findCategoryRow(trimmedCat)?.id
            }

            for (line in dataLines) {
                val cells = try {
                    CsvRfc4180.parseLine(line)
                } catch (_: Exception) {
                    rowsFailed++
                    continue
                }
                fun cellAt(index: Int): String? {
                    if (index < 0 || index >= cells.size) return null
                    return cells[index]
                }

                val categoryNameRaw = cellAt(idxCategory)?.trim() ?: ""
                val itemNameRaw = cellAt(idxItem)?.trim() ?: ""
                if (categoryNameRaw.isEmpty() || itemNameRaw.isEmpty()) {
                    rowsFailed++
                    continue
                }

                val isActiveParsed = parseBoolOrNull(cellAt(optionalIdx[COL_IS_ACTIVE] ?: -1))
                if (!options.importInactiveItems && isActiveParsed == false) {
                    continue
                }
                val effectiveIsActive = when {
                    !options.importInactiveItems -> true
                    isActiveParsed != null -> isActiveParsed
                    else -> true
                }

                val normItem = normalizeName(itemNameRaw)
                val fileKey = normalizeName(categoryNameRaw) + "\u0000" + normItem
                if (fileKey in seenKeysInFile) {
                    duplicatesSkipped++
                    continue
                }
                seenKeysInFile.add(fileKey)

                val categoryId = resolveCategoryId(categoryNameRaw) ?: run {
                    rowsFailed++
                    continue
                }

                val csvFavorite = parseBoolOrNull(cellAt(optionalIdx[COL_IS_FAVORITE] ?: -1)) ?: false
                val purchase = cellAt(optionalIdx[COL_PURCHASE_COUNT] ?: -1)?.let { parseIntOrNull(it) }
                val selected = cellAt(optionalIdx[COL_SELECTED_COUNT] ?: -1)?.let { parseIntOrNull(it) }
                val lastSel = cellAt(optionalIdx[COL_LAST_SELECTED_AT] ?: -1)?.let { parseLongOrNull(it) }
                val lastPur = cellAt(optionalIdx[COL_LAST_PURCHASED_AT] ?: -1)?.let { parseLongOrNull(it) }

                val pc: Int
                val sc: Int
                val lsa: Long?
                val lpa: Long?
                if (options.importFrequentData) {
                    pc = purchase?.coerceAtLeast(0) ?: 0
                    sc = selected?.coerceAtLeast(0) ?: 0
                    lsa = lastSel
                    lpa = lastPur
                } else {
                    pc = 0
                    sc = 0
                    lsa = null
                    lpa = null
                }

                val existing = itemDao.getItemForCsvMerge(categoryId, normItem)
                if (existing == null) {
                    val favorite = if (options.importFavorites) csvFavorite else false
                    val newEntity = ItemEntity(
                        name = itemNameRaw,
                        normalizedName = normItem,
                        categoryId = categoryId,
                        storeTypeId = null,
                        notes = null,
                        isFavorite = favorite,
                        purchaseCount = pc,
                        selectedCount = sc,
                        lastSelectedAt = lsa,
                        lastPurchasedAt = lpa,
                        excludedFromFrequent = false,
                        isDefault = false,
                        isActive = effectiveIsActive,
                        createdAt = now,
                        updatedAt = now
                    )
                    itemDao.insertItem(newEntity)
                    itemsAdded++
                } else {
                    val favorite = when {
                        options.importFavorites -> csvFavorite
                        else -> existing.isFavorite
                    }
                    val merged = existing.copy(
                        name = itemNameRaw,
                        normalizedName = normItem,
                        isFavorite = favorite,
                        purchaseCount = pc,
                        selectedCount = sc,
                        lastSelectedAt = lsa,
                        lastPurchasedAt = lpa,
                        isActive = effectiveIsActive,
                        updatedAt = now
                    )
                    if (merged != existing) {
                        itemDao.updateItem(merged)
                        itemsUpdated++
                    }
                }
            }
        }

        return CsvImportResult.Success(
            CsvImportSummary(
                categoriesAdded = categoriesAdded,
                itemsAdded = itemsAdded,
                itemsUpdated = itemsUpdated,
                duplicatesSkipped = duplicatesSkipped,
                rowsFailed = rowsFailed
            )
        )
    }

    private fun normalizeName(s: String): String =
        s.trim().lowercase(Locale.getDefault())

    private fun parseBoolOrNull(raw: String?): Boolean? {
        if (raw == null) return null
        val t = raw.trim().lowercase(Locale.ROOT)
        if (t.isEmpty()) return null
        return when (t) {
            "true", "1", "yes", "y" -> true
            "false", "0", "no", "n" -> false
            else -> null
        }
    }

    private fun parseIntOrNull(raw: String): Int? =
        raw.trim().toIntOrNull()

    private fun parseLongOrNull(raw: String): Long? =
        raw.trim().toLongOrNull()
}
