package com.preshan.grocerylist.ui.screens.manageitems

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.preshan.grocerylist.data.local.dao.ManageItemRow
import com.preshan.grocerylist.data.local.database.DatabaseProvider
import com.preshan.grocerylist.data.local.entity.CategoryEntity
import com.preshan.grocerylist.data.local.entity.ItemEntity
import com.preshan.grocerylist.data.seed.DatabaseSeeder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

data class BulkAddResult(
    val added: Int,
    val skippedDuplicate: Int,
    val skippedEmpty: Int
)

data class ManageItemsPickerData(
    val categories: List<CategoryEntity>
)

class ManageItemsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseProvider.getDatabase(application)
    private val itemDao = database.itemDao()
    private val categoryDao = database.categoryDao()
    private val storeTypeDao = database.storeTypeDao()
    private val appSettingDao = database.appSettingDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _pickerData = MutableStateFlow<ManageItemsPickerData?>(null)
    val pickerData: StateFlow<ManageItemsPickerData?> = _pickerData.asStateFlow()

    private val _pendingRemove = MutableStateFlow<ManageItemRow?>(null)
    val pendingRemove: StateFlow<ManageItemRow?> = _pendingRemove.asStateFlow()

    private val _pendingBulkRemoveIds = MutableStateFlow<List<Long>?>(null)
    val pendingBulkRemoveIds: StateFlow<List<Long>?> = _pendingBulkRemoveIds.asStateFlow()

    private val _selectionMode = MutableStateFlow(false)
    val selectionMode: StateFlow<Boolean> = _selectionMode.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    val displayedItems: StateFlow<List<ManageItemRow>> = combine(
        itemDao.observeManageItemRows(),
        _searchQuery
    ) { rows, query ->
        if (query.isBlank()) rows
        else rows.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseSeeder(
                categoryDao = categoryDao,
                storeTypeDao = storeTypeDao,
                itemDao = itemDao,
                appSettingDao = appSettingDao
            ).seedIfNeeded()
            _pickerData.value = ManageItemsPickerData(
                categories = categoryDao.getActiveCategories()
            )
        }
    }

    fun setSearchQuery(value: String) {
        _searchQuery.value = value
    }

    fun requestRemove(row: ManageItemRow) {
        _pendingRemove.value = row
    }

    fun requestRemoveForEditor(itemId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = itemDao.getById(itemId) ?: return@launch
            if (!entity.isActive) return@launch
            val categoryName = categoryDao.getActiveCategories()
                .find { it.id == entity.categoryId }
                ?.name
                .orEmpty()
            _pendingRemove.value = ManageItemRow(
                id = entity.id,
                name = entity.name,
                categoryName = categoryName,
                isFavorite = entity.isFavorite
            )
        }
    }

    fun setSelectionMode(enabled: Boolean) {
        _selectionMode.value = enabled
        if (!enabled) _selectedIds.value = emptySet()
    }

    fun toggleRowSelected(itemId: Long) {
        _selectedIds.update { cur -> if (itemId in cur) cur - itemId else cur + itemId }
    }

    fun selectAllDisplayedItemIds(ids: List<Long>) {
        _selectedIds.value = ids.toSet()
    }

    fun clearRowSelection() {
        _selectedIds.value = emptySet()
    }

    fun requestBulkRemoveConfirm() {
        val ids = _selectedIds.value.toList()
        if (ids.isNotEmpty()) _pendingBulkRemoveIds.value = ids
    }

    fun dismissBulkRemoveConfirm() {
        _pendingBulkRemoveIds.value = null
    }

    fun confirmBulkRemove() {
        val ids = _pendingBulkRemoveIds.value ?: return
        if (ids.isEmpty()) {
            _pendingBulkRemoveIds.value = null
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            itemDao.softDeactivateItems(ids, now)
            _selectedIds.value = emptySet()
            _selectionMode.value = false
            _pendingBulkRemoveIds.value = null
        }
    }

    fun dismissRemoveConfirm() {
        _pendingRemove.value = null
    }

    suspend fun bulkAddCommaSeparated(categoryId: Long, raw: String): BulkAddResult = withContext(Dispatchers.IO) {
        val parts = raw.split(',')
        val skippedEmpty = parts.count { it.trim().isEmpty() }
        val names = parts.map { it.trim() }.filter { it.isNotEmpty() }
        if (names.isEmpty()) {
            return@withContext BulkAddResult(added = 0, skippedDuplicate = 0, skippedEmpty = skippedEmpty)
        }
        var added = 0
        var skippedDuplicate = 0
        val seenNorm = mutableSetOf<String>()
        val now = System.currentTimeMillis()
        for (trimmed in names) {
            val normalized = trimmed.lowercase(Locale.getDefault())
            if (!seenNorm.add(normalized)) {
                skippedDuplicate++
                continue
            }
            if (itemDao.existsActiveDuplicateInCategory(categoryId, normalized, excludeId = 0L)) {
                skippedDuplicate++
                continue
            }
            val entity = ItemEntity(
                name = trimmed,
                normalizedName = normalized,
                categoryId = categoryId,
                storeTypeId = null,
                notes = null,
                isFavorite = false,
                purchaseCount = 0,
                selectedCount = 0,
                lastSelectedAt = null,
                lastPurchasedAt = null,
                excludedFromFrequent = false,
                isDefault = false,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
            itemDao.insertItem(entity)
            added++
        }
        BulkAddResult(added = added, skippedDuplicate = skippedDuplicate, skippedEmpty = skippedEmpty)
    }

    fun confirmRemove() {
        val row = _pendingRemove.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            itemDao.softDeactivateItem(row.id, System.currentTimeMillis())
            _pendingRemove.value = null
        }
    }

    fun toggleFavorite(itemId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = itemDao.getById(itemId) ?: return@launch
            if (!entity.isActive) return@launch
            itemDao.updateFavorite(itemId, !entity.isFavorite, System.currentTimeMillis())
        }
    }

    suspend fun loadItemForEdit(id: Long): ItemEntity? = withContext(Dispatchers.IO) {
        itemDao.getById(id)?.takeIf { it.isActive }
    }

    /**
     * Persists a new or updated item. Returns null on success, or a short error message.
     */
    suspend fun saveItem(
        existingId: Long?,
        name: String,
        categoryId: Long,
        favorite: Boolean
    ): String? = withContext(Dispatchers.IO) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return@withContext "Name is required."
        val normalized = trimmed.lowercase(Locale.getDefault())
        val excludeId = existingId ?: 0L
        if (itemDao.existsActiveDuplicateInCategory(categoryId, normalized, excludeId)) {
            return@withContext "An item with this name already exists in this category."
        }
        val now = System.currentTimeMillis()
        if (existingId == null) {
            val entity = ItemEntity(
                name = trimmed,
                normalizedName = normalized,
                categoryId = categoryId,
                storeTypeId = null,
                notes = null,
                isFavorite = favorite,
                purchaseCount = 0,
                selectedCount = 0,
                lastSelectedAt = null,
                lastPurchasedAt = null,
                excludedFromFrequent = false,
                isDefault = false,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
            itemDao.insertItem(entity)
        } else {
            val existing = itemDao.getById(existingId) ?: return@withContext "Item not found."
            if (!existing.isActive) return@withContext "Item not found."
            itemDao.updateItem(
                existing.copy(
                    name = trimmed,
                    normalizedName = normalized,
                    categoryId = categoryId,
                    isFavorite = favorite,
                    updatedAt = now
                )
            )
        }
        null
    }
}
