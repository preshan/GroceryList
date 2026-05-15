package com.preshan.grocerylist.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.preshan.grocerylist.data.repository.CsvImportOptions
import com.preshan.grocerylist.ui.components.AppDivider
import com.preshan.grocerylist.ui.components.AppSectionCard
import com.preshan.grocerylist.ui.components.AppSettingRow
import com.preshan.grocerylist.ui.theme.GroceryListTheme
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider

data class SettingsRowAction(
    val title: String,
    val subtitle: String? = null,
    val onClick: (() -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onManageItemsClick: () -> Unit = {},
    onManageCategoriesClick: () -> Unit = {},
    onConfirmRestoreDefaultItems: () -> Unit = {},
    onResetFrequentItemDataClick: () -> Unit = {},
    onClearCurrentShoppingListClick: () -> Unit = {},
    onConfirmClearAllLocalData: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onAdPrivacyOptionsPlaceholderClick: () -> Unit = {},
    onCountryRegionLanguageClick: () -> Unit = {},
    onExportConfirmed: () -> Unit = {},
    onImportChooseFile: (CsvImportOptions) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lang = AppTextProvider.LocalAppLanguage.current
    var showClearAllConfirm by remember { mutableStateOf(false) }
    var showRestoreDefaultsConfirm by remember { mutableStateOf(false) }
    var showExportInfo by remember { mutableStateOf(false) }
    var showImportOptions by remember { mutableStateOf(false) }
    var importFavorites by remember { mutableStateOf(true) }
    var importFrequent by remember { mutableStateOf(true) }
    var importRemoved by remember { mutableStateOf(true) }

    if (showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirm = false },
            title = { Text(AppTextProvider.getText(AppTextKey.CLEAR_ALL_DATA_CONFIRM_TITLE, lang)) },
            text = {
                Text(AppTextProvider.getText(AppTextKey.CLEAR_ALL_DATA_CONFIRM_MESSAGE, lang))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearAllConfirm = false
                        onConfirmClearAllLocalData()
                    }
                ) {
                    Text(AppTextProvider.getText(AppTextKey.CONTINUE, lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirm = false }) {
                    Text(AppTextProvider.getText(AppTextKey.CANCEL, lang))
                }
            }
        )
    }

    if (showExportInfo) {
        AlertDialog(
            onDismissRequest = { showExportInfo = false },
            title = { Text(AppTextProvider.getText(AppTextKey.EXPORT_DATA, lang)) },
            text = { Text(AppTextProvider.getText(AppTextKey.EXPORT_DATA_MESSAGE, lang)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExportInfo = false
                        onExportConfirmed()
                    }
                ) {
                    Text(AppTextProvider.getText(AppTextKey.SAVE_CSV_FILE, lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportInfo = false }) {
                    Text(AppTextProvider.getText(AppTextKey.CANCEL, lang))
                }
            }
        )
    }

    if (showImportOptions) {
        AlertDialog(
            onDismissRequest = { showImportOptions = false },
            title = { Text(AppTextProvider.getText(AppTextKey.CHOOSE_IMPORT_OPTIONS, lang)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            AppTextProvider.getText(AppTextKey.IMPORT_FAVORITES, lang),
                            modifier = Modifier.weight(1f)
                        )
                        Switch(checked = importFavorites, onCheckedChange = { importFavorites = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            AppTextProvider.getText(AppTextKey.IMPORT_FREQUENT_DATA, lang),
                            modifier = Modifier.weight(1f)
                        )
                        Switch(checked = importFrequent, onCheckedChange = { importFrequent = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            AppTextProvider.getText(AppTextKey.IMPORT_REMOVED_ITEMS, lang),
                            modifier = Modifier.weight(1f)
                        )
                        Switch(checked = importRemoved, onCheckedChange = { importRemoved = it })
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportOptions = false
                        onImportChooseFile(
                            CsvImportOptions(
                                importFavorites = importFavorites,
                                importFrequentData = importFrequent,
                                importInactiveItems = importRemoved
                            )
                        )
                    }
                ) {
                    Text(AppTextProvider.getText(AppTextKey.CHOOSE_CSV_FILE, lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportOptions = false }) {
                    Text(AppTextProvider.getText(AppTextKey.CANCEL, lang))
                }
            }
        )
    }

    if (showRestoreDefaultsConfirm) {
        AlertDialog(
            onDismissRequest = { showRestoreDefaultsConfirm = false },
            title = { Text(AppTextProvider.getText(AppTextKey.RESTORE_DEFAULT_ITEMS_CONFIRM_TITLE, lang)) },
            text = {
                Text(AppTextProvider.getText(AppTextKey.RESTORE_DEFAULT_ITEMS_CONFIRM_MESSAGE, lang))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestoreDefaultsConfirm = false
                        onConfirmRestoreDefaultItems()
                    }
                ) {
                    Text(AppTextProvider.getText(AppTextKey.RESTORE, lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDefaultsConfirm = false }) {
                    Text(AppTextProvider.getText(AppTextKey.CANCEL, lang))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(AppTextProvider.getText(AppTextKey.SETTINGS, lang)) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(AppTextProvider.getText(AppTextKey.BACK, lang))
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingsSectionCard(
                    title = AppTextProvider.getText(AppTextKey.COUNTRY_REGION_LANGUAGE, lang),
                    rows = listOf(
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.COUNTRY_REGION, lang),
                            onClick = onCountryRegionLanguageClick
                        ),
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.LANGUAGE, lang),
                            onClick = onCountryRegionLanguageClick
                        )
                    )
                )
            }
            item {
                SettingsSectionCard(
                    title = AppTextProvider.getText(AppTextKey.CATALOGUE, lang),
                    rows = listOf(
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.MANAGE_ITEMS, lang),
                            onClick = onManageItemsClick
                        ),
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.MANAGE_CATEGORIES, lang),
                            AppTextProvider.getText(AppTextKey.MANAGE_CATEGORIES_SUBTITLE, lang),
                            onManageCategoriesClick
                        ),
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.RESTORE_DEFAULT_ITEMS, lang),
                            AppTextProvider.getText(AppTextKey.RESTORE_DEFAULTS_SUBTITLE, lang),
                            onClick = {
                            showRestoreDefaultsConfirm = true
                        })
                    )
                )
            }
            item {
                SettingsSectionCard(
                    title = AppTextProvider.getText(AppTextKey.SHOPPING, lang),
                    rows = listOf(
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.RESET_FREQUENT_ITEM_DATA, lang),
                            AppTextProvider.getText(AppTextKey.RESET_FREQUENT_SUBTITLE, lang),
                            onResetFrequentItemDataClick
                        )
                    )
                )
            }
            item {
                SettingsSectionCard(
                    title = AppTextProvider.getText(AppTextKey.DATA, lang),
                    rows = listOf(
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.EXPORT_DATA, lang),
                            AppTextProvider.getText(AppTextKey.EXPORT_DATA_SUBTITLE, lang),
                            onClick = { showExportInfo = true }
                        ),
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.IMPORT_DATA, lang),
                            AppTextProvider.getText(AppTextKey.IMPORT_DATA_SUBTITLE, lang),
                            onClick = {
                                importFavorites = true
                                importFrequent = true
                                importRemoved = true
                                showImportOptions = true
                            }
                        ),
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.CLEAR_CURRENT_SHOPPING_LIST, lang),
                            onClick = onClearCurrentShoppingListClick
                        ),
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.CLEAR_ALL_LOCAL_DATA, lang),
                            AppTextProvider.getText(AppTextKey.CLEAR_ALL_DATA_SUBTITLE, lang),
                            onClick = { showClearAllConfirm = true }
                        )
                    )
                )
            }
            item {
                SettingsSectionCard(
                    title = AppTextProvider.getText(AppTextKey.PRIVACY, lang),
                    rows = listOf(
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.PRIVACY_POLICY, lang),
                            AppTextProvider.getText(AppTextKey.PRIVACY_POLICY_SUBTITLE, lang),
                            onPrivacyPolicyClick
                        ),
                        SettingsRowAction(
                            AppTextProvider.getText(AppTextKey.AD_PRIVACY_OPTIONS, lang),
                            null,
                            onAdPrivacyOptionsPlaceholderClick
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    rows: List<SettingsRowAction>
) {
    AppSectionCard(title = title) {
        rows.forEachIndexed { index, row ->
            AppSettingRow(
                title = row.title,
                subtitle = row.subtitle,
                onClick = row.onClick
            )
            if (index != rows.lastIndex) {
                AppDivider()
            }
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun SettingsScreenPreviewLight() {
    GroceryListTheme(darkTheme = false) { SettingsScreen() }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun SettingsScreenPreviewDark() {
    GroceryListTheme(darkTheme = true) { SettingsScreen() }
}
