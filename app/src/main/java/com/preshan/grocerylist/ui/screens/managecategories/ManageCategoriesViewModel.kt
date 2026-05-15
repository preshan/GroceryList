package com.preshan.grocerylist.ui.screens.managecategories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.preshan.grocerylist.data.local.database.DatabaseProvider
import com.preshan.grocerylist.data.local.entity.CategoryEntity
import com.preshan.grocerylist.data.seed.DatabaseSeeder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.preshan.grocerylist.util.AppTextKey

class ManageCategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseProvider.getDatabase(application)
    private val categoryDao = database.categoryDao()
    private val storeTypeDao = database.storeTypeDao()
    private val itemDao = database.itemDao()
    private val appSettingDao = database.appSettingDao()

    val categories: StateFlow<List<CategoryEntity>> = categoryDao.observeActiveCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _pendingRemove = MutableStateFlow<CategoryEntity?>(null)
    val pendingRemove: StateFlow<CategoryEntity?> = _pendingRemove.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseSeeder(
                categoryDao = categoryDao,
                storeTypeDao = storeTypeDao,
                itemDao = itemDao,
                appSettingDao = appSettingDao
            ).seedIfNeeded()
        }
    }

    fun requestRemove(category: CategoryEntity) {
        _pendingRemove.value = category
    }

    fun dismissRemoveConfirm() {
        _pendingRemove.value = null
    }

    suspend fun confirmRemovePending(): AppTextKey? = withContext(Dispatchers.IO) {
        val cat = _pendingRemove.value ?: return@withContext null
        if (cat.isDefault) {
            _pendingRemove.value = null
            return@withContext AppTextKey.BUILTIN_CATEGORY_CANNOT_REMOVE
        }
        if (categoryDao.countActiveItemsInCategory(cat.id) > 0) {
            _pendingRemove.value = null
            return@withContext AppTextKey.CATEGORY_HAS_ITEMS_CANNOT_REMOVE
        }
        val now = System.currentTimeMillis()
        val rows = categoryDao.softDeactivateUserCategory(cat.id, now)
        _pendingRemove.value = null
        if (rows == 0) return@withContext AppTextKey.COULD_NOT_REMOVE_CATEGORY
        null
    }

    suspend fun addCategory(name: String): AppTextKey? = withContext(Dispatchers.IO) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return@withContext AppTextKey.ITEM_NAME_REQUIRED
        if (categoryDao.existsOtherActiveWithSameName(trimmed, excludeId = 0L)) {
            return@withContext AppTextKey.DUPLICATE_CATEGORY_NAME
        }
        val now = System.currentTimeMillis()
        val sortOrder = categoryDao.getMaxSortOrder() + 1
        categoryDao.insert(
            CategoryEntity(
                name = trimmed,
                description = null,
                sortOrder = sortOrder,
                isDefault = false,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
        )
        null
    }

    suspend fun updateCategory(id: Long, name: String): AppTextKey? = withContext(Dispatchers.IO) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return@withContext AppTextKey.ITEM_NAME_REQUIRED
        val existing = categoryDao.getById(id) ?: return@withContext AppTextKey.CATEGORY_NOT_FOUND
        if (!existing.isActive) return@withContext AppTextKey.CATEGORY_NOT_FOUND
        if (categoryDao.existsOtherActiveWithSameName(trimmed, excludeId = id)) {
            return@withContext AppTextKey.DUPLICATE_CATEGORY_NAME
        }
        val now = System.currentTimeMillis()
        categoryDao.update(
            existing.copy(name = trimmed, updatedAt = now)
        )
        null
    }
}
