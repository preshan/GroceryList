package com.preshan.grocerylist.ui.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.preshan.grocerylist.data.local.dao.ItemDao
import com.preshan.grocerylist.data.local.database.DatabaseProvider
import com.preshan.grocerylist.data.repository.AppSettingsRepository
import com.preshan.grocerylist.data.repository.CatalogRepository
import com.preshan.grocerylist.data.repository.CatalogRepositoryImpl
import com.preshan.grocerylist.data.repository.LocalDataRepository
import com.preshan.grocerylist.data.repository.ShoppingSessionRepository
import com.preshan.grocerylist.data.seed.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

data class GroceryItem(
    val id: Long,
    val name: String,
    val category: String,
    val normalizedName: String,
    val isFavorite: Boolean = false,
    val purchaseCount: Int = 0
)

enum class ShoppingFilter {
    ALL,
    PENDING,
    PURCHASED
}

data class GroceryUiState(
    val categories: List<String> = emptyList(),
    val itemsByCategory: Map<String, List<GroceryItem>> = emptyMap(),
    /** `null` means "All Categories" on Quick Select. */
    val selectedCategory: String? = null,
    val selectedItemIds: Set<Long> = emptySet(),
    val purchasedItemIds: Set<Long> = emptySet(),
    val shoppingFilter: ShoppingFilter = ShoppingFilter.ALL,
    /** Active Room shopping session while user is in shopping mode, null otherwise. */
    val activeShoppingSessionId: Long? = null,
    /** One-shot hint from quick actions (e.g. no frequent items). */
    val quickActionMessage: String? = null
)

class GroceryListViewModel(application: Application) : AndroidViewModel(application) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Serializes session-related DB writes vs finish/cancel/confirm. */
    private val sessionWriteMutex = Mutex()

    private val catalogRepository: CatalogRepository
    private val shoppingSessionRepository: ShoppingSessionRepository
    private val localDataRepository: LocalDataRepository
    private val itemDao: ItemDao
    private val databaseSeeder: DatabaseSeeder

    private val _uiState = MutableStateFlow(GroceryUiState())
    val uiState: StateFlow<GroceryUiState> = _uiState.asStateFlow()

    init {
        val database = DatabaseProvider.getDatabase(application)
        itemDao = database.itemDao()
        shoppingSessionRepository = ShoppingSessionRepository(database)
        localDataRepository = LocalDataRepository(database)
        databaseSeeder = DatabaseSeeder(
            categoryDao = database.categoryDao(),
            storeTypeDao = database.storeTypeDao(),
            itemDao = database.itemDao(),
            appSettingDao = database.appSettingDao()
        )
        catalogRepository = CatalogRepositoryImpl(
            itemDao = database.itemDao(),
            seeder = databaseSeeder
        )
        val appSettingsRepository = AppSettingsRepository(database.appSettingDao())

        scope.launch {
            appSettingsRepository.migrateLegacyInstallIfNeeded()
            catalogRepository.seedDefaultsIfNeeded()
            catalogRepository.observeActiveCatalogueGrouped().collect { grouped ->
                _uiState.update { state ->
                    val categoryList = grouped.keys.toList()
                    val selectedCategory = when (val sel = state.selectedCategory) {
                        null -> null
                        "" -> null
                        else -> if (grouped.containsKey(sel)) sel else null
                    }
                    state.copy(
                        categories = categoryList,
                        itemsByCategory = grouped,
                        selectedCategory = selectedCategory,
                        quickActionMessage = state.quickActionMessage
                    )
                }
            }
        }
    }

    /** Run after first-launch locale onboarding saves `first_launch_completed` so initial seed can run. */
    fun onFirstLaunchLocaleCompleted() {
        scope.launch(Dispatchers.IO) {
            catalogRepository.seedDefaultsIfNeeded()
        }
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category, quickActionMessage = null) }
    }

    fun toggleItemSelection(itemId: Long) {
        _uiState.update { state ->
            val updated = state.selectedItemIds.toMutableSet()
            if (!updated.add(itemId)) updated.remove(itemId)
            state.copy(selectedItemIds = updated, quickActionMessage = null)
        }
    }

    fun selectAllCurrentCategory() {
        _uiState.update { state ->
            val categoryItems = when (val cat = state.selectedCategory) {
                null -> state.itemsByCategory.values.flatten().map { it.id }
                else -> state.itemsByCategory[cat].orEmpty().map { it.id }
            }
            state.copy(selectedItemIds = state.selectedItemIds + categoryItems, quickActionMessage = null)
        }
    }

    /**
     * Quick Select "Clear": removes all selections when viewing all categories;
     * removes only selections in the current category when a category chip is selected.
     */
    fun clearQuickSelectSelections() {
        _uiState.update { state ->
            when (val cat = state.selectedCategory) {
                null -> state.copy(selectedItemIds = emptySet(), quickActionMessage = null)
                else -> {
                    val inCategory = state.itemsByCategory[cat].orEmpty().map { it.id }.toSet()
                    state.copy(selectedItemIds = state.selectedItemIds - inCategory, quickActionMessage = null)
                }
            }
        }
    }

    fun clearAllSelections() {
        _uiState.update { it.copy(selectedItemIds = emptySet(), purchasedItemIds = emptySet(), quickActionMessage = null) }
    }

    fun selectFavoritesFromDatabase() {
        _uiState.update { state ->
            val ids = state.itemsByCategory.values
                .flatten()
                .filter { it.isFavorite }
                .map { it.id }
                .toSet()
            if (ids.isEmpty()) {
                state.copy(quickActionMessage = "No favorites yet. Tap the star on a row to mark favorites.")
            } else {
                state.copy(selectedItemIds = state.selectedItemIds + ids, quickActionMessage = null)
            }
        }
    }

    fun selectFrequentFromDatabase(onDone: () -> Unit = {}) {
        scope.launch(Dispatchers.IO) {
            val ids = itemDao.getFrequentActiveItemIds().toSet()
            withContext(Dispatchers.Main) {
                _uiState.update { state ->
                    if (ids.isEmpty()) {
                        state.copy(quickActionMessage = "No frequent items yet (purchase an item 3+ times).")
                    } else {
                        state.copy(selectedItemIds = state.selectedItemIds + ids, quickActionMessage = null)
                    }
                }
                onDone()
            }
        }
    }

    fun resetFrequentItemData(onDone: () -> Unit = {}) {
        scope.launch(Dispatchers.IO) {
            itemDao.resetFrequentItemData(System.currentTimeMillis())
            withContext(Dispatchers.Main) { onDone() }
        }
    }

    fun restoreDefaultItems(onDone: (DatabaseSeeder.RestoreDefaultsResult) -> Unit) {
        scope.launch(Dispatchers.IO) {
            val result = databaseSeeder.restoreDefaultItems()
            withContext(Dispatchers.Main) { onDone(result) }
        }
    }

    fun toggleFavorite(itemId: Long) {
        scope.launch(Dispatchers.IO) {
            val rows = itemDao.getActiveByIds(listOf(itemId))
            val entity = rows.firstOrNull() ?: return@launch
            itemDao.updateFavorite(itemId, !entity.isFavorite, System.currentTimeMillis())
        }
    }

    fun setItemFavorite(itemId: Long, favorite: Boolean) {
        scope.launch(Dispatchers.IO) {
            val rows = itemDao.getActiveByIds(listOf(itemId))
            val entity = rows.firstOrNull() ?: return@launch
            if (entity.isFavorite == favorite) return@launch
            itemDao.updateFavorite(itemId, favorite, System.currentTimeMillis())
        }
    }

    suspend fun renameCatalogItem(itemId: Long, name: String): String? = withContext(Dispatchers.IO) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return@withContext "Name is required."
        val normalized = trimmed.lowercase(Locale.getDefault())
        val existing = itemDao.getById(itemId) ?: return@withContext "Item not found."
        if (!existing.isActive) return@withContext "Item not found."
        if (itemDao.existsActiveDuplicateInCategory(existing.categoryId, normalized, itemId)) {
            return@withContext "An item with this name already exists in this category."
        }
        val now = System.currentTimeMillis()
        itemDao.updateItem(
            existing.copy(
                name = trimmed,
                normalizedName = normalized,
                updatedAt = now
            )
        )
        null
    }

    fun deleteCatalogItem(itemId: Long) {
        scope.launch(Dispatchers.IO) {
            val entity = itemDao.getById(itemId) ?: return@launch
            if (!entity.isActive) return@launch
            itemDao.softDeactivateItem(itemId, System.currentTimeMillis())
            _uiState.update { state ->
                state.copy(
                    selectedItemIds = state.selectedItemIds - itemId,
                    purchasedItemIds = state.purchasedItemIds - itemId
                )
            }
        }
    }

    fun dismissQuickActionMessage() {
        _uiState.update { it.copy(quickActionMessage = null) }
    }

    fun setShoppingFilter(filter: ShoppingFilter) {
        _uiState.update { it.copy(shoppingFilter = filter) }
    }

    fun togglePurchased(itemId: Long) {
        var newPurchased = false
        var sessionId: Long? = null
        _uiState.update { state ->
            if (!state.selectedItemIds.contains(itemId)) return@update state
            val updatedPurchased = state.purchasedItemIds.toMutableSet()
            newPurchased = if (updatedPurchased.contains(itemId)) {
                updatedPurchased.remove(itemId)
                false
            } else {
                updatedPurchased.add(itemId)
                true
            }
            sessionId = state.activeShoppingSessionId
            state.copy(purchasedItemIds = updatedPurchased)
        }
        val sid = sessionId
        if (sid != null) {
            scope.launch(Dispatchers.IO) {
                sessionWriteMutex.withLock {
                    shoppingSessionRepository.setLinePurchased(sid, itemId, newPurchased)
                }
            }
        }
    }

    fun confirmSelection(onSuccess: () -> Unit) {
        scope.launch(Dispatchers.IO) {
            var createdSession = false
            sessionWriteMutex.withLock {
                val ids = _uiState.value.selectedItemIds.toList()
                if (ids.isEmpty()) return@withLock
                val sessionId = shoppingSessionRepository.createActiveSessionWithItems(ids)
                if (sessionId <= 0) return@withLock
                _uiState.update { it.copy(activeShoppingSessionId = sessionId, quickActionMessage = null) }
                createdSession = true
            }
            if (createdSession) {
                withContext(Dispatchers.Main) { onSuccess() }
            }
        }
    }

    fun finishShopping(onComplete: () -> Unit) {
        scope.launch(Dispatchers.IO) {
            sessionWriteMutex.withLock {
                val sid = _uiState.value.activeShoppingSessionId
                val purchased = _uiState.value.purchasedItemIds
                val selectedIds = _uiState.value.selectedItemIds.toList()
                val total = selectedIds.size
                val purchasedCount = purchased.size
                if (sid != null && total > 0) {
                    shoppingSessionRepository.completeSessionAndApplyItemStats(
                        sessionId = sid,
                        purchasedItemIds = purchased,
                        allSelectedItemIds = selectedIds,
                        totalItemCount = total,
                        purchasedCount = purchasedCount
                    )
                }
                _uiState.update {
                    it.copy(
                        activeShoppingSessionId = null,
                        selectedItemIds = emptySet(),
                        purchasedItemIds = emptySet(),
                        shoppingFilter = ShoppingFilter.ALL
                    )
                }
            }
            withContext(Dispatchers.Main) { onComplete() }
        }
    }

    fun cancelActiveSessionIfNeeded(onDone: () -> Unit = {}) {
        val sid = _uiState.value.activeShoppingSessionId
        if (sid == null) {
            onDone()
            return
        }
        scope.launch(Dispatchers.IO) {
            sessionWriteMutex.withLock {
                shoppingSessionRepository.cancelActiveSessionIfNeeded(sid)
                _uiState.update {
                    it.copy(
                        activeShoppingSessionId = null,
                        purchasedItemIds = emptySet(),
                        shoppingFilter = ShoppingFilter.ALL
                    )
                }
            }
            withContext(Dispatchers.Main) { onDone() }
        }
    }

    /**
     * Aligns in-memory purchased flags with [shopping_session_items] (e.g. after navigation).
     */
    suspend fun hydratePurchasedFromDatabase() {
        sessionWriteMutex.withLock {
            val sid = _uiState.value.activeShoppingSessionId ?: return@withLock
            val rows = shoppingSessionRepository.getSessionItems(sid)
            val purchased = rows.filter { it.isPurchased }.map { it.itemId }.toSet()
            _uiState.update { it.copy(purchasedItemIds = purchased) }
        }
    }

    fun selectedItemsGroupedByCategory(): Map<String, List<GroceryItem>> {
        val selected = _uiState.value.itemsByCategory.values.flatten()
            .filter { _uiState.value.selectedItemIds.contains(it.id) }
        return selected.groupBy { it.category }
    }

    /**
     * Deletes all Room data and resets in-memory UI state. Safe if a shopping session is active.
     */
    fun clearAllLocalData(onComplete: () -> Unit = {}) {
        scope.launch(Dispatchers.IO) {
            sessionWriteMutex.withLock {
                val sid = _uiState.value.activeShoppingSessionId
                if (sid != null) {
                    runCatching {
                        shoppingSessionRepository.cancelActiveSessionIfNeeded(sid)
                    }
                }
                localDataRepository.clearAllLocalData()
                _uiState.value = GroceryUiState()
            }
            withContext(Dispatchers.Main) { onComplete() }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}
