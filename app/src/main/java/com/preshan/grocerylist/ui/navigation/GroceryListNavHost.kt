package com.preshan.grocerylist.ui.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.preshan.grocerylist.data.AppSettingKeys
import com.preshan.grocerylist.data.local.database.DatabaseProvider
import com.preshan.grocerylist.data.repository.AppSettingsRepository
import com.preshan.grocerylist.data.repository.CatalogImportExportRepository
import com.preshan.grocerylist.data.repository.CsvImportOptions
import com.preshan.grocerylist.data.repository.CsvImportResult
import com.preshan.grocerylist.data.repository.CsvImportSummary
import com.preshan.grocerylist.ui.screens.home.HomeScreen
import com.preshan.grocerylist.ui.screens.locale.LocaleSetupScreen
import com.preshan.grocerylist.ui.screens.locale.LocaleSetupViewModel
import com.preshan.grocerylist.ui.screens.managecategories.ManageCategoriesScreen
import com.preshan.grocerylist.ui.screens.managecategories.ManageCategoriesViewModel
import com.preshan.grocerylist.ui.screens.manageitems.ManageItemsScreen
import com.preshan.grocerylist.ui.screens.manageitems.ManageItemsViewModel
import com.preshan.grocerylist.ui.screens.quickselect.QuickSelectScreen
import com.preshan.grocerylist.ui.screens.settings.PrivacyPolicyScreen
import com.preshan.grocerylist.ui.screens.settings.SettingsScreen
import com.preshan.grocerylist.ui.screens.shopping.ShoppingModeScreen
import com.preshan.grocerylist.util.AppLanguage
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider
import com.preshan.grocerylist.util.layoutDirectionFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.text.Charsets

@Composable
fun GroceryListNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: GroceryListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var navReady by remember { mutableStateOf(false) }
    var startDestination by remember { mutableStateOf(AppRoutes.HOME) }
    var appLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }

    LaunchedEffect(Unit) {
        val db = DatabaseProvider.getDatabase(context)
        val repo = AppSettingsRepository(db.appSettingDao())
        repo.migrateLegacyInstallIfNeeded()
        val completed =
            repo.getSetting(AppSettingKeys.FIRST_LAUNCH_COMPLETED)?.value == "true"
        val storedLanguage =
            repo.getSetting(AppSettingKeys.SELECTED_LANGUAGE)?.value
        appLanguage = AppLanguage.fromStoredValue(storedLanguage)
        startDestination = if (completed) AppRoutes.HOME else AppRoutes.LOCALE_SETUP_FIRST
        navReady = true
    }

    if (!navReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    androidx.compose.runtime.CompositionLocalProvider(
        AppTextProvider.LocalAppLanguage provides appLanguage,
        LocalLayoutDirection provides layoutDirectionFor(appLanguage)
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
        composable(AppRoutes.LOCALE_SETUP_FIRST) {
            val localeVm: LocaleSetupViewModel = viewModel()
            LocaleSetupScreen(
                fromSettings = false,
                viewModel = localeVm,
                onContinue = {
                    localeVm.saveAndCompleteFirstLaunch {
                        appLanguage = AppLanguage.fromStoredValue(localeVm.language)
                        viewModel.onFirstLaunchLocaleCompleted()
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.LOCALE_SETUP_FIRST) { inclusive = true }
                        }
                    }
                },
                onBackFromSettings = {}
            )
        }
        composable(AppRoutes.LOCALE_SETUP_EDIT) {
            val localeVm: LocaleSetupViewModel = viewModel()
            LocaleSetupScreen(
                fromSettings = true,
                viewModel = localeVm,
                onContinue = {
                    localeVm.saveEdits {
                        appLanguage = AppLanguage.fromStoredValue(localeVm.language)
                        navController.popBackStack()
                    }
                },
                onBackFromSettings = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.HOME) {
            HomeScreen(
                onPrepareShoppingListClick = { navController.navigate(AppRoutes.QUICK_SELECT) },
                onFavoritesClick = {
                    viewModel.selectFavoritesFromDatabase()
                    navController.navigate(AppRoutes.QUICK_SELECT)
                },
                onFrequentClick = {
                    viewModel.selectFrequentFromDatabase {
                        navController.navigate(AppRoutes.QUICK_SELECT)
                    }
                },
                onManageItemsClick = { navController.navigate(AppRoutes.MANAGE_ITEMS) },
                onSettingsClick = { navController.navigate(AppRoutes.SETTINGS) }
            )
        }
        composable(AppRoutes.MANAGE_ITEMS) {
            val manageVm: ManageItemsViewModel = viewModel()
            ManageItemsScreen(
                viewModel = manageVm,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.MANAGE_CATEGORIES) {
            val categoriesVm: ManageCategoriesViewModel = viewModel()
            ManageCategoriesScreen(
                viewModel = categoriesVm,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.QUICK_SELECT) {
            QuickSelectScreen(
                uiState = uiState,
                onCategorySelected = viewModel::selectCategory,
                onItemToggle = viewModel::toggleItemSelection,
                onToggleFavorite = viewModel::toggleFavorite,
                onSelectAll = viewModel::selectAllCurrentCategory,
                onSelectFavorites = viewModel::selectFavoritesFromDatabase,
                onSelectFrequent = viewModel::selectFrequentFromDatabase,
                onClear = viewModel::clearQuickSelectSelections,
                onConfirmList = {
                    viewModel.confirmSelection {
                        navController.navigate(AppRoutes.SHOPPING_MODE)
                    }
                },
                onDismissQuickMessage = viewModel::dismissQuickActionMessage,
                renameCatalogItem = viewModel::renameCatalogItem,
                setItemFavorite = viewModel::setItemFavorite,
                deleteCatalogItem = viewModel::deleteCatalogItem
            )
        }
        composable(AppRoutes.SHOPPING_MODE) {
            LaunchedEffect(uiState.activeShoppingSessionId) {
                if (uiState.activeShoppingSessionId != null) {
                    viewModel.hydratePurchasedFromDatabase()
                }
            }
            BackHandler {
                viewModel.cancelActiveSessionIfNeeded {
                    navController.popBackStack()
                }
            }
            ShoppingModeScreen(
                uiState = uiState,
                selectedItemsByCategory = viewModel.selectedItemsGroupedByCategory(),
                onFilterChange = viewModel::setShoppingFilter,
                onTogglePurchased = viewModel::togglePurchased,
                onFinishShopping = {
                    viewModel.finishShopping {
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.HOME) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(AppRoutes.SETTINGS) {
            val scope = rememberCoroutineScope()
            val csvRepo = remember {
                CatalogImportExportRepository(DatabaseProvider.getDatabase(context))
            }
            var pendingImportOptions by remember { mutableStateOf<CsvImportOptions?>(null) }
            var importSummary by remember { mutableStateOf<CsvImportSummary?>(null) }

            val createExportDoc = rememberLauncherForActivityResult(
                ActivityResultContracts.CreateDocument("text/csv")
            ) { uri ->
                if (uri == null) return@rememberLauncherForActivityResult
                scope.launch(Dispatchers.IO) {
                    val ok = runCatching {
                        val csv = csvRepo.buildExportCsvUtf8()
                        context.contentResolver.openOutputStream(uri)?.use { os ->
                            os.bufferedWriter(Charsets.UTF_8).use { writer ->
                                writer.write(csv)
                            }
                        } != null
                    }.getOrDefault(false)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            AppTextProvider.getText(
                                if (ok) AppTextKey.EXPORT_SUCCESS else AppTextKey.EXPORT_FAILED,
                                appLanguage
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            val openImportDoc = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri ->
                val opts = pendingImportOptions
                pendingImportOptions = null
                if (uri == null || opts == null) return@rememberLauncherForActivityResult
                scope.launch(Dispatchers.IO) {
                    val text = runCatching {
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            input.bufferedReader(Charsets.UTF_8).readText()
                        } ?: ""
                    }.getOrDefault("")
                    val result = runCatching {
                        when {
                            text.isBlank() -> CsvImportResult.EmptyFile
                            else -> csvRepo.importFromCsvUtf8(text, opts)
                        }
                    }.getOrElse {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                AppTextProvider.getText(AppTextKey.IMPORT_FAILED, appLanguage),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@launch
                    }
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is CsvImportResult.Success -> {
                                Toast.makeText(
                                    context,
                                    AppTextProvider.getText(AppTextKey.IMPORT_SUCCESS, appLanguage),
                                    Toast.LENGTH_SHORT
                                ).show()
                                importSummary = result.summary
                            }
                            CsvImportResult.InvalidHeader -> {
                                Toast.makeText(
                                    context,
                                    AppTextProvider.getText(
                                        AppTextKey.IMPORT_INVALID_CSV_HEADER,
                                        appLanguage
                                    ),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            CsvImportResult.EmptyFile -> {
                                Toast.makeText(
                                    context,
                                    AppTextProvider.getText(AppTextKey.IMPORT_EMPTY_FILE, appLanguage),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }

            importSummary?.let { s ->
                val lineSep = "\n"
                val summaryText = buildString {
                    append(AppTextProvider.getText(AppTextKey.CATEGORIES_ADDED, appLanguage))
                    append(": ")
                    append(s.categoriesAdded)
                    append(lineSep)
                    append(AppTextProvider.getText(AppTextKey.ITEMS_ADDED, appLanguage))
                    append(": ")
                    append(s.itemsAdded)
                    append(lineSep)
                    append(AppTextProvider.getText(AppTextKey.ITEMS_UPDATED, appLanguage))
                    append(": ")
                    append(s.itemsUpdated)
                    append(lineSep)
                    append(AppTextProvider.getText(AppTextKey.DUPLICATES_SKIPPED, appLanguage))
                    append(": ")
                    append(s.duplicatesSkipped)
                    append(lineSep)
                    append(AppTextProvider.getText(AppTextKey.ROWS_FAILED, appLanguage))
                    append(": ")
                    append(s.rowsFailed)
                }
                AlertDialog(
                    onDismissRequest = { importSummary = null },
                    title = {
                        Text(AppTextProvider.getText(AppTextKey.IMPORT_SUMMARY, appLanguage))
                    },
                    text = { Text(summaryText) },
                    confirmButton = {
                        TextButton(onClick = { importSummary = null }) {
                            Text(AppTextProvider.getText(AppTextKey.OK, appLanguage))
                        }
                    }
                )
            }

            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onManageItemsClick = { navController.navigate(AppRoutes.MANAGE_ITEMS) },
                onManageCategoriesClick = {
                    navController.navigate(AppRoutes.MANAGE_CATEGORIES)
                },
                onConfirmRestoreDefaultItems = {
                    viewModel.restoreDefaultItems { result ->
                        val template = AppTextProvider.getText(
                            AppTextKey.TOAST_RESTORE_DEFAULTS_SUMMARY,
                            appLanguage
                        )
                        val message = String.format(
                            template,
                            result.categoriesInserted + result.categoriesReactivated,
                            result.itemsInserted + result.itemsReactivated
                        )
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                },
                onResetFrequentItemDataClick = {
                    viewModel.resetFrequentItemData {
                        Toast.makeText(
                            context,
                            AppTextProvider.getText(
                                AppTextKey.TOAST_FREQUENT_RESET,
                                appLanguage
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onClearCurrentShoppingListClick = {
                    viewModel.cancelActiveSessionIfNeeded()
                    viewModel.clearAllSelections()
                    Toast.makeText(
                        context,
                        AppTextProvider.getText(AppTextKey.TOAST_LIST_CLEARED, appLanguage),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onConfirmClearAllLocalData = {
                    viewModel.clearAllLocalData {
                        appLanguage = AppLanguage.ENGLISH
                        Toast.makeText(
                            context,
                            AppTextProvider.getText(
                                AppTextKey.TOAST_CLEAR_ALL_SUCCESS,
                                AppLanguage.ENGLISH
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate(AppRoutes.LOCALE_SETUP_FIRST) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                },
                onPrivacyPolicyClick = { navController.navigate(AppRoutes.PRIVACY_POLICY) },
                onAdPrivacyOptionsPlaceholderClick = {
                    Toast.makeText(
                        context,
                        AppTextProvider.getText(
                            AppTextKey.TOAST_AD_PRIVACY_PLACEHOLDER,
                            appLanguage
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onCountryRegionLanguageClick = {
                    navController.navigate(AppRoutes.LOCALE_SETUP_EDIT)
                },
                onExportConfirmed = {
                    createExportDoc.launch("grocery_catalog_${System.currentTimeMillis()}.csv")
                },
                onImportChooseFile = { opts ->
                    pendingImportOptions = opts
                    openImportDoc.launch(
                        arrayOf(
                            "text/csv",
                            "text/comma-separated-values",
                            "text/plain",
                            "application/octet-stream",
                            "*/*"
                        )
                    )
                }
            )
        }
        composable(AppRoutes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        }
    }
}
