package com.preshan.grocerylist.ui.screens.quickselect

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.preshan.grocerylist.ui.components.AppPrimaryButton
import com.preshan.grocerylist.ui.navigation.GroceryItem
import com.preshan.grocerylist.ui.navigation.GroceryUiState
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider
import com.preshan.grocerylist.ui.theme.GroceryListTheme
import kotlinx.coroutines.launch

// Category chip labels and section headers are localized via `AppTextProvider`.

/** Matches [com.preshan.grocerylist.ui.components.AppPrimaryButton] height. */
private val QuickSelectSheetButtonHeight = 52.dp

private sealed class QuickSelectRow {
    data class SectionHeader(val title: String) : QuickSelectRow()
    data class ItemEntry(val item: GroceryItem) : QuickSelectRow()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSelectScreen(
    uiState: GroceryUiState,
    onCategorySelected: (String?) -> Unit,
    onItemToggle: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onSelectAll: () -> Unit,
    onSelectFavorites: () -> Unit,
    onSelectFrequent: () -> Unit,
    onClear: () -> Unit,
    onConfirmList: () -> Unit,
    onDismissQuickMessage: () -> Unit = {},
    renameCatalogItem: suspend (Long, String) -> String?,
    setItemFavorite: (Long, Boolean) -> Unit,
    deleteCatalogItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val selectedCount = uiState.selectedItemIds.size
    val lang = AppTextProvider.LocalAppLanguage.current
    fun localizedCategoryName(name: String): String {
        return when (name) {
            "Food & Grocery" -> AppTextProvider.getText(AppTextKey.FOOD_GROCERY, lang)
            "Vegetables" -> AppTextProvider.getText(AppTextKey.VEGETABLES, lang)
            "Fruits" -> AppTextProvider.getText(AppTextKey.FRUITS, lang)
            "Meat Shop" -> AppTextProvider.getText(AppTextKey.MEAT_SHOP, lang)
            "Health & Pharmacy" -> AppTextProvider.getText(AppTextKey.HEALTH_PHARMACY, lang)
            "Household & Personal Care" -> AppTextProvider.getText(
                AppTextKey.HOUSEHOLD_PERSONAL_CARE,
                lang
            )
            else -> name
        }
    }
    var searchQuery by remember { mutableStateOf("") }

    var sheetItem by remember { mutableStateOf<GroceryItem?>(null) }
    var deleteConfirmFor by remember { mutableStateOf<GroceryItem?>(null) }
    var editName by remember { mutableStateOf("") }
    var editFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(sheetItem?.id) {
        val item = sheetItem
        if (item != null) {
            editName = item.name
            editFavorite = item.isFavorite
        }
    }

    val listEntries: List<QuickSelectRow> = remember(
        uiState.selectedCategory,
        uiState.itemsByCategory,
        uiState.categories,
        searchQuery
    ) {
        when {
            searchQuery.isNotBlank() -> {
                buildList<QuickSelectRow> {
                    uiState.categories.forEach { categoryName ->
                        uiState.itemsByCategory[categoryName].orEmpty()
                            .filter { it.name.contains(searchQuery, ignoreCase = true) }
                            .forEach { add(QuickSelectRow.ItemEntry(it)) }
                    }
                }
            }
            uiState.selectedCategory == null -> {
                buildList<QuickSelectRow> {
                    uiState.categories.forEach { categoryName ->
                        val catItems = uiState.itemsByCategory[categoryName].orEmpty()
                        if (catItems.isNotEmpty()) {
                            add(QuickSelectRow.SectionHeader(localizedCategoryName(categoryName)))
                            catItems.forEach { add(QuickSelectRow.ItemEntry(it)) }
                        }
                    }
                }
            }
            else -> {
                uiState.itemsByCategory[uiState.selectedCategory].orEmpty()
                    .map { QuickSelectRow.ItemEntry(it) }
            }
        }
    }

    deleteConfirmFor?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteConfirmFor = null },
            title = { Text(AppTextProvider.getText(AppTextKey.REMOVE_ITEM_CONFIRM_TITLE, lang)) },
            text = {
                Text(
                    String.format(
                        AppTextProvider.getText(AppTextKey.REMOVE_ITEM_CONFIRM_MESSAGE, lang),
                        target.name
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteCatalogItem(target.id)
                        deleteConfirmFor = null
                        sheetItem = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(AppTextProvider.getText(AppTextKey.DELETE, lang))
                }
            },
            dismissButton = { TextButton(onClick = { deleteConfirmFor = null }) { Text(AppTextProvider.getText(AppTextKey.CANCEL, lang)) } }
        )
    }

    sheetItem?.let { activeItem ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { sheetItem = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = AppTextProvider.getText(AppTextKey.EDIT_ITEM, lang),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = AppTextProvider.getText(AppTextKey.CATEGORY, lang),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = activeItem.category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    label = { Text(AppTextProvider.getText(AppTextKey.ITEM_NAME, lang)) }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (editFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = AppTextProvider.getText(AppTextKey.FAVORITES, lang),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = AppTextProvider.getText(AppTextKey.FAVORITES_QUICK_FILTER_HELP, lang),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = editFavorite,
                        onCheckedChange = { editFavorite = it }
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                AppPrimaryButton(
                    text = AppTextProvider.getText(AppTextKey.SAVE, lang),
                    onClick = {
                        scope.launch {
                            val err = renameCatalogItem(activeItem.id, editName)
                            if (err != null) {
                                val translatedErr = when (err) {
                                    "Name is required." ->
                                        AppTextProvider.getText(AppTextKey.ITEM_NAME_REQUIRED, lang)
                                    "An item with this name already exists in this category." ->
                                        AppTextProvider.getText(AppTextKey.DUPLICATE_ITEM_MESSAGE, lang)
                                    "Item not found." ->
                                        AppTextProvider.getText(AppTextKey.TOAST_ITEM_NOT_FOUND, lang)
                                    else -> err
                                }
                                Toast.makeText(context, translatedErr, Toast.LENGTH_SHORT).show()
                            } else {
                                setItemFavorite(activeItem.id, editFavorite)
                                sheetItem = null
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { deleteConfirmFor = activeItem },
                        modifier = Modifier
                            .weight(1f)
                            .height(QuickSelectSheetButtonHeight),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = AppTextProvider.getText(AppTextKey.DELETE, lang),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    OutlinedButton(
                        onClick = { sheetItem = null },
                        modifier = Modifier
                            .weight(1f)
                            .height(QuickSelectSheetButtonHeight),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(AppTextProvider.getText(AppTextKey.CANCEL, lang), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Surface(tonalElevation = 3.dp, shadowElevation = 3.dp) {
                AppPrimaryButton(
                    text = run {
                        val confirm = AppTextProvider.getText(AppTextKey.CONFIRM_LIST, lang)
                        if (selectedCount == 0) {
                            confirm
                        } else {
                            "$confirm ($selectedCount ${AppTextProvider.getText(AppTextKey.SELECTED_ITEMS_COUNT, lang)})"
                        }
                    },
                    onClick = onConfirmList,
                    enabled = selectedCount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text(AppTextProvider.getText(AppTextKey.SEARCH_ITEMS, lang)) }
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = AppTextProvider.getText(AppTextKey.CATEGORIES, lang),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickCategoryChip(
                        label = AppTextProvider.getText(AppTextKey.ALL_CATEGORIES, lang),
                        selected = uiState.selectedCategory == null,
                        onClick = { onCategorySelected(null) }
                    )
                    uiState.categories.forEach { category ->
                        QuickCategoryChip(
                            label = when (category) {
                                "Food & Grocery" -> AppTextProvider.getText(AppTextKey.FOOD_GROCERY, lang)
                                "Vegetables" -> AppTextProvider.getText(AppTextKey.VEGETABLES, lang)
                                "Fruits" -> AppTextProvider.getText(AppTextKey.FRUITS, lang)
                                "Meat Shop" -> AppTextProvider.getText(AppTextKey.MEAT_SHOP, lang)
                                "Health & Pharmacy" -> AppTextProvider.getText(AppTextKey.HEALTH_PHARMACY, lang)
                                "Household & Personal Care" -> AppTextProvider.getText(
                                    AppTextKey.HOUSEHOLD_PERSONAL_CARE,
                                    lang
                                )
                                else -> category
                            },
                            selected = category == uiState.selectedCategory,
                            onClick = { onCategorySelected(category) }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = AppTextProvider.getText(AppTextKey.FILTER_BY, lang),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickFilterActionChip(
                        AppTextProvider.getText(AppTextKey.ALL, lang),
                        onSelectAll
                    )
                    QuickFilterActionChip(
                        AppTextProvider.getText(AppTextKey.CLEAR, lang),
                        onClear
                    )
                    QuickFilterActionChip(
                        AppTextProvider.getText(AppTextKey.FAVORITES, lang),
                        onSelectFavorites
                    )
                    QuickFilterActionChip(
                        AppTextProvider.getText(AppTextKey.FREQUENT_ITEMS, lang),
                        onSelectFrequent
                    )
                }
            }

            uiState.quickActionMessage?.let { msg ->
                val translatedMsg = when (msg) {
                    "No favorites yet. Tap the star on a row to mark favorites." ->
                        AppTextProvider.getText(AppTextKey.NO_FAVORITE_ITEMS, lang)
                    "No frequent items yet (purchase an item 3+ times)." ->
                        AppTextProvider.getText(AppTextKey.NO_FREQUENT_ITEMS, lang)
                    else -> msg
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onDismissQuickMessage),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = translatedMsg,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    count = listEntries.size,
                    key = { index ->
                        when (val entry = listEntries[index]) {
                            is QuickSelectRow.SectionHeader -> "hdr_${entry.title}"
                            is QuickSelectRow.ItemEntry -> "item_${entry.item.id}"
                        }
                    },
                    span = { index ->
                        when (listEntries[index]) {
                            is QuickSelectRow.SectionHeader -> GridItemSpan(maxLineSpan)
                            is QuickSelectRow.ItemEntry -> GridItemSpan(1)
                        }
                    }
                ) { index ->
                    when (val entry = listEntries[index]) {
                        is QuickSelectRow.SectionHeader -> {
                            val topPad = if (index == 0) 4.dp else 10.dp
                            Text(
                                text = entry.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = topPad, bottom = 2.dp, start = 2.dp, end = 2.dp)
                            )
                        }
                        is QuickSelectRow.ItemEntry -> {
                            SelectableItemRow(
                                item = entry.item,
                                isSelected = uiState.selectedItemIds.contains(entry.item.id),
                                onRowClick = { onItemToggle(entry.item.id) },
                                onLongPress = { sheetItem = entry.item }
                            )
                        }
                    }
                }
            }
        }
    }
}

private val QuickChipShape = RoundedCornerShape(10.dp)

@Composable
private fun QuickCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = QuickChipShape,
        color = if (selected) scheme.primaryContainer else scheme.surface,
        contentColor = if (selected) scheme.onPrimaryContainer else scheme.onSurface,
        border = BorderStroke(1.dp, scheme.primary)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun QuickFilterActionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = QuickChipShape,
        color = scheme.surface,
        contentColor = scheme.onSurface,
        border = BorderStroke(1.dp, scheme.outlineVariant)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SelectableItemRow(
    item: GroceryItem,
    isSelected: Boolean,
    onRowClick: () -> Unit,
    onLongPress: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onRowClick,
                    onLongClick = onLongPress
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun QuickSelectScreenPreviewLight() {
    GroceryListTheme(darkTheme = false) {
        val previewItems = mapOf(
            "Rice & Grains" to listOf(
                GroceryItem(1L, "සම්බ සහල්", "Rice & Grains", "සම්බ සහල්", isFavorite = true, purchaseCount = 0),
                GroceryItem(2L, "රතු හාල්", "Rice & Grains", "රතු හාල්", isFavorite = false, purchaseCount = 4)
            ),
            "Vegetables" to listOf(
                GroceryItem(3L, "Carrot", "Vegetables", "Carrot", isFavorite = false, purchaseCount = 1)
            )
        )
        QuickSelectScreen(
            uiState = GroceryUiState(
                categories = listOf("Rice & Grains", "Vegetables"),
                itemsByCategory = previewItems,
                selectedCategory = null
            ),
            onCategorySelected = {},
            onItemToggle = {},
            onToggleFavorite = {},
            onSelectAll = {},
            onSelectFavorites = {},
            onSelectFrequent = {},
            onClear = {},
            onConfirmList = {},
            renameCatalogItem = { _, _ -> null },
            setItemFavorite = { _, _ -> },
            deleteCatalogItem = { }
        )
    }
}
