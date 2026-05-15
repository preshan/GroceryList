package com.preshan.grocerylist.ui.screens.shopping

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.preshan.grocerylist.ui.navigation.GroceryItem
import com.preshan.grocerylist.ui.navigation.GroceryUiState
import com.preshan.grocerylist.ui.navigation.ShoppingFilter
import com.preshan.grocerylist.ui.theme.GroceryListTheme
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider
import com.preshan.grocerylist.util.CategoryDisplayNames
import com.preshan.grocerylist.util.ShoppingShareFormat
import com.preshan.grocerylist.util.buildShoppingListShareText
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingModeScreen(
    uiState: GroceryUiState,
    selectedItemsByCategory: Map<String, List<GroceryItem>>,
    onFilterChange: (ShoppingFilter) -> Unit,
    onTogglePurchased: (Long) -> Unit,
    onFinishShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = AppTextProvider.LocalAppLanguage.current
    val selectedIds = uiState.selectedItemIds
    val purchasedIds = uiState.purchasedItemIds
    val completedCount = purchasedIds.size
    val totalCount = selectedIds.size
    val progress = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount.toFloat()
    var showShareOptions by remember { mutableStateOf(false) }

    fun shareWithFormat(format: ShoppingShareFormat) {
        val text = buildShoppingListShareText(
            itemsByCategory = selectedItemsByCategory,
            purchasedItemIds = purchasedIds,
            format = format,
            language = lang
        )
        if (text == null) {
            Toast.makeText(
                context,
                AppTextProvider.getText(AppTextKey.NOTHING_TO_SHARE, lang),
                Toast.LENGTH_SHORT
            ).show()
            showShareOptions = false
            return
        }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooserTitle = AppTextProvider.getText(AppTextKey.SHARE_CHOOSER_TITLE, lang)
        context.startActivity(Intent.createChooser(send, chooserTitle))
        showShareOptions = false
    }

    if (showShareOptions) {
        val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showShareOptions = false },
            sheetState = shareSheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = AppTextProvider.getText(AppTextKey.SHARE_AS, lang),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
                Text(
                    text = AppTextProvider.getText(AppTextKey.SHARE_AS_HELP_TEXT, lang),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ShareSheetOptionRow(
                    label = AppTextProvider.getText(AppTextKey.ALL_ITEMS, lang),
                    onClick = { shareWithFormat(ShoppingShareFormat.ALL_ITEMS) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ShareSheetOptionRow(
                    label = AppTextProvider.getText(AppTextKey.PENDING_ONLY, lang),
                    onClick = { shareWithFormat(ShoppingShareFormat.PENDING_ONLY) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ShareSheetOptionRow(
                    label = AppTextProvider.getText(AppTextKey.PURCHASED_ONLY, lang),
                    onClick = { shareWithFormat(ShoppingShareFormat.PURCHASED_ONLY) }
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onFinishShopping,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(AppTextProvider.getText(AppTextKey.FINISH_SHOPPING, lang))
                    }
                    OutlinedButton(
                        onClick = { showShareOptions = true },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(AppTextProvider.getText(AppTextKey.SHARE, lang))
                    }
                }
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
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val completedTemplate =
                        AppTextProvider.getText(AppTextKey.COMPLETED_COUNT, lang)
                    val completedText = String.format(
                        Locale.ROOT,
                        completedTemplate,
                        completedCount,
                        totalCount
                    )
                    Text(completedText, style = MaterialTheme.typography.titleSmall)
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                }
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.shoppingFilter == ShoppingFilter.ALL,
                    onClick = { onFilterChange(ShoppingFilter.ALL) },
                    label = { Text(AppTextProvider.getText(AppTextKey.ALL, lang)) }
                )
                FilterChip(
                    selected = uiState.shoppingFilter == ShoppingFilter.PENDING,
                    onClick = { onFilterChange(ShoppingFilter.PENDING) },
                    label = { Text(AppTextProvider.getText(AppTextKey.PENDING, lang)) }
                )
                FilterChip(
                    selected = uiState.shoppingFilter == ShoppingFilter.PURCHASED,
                    onClick = { onFilterChange(ShoppingFilter.PURCHASED) },
                    label = { Text(AppTextProvider.getText(AppTextKey.PURCHASED, lang)) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedItemsByCategory.forEach { (category, itemsInCategory) ->
                    val filteredItems = itemsInCategory.filter { item ->
                        when (uiState.shoppingFilter) {
                            ShoppingFilter.ALL -> true
                            ShoppingFilter.PENDING -> !purchasedIds.contains(item.id)
                            ShoppingFilter.PURCHASED -> purchasedIds.contains(item.id)
                        }
                    }
                    if (filteredItems.isNotEmpty()) {
                        item(key = "header_$category") {
                            Text(
                                text = CategoryDisplayNames.localizedName(category, lang),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                            )
                        }
                        items(filteredItems, key = { it.id }) { item ->
                            val isPurchased = purchasedIds.contains(item.id)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTogglePurchased(item.id) },
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isPurchased) {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 13.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (isPurchased) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                        textDecoration = if (isPurchased) TextDecoration.LineThrough else TextDecoration.None,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = if (isPurchased) "✓" else "○",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isPurchased) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareSheetOptionRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp)
    )
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun ShoppingModeScreenPreviewLight() {
    GroceryListTheme(darkTheme = false) {
        val previewItem = GroceryItem(1L, "සම්බ සහල්", "Rice & Grains", "සම්බ සහල්")
        ShoppingModeScreen(
            uiState = GroceryUiState(
                selectedItemIds = setOf(previewItem.id),
                purchasedItemIds = emptySet()
            ),
            selectedItemsByCategory = mapOf("Rice & Grains" to listOf(previewItem)),
            onFilterChange = {},
            onTogglePurchased = {},
            onFinishShopping = {}
        )
    }
}
