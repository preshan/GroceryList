package com.preshan.grocerylist.ui.screens.manageitems

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.preshan.grocerylist.data.local.dao.ManageItemRow
import com.preshan.grocerylist.data.local.entity.CategoryEntity
import com.preshan.grocerylist.ui.components.AppPrimaryButton
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider
import kotlinx.coroutines.launch

/** Matches [com.preshan.grocerylist.ui.components.AppPrimaryButton] height. */
private val ManageItemsActionButtonHeight = 52.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageItemsScreen(
    viewModel: ManageItemsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = AppTextProvider.LocalAppLanguage.current
    val items by viewModel.displayedItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val pickerData by viewModel.pickerData.collectAsState()
    val pendingRemove by viewModel.pendingRemove.collectAsState()
    val pendingBulkRemoveIds by viewModel.pendingBulkRemoveIds.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val scope = rememberCoroutineScope()

    var bulkAddOpen by remember { mutableStateOf(false) }

    var editorOpen by remember { mutableStateOf(false) }
    var editingItemId by remember { mutableStateOf<Long?>(null) }

    var editorName by remember { mutableStateOf("") }
    var editorCategoryId by remember { mutableLongStateOf(0L) }
    var editorFavorite by remember { mutableStateOf(false) }

    fun openAdd() {
        val first = pickerData?.categories?.firstOrNull()
        if (first == null) {
            Toast.makeText(
                context,
                AppTextProvider.getText(AppTextKey.TOAST_STILL_LOADING, lang),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        editingItemId = null
        editorName = ""
        editorFavorite = false
        editorCategoryId = first.id
        editorOpen = true
    }

    fun openEdit(row: ManageItemRow) {
        scope.launch {
            val entity = viewModel.loadItemForEdit(row.id)
            if (entity == null) {
                Toast.makeText(
                    context,
                    AppTextProvider.getText(AppTextKey.TOAST_ITEM_NOT_FOUND, lang),
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            editingItemId = row.id
            editorName = entity.name
            editorCategoryId = entity.categoryId
            editorFavorite = entity.isFavorite
            editorOpen = true
        }
    }

    pendingRemove?.let { row ->
        AlertDialog(
            onDismissRequest = viewModel::dismissRemoveConfirm,
            title = { Text(AppTextProvider.getText(AppTextKey.REMOVE_ITEM_CONFIRM_TITLE, lang)) },
            text = {
                Text(
                    String.format(
                        AppTextProvider.getText(AppTextKey.REMOVE_ITEM_CONFIRM_MESSAGE, lang),
                        row.name
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmRemove) {
                    Text(AppTextProvider.getText(AppTextKey.REMOVE, lang))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissRemoveConfirm) {
                    Text(AppTextProvider.getText(AppTextKey.CANCEL, lang))
                }
            }
        )
    }

    pendingBulkRemoveIds?.let { ids ->
        AlertDialog(
            onDismissRequest = viewModel::dismissBulkRemoveConfirm,
            title = { Text(AppTextProvider.getText(AppTextKey.REMOVE_ITEM_CONFIRM_TITLE, lang)) },
            text = {
                Text(
                    AppTextProvider.getText(AppTextKey.REMOVE_SELECTED_ITEMS_CONFIRM_MESSAGE, lang)
                        .replace("%1\$d", ids.size.toString())
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmBulkRemove) {
                    Text(AppTextProvider.getText(AppTextKey.REMOVE, lang))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissBulkRemoveConfirm) {
                    Text(AppTextProvider.getText(AppTextKey.CANCEL, lang))
                }
            }
        )
    }

    if (bulkAddOpen) {
        val data = pickerData
        if (data == null || data.categories.isEmpty()) {
            AlertDialog(
                onDismissRequest = { bulkAddOpen = false },
                title = { Text(AppTextProvider.getText(AppTextKey.PLEASE_WAIT, lang)) },
                text = { Text(AppTextProvider.getText(AppTextKey.TOAST_STILL_LOADING, lang)) },
                confirmButton = {
                    TextButton(onClick = { bulkAddOpen = false }) {
                        Text(AppTextProvider.getText(AppTextKey.OK, lang))
                    }
                }
            )
        } else {
            BulkAddItemsDialog(
                categories = data.categories,
                onDismiss = { bulkAddOpen = false },
                onAdd = { categoryId, raw ->
                    scope.launch {
                        val result = viewModel.bulkAddCommaSeparated(categoryId, raw)
                        bulkAddOpen = false
                        val msg = buildString {
                            append(AppTextProvider.getText(AppTextKey.BULK_ADD_SUCCESS_MESSAGE, lang).replace("%1\$d", result.added.toString()))
                            if (result.skippedDuplicate > 0) {
                                append(AppTextProvider.getText(AppTextKey.BULK_ADD_SKIPPED_DUPLICATES, lang).replace("%1\$d", result.skippedDuplicate.toString()))
                            }
                            if (result.skippedEmpty > 0) {
                                append(AppTextProvider.getText(AppTextKey.BULK_ADD_SKIPPED_EMPTY, lang).replace("%1\$d", result.skippedEmpty.toString()))
                            }
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    if (editorOpen) {
        val data = pickerData
        if (data == null || data.categories.isEmpty()) {
            AlertDialog(
                onDismissRequest = { editorOpen = false },
                title = { Text(AppTextProvider.getText(AppTextKey.PLEASE_WAIT, lang)) },
                text = { Text(AppTextProvider.getText(AppTextKey.TOAST_STILL_LOADING, lang)) },
                confirmButton = {
                    TextButton(onClick = { editorOpen = false }) {
                        Text(AppTextProvider.getText(AppTextKey.OK, lang))
                    }
                }
            )
        } else {
            ItemEditorDialog(
                title = if (editingItemId == null) AppTextProvider.getText(AppTextKey.ADD_ITEM, lang) else AppTextProvider.getText(AppTextKey.EDIT_ITEM, lang),
                name = editorName,
                onNameChange = { editorName = it },
                categories = data.categories,
                selectedCategoryId = editorCategoryId,
                onCategorySelected = { editorCategoryId = it },
                favorite = editorFavorite,
                onFavoriteChange = { editorFavorite = it },
                isEditMode = editingItemId != null,
                onRemove = editingItemId?.let { id ->
                    {
                        editorOpen = false
                        viewModel.requestRemoveForEditor(id)
                    }
                },
                onDismiss = { editorOpen = false },
                onSave = {
                    if (editorCategoryId == 0L) {
                        Toast.makeText(
                            context,
                            AppTextProvider.getText(AppTextKey.CATEGORY_REQUIRED, lang),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        scope.launch {
                            val err = viewModel.saveItem(
                                existingId = editingItemId,
                                name = editorName,
                                categoryId = editorCategoryId,
                                favorite = editorFavorite
                            )
                            if (err != null) {
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            } else {
                                editorOpen = false
                            }
                        }
                    }
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(AppTextProvider.getText(AppTextKey.MANAGE_ITEMS, lang)) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(AppTextProvider.getText(AppTextKey.BACK, lang))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppPrimaryButton(
                    text = AppTextProvider.getText(AppTextKey.ADD_ITEM, lang),
                    onClick = { openAdd() },
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = { bulkAddOpen = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(ManageItemsActionButtonHeight),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(AppTextProvider.getText(AppTextKey.BULK_ADD, lang), style = MaterialTheme.typography.labelLarge)
                }
            }

            OutlinedButton(
                onClick = { viewModel.setSelectionMode(!selectionMode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ManageItemsActionButtonHeight),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (selectionMode) AppTextProvider.getText(AppTextKey.CANCEL_SELECTION, lang) else AppTextProvider.getText(AppTextKey.SELECT_ITEMS_TO_REMOVE, lang),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::setSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text(AppTextProvider.getText(AppTextKey.SEARCH, lang)) }
            )

            if (selectionMode) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = AppTextProvider.getText(AppTextKey.SELECTED_COUNT, lang).replace("%1\$d", selectedIds.size.toString()),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TextButton(onClick = { viewModel.selectAllDisplayedItemIds(items.map { it.id }) }) {
                                Text(AppTextProvider.getText(AppTextKey.ALL, lang))
                            }
                            TextButton(onClick = { viewModel.clearRowSelection() }) {
                                Text(AppTextProvider.getText(AppTextKey.NONE, lang))
                            }
                            TextButton(
                                onClick = { viewModel.requestBulkRemoveConfirm() },
                                enabled = selectedIds.isNotEmpty()
                            ) {
                                Text(AppTextProvider.getText(AppTextKey.REMOVE, lang))
                            }
                            TextButton(onClick = { viewModel.setSelectionMode(false) }) {
                                Text(AppTextProvider.getText(AppTextKey.DONE, lang))
                            }
                        }
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items, key = { it.id }) { row ->
                    ManageItemRowCard(
                        row = row,
                        selectionMode = selectionMode,
                        selected = selectedIds.contains(row.id),
                        onToggleSelected = { viewModel.toggleRowSelected(row.id) },
                        onEditClick = { openEdit(row) },
                        onToggleFavorite = { viewModel.toggleFavorite(row.id) },
                        onRemoveClick = { viewModel.requestRemove(row) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ManageItemRowCard(
    row: ManageItemRow,
    selectionMode: Boolean,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    onEditClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val lang = AppTextProvider.LocalAppLanguage.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectionMode) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = { onToggleSelected() }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = row.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    text = row.categoryName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (!selectionMode) {
                Text(
                    text = if (row.isFavorite) "★" else "☆",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (row.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable(onClick = onToggleFavorite)
                        .padding(4.dp)
                )
                TextButton(onClick = onEditClick) {
                    Text(AppTextProvider.getText(AppTextKey.EDIT, lang))
                }
                TextButton(
                    onClick = onRemoveClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(AppTextProvider.getText(AppTextKey.REMOVE, lang))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BulkAddItemsDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onAdd: (categoryId: Long, raw: String) -> Unit
) {
    val lang = AppTextProvider.LocalAppLanguage.current
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableLongStateOf(categories.first().id) }
    var bulkText by remember { mutableStateOf("") }

    val categoryLabel = categories.find { it.id == selectedCategoryId }?.name.orEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppTextProvider.getText(AppTextKey.BULK_ADD_TITLE, lang)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = AppTextProvider.getText(AppTextKey.BULK_ADD_HELP_TEXT_DETAILED, lang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ExposedDropdownMenuBox(expanded = categoryMenuExpanded, onExpandedChange = { categoryMenuExpanded = it }) {
                    OutlinedTextField(
                        value = categoryLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppTextProvider.getText(AppTextKey.CATEGORY, lang)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = categoryMenuExpanded, onDismissRequest = { categoryMenuExpanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    selectedCategoryId = cat.id
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = bulkText,
                    onValueChange = { bulkText = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    label = { Text(AppTextProvider.getText(AppTextKey.ITEM_NAMES_LABEL, lang)) },
                    supportingText = { Text(AppTextProvider.getText(AppTextKey.BULK_ADD_HELP_TEXT, lang)) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedCategoryId != 0L) {
                        onAdd(selectedCategoryId, bulkText)
                    }
                }
            ) {
                Text(AppTextProvider.getText(AppTextKey.ADD, lang))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(AppTextProvider.getText(AppTextKey.CANCEL, lang)) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemEditorDialog(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    categories: List<CategoryEntity>,
    selectedCategoryId: Long,
    onCategorySelected: (Long) -> Unit,
    favorite: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    isEditMode: Boolean,
    onRemove: (() -> Unit)?,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val lang = AppTextProvider.LocalAppLanguage.current
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val categoryLabel = categories.find { it.id == selectedCategoryId }?.name.orEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    label = { Text(AppTextProvider.getText(AppTextKey.ITEM_NAME, lang)) }
                )

                ExposedDropdownMenuBox(expanded = categoryMenuExpanded, onExpandedChange = { categoryMenuExpanded = it }) {
                    OutlinedTextField(
                        value = categoryLabel,
                        onValueChange = {},
                        readOnly = true,
                            label = { Text(AppTextProvider.getText(AppTextKey.CATEGORY, lang)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = categoryMenuExpanded, onDismissRequest = { categoryMenuExpanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    onCategorySelected(cat.id)
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(AppTextProvider.getText(AppTextKey.FAVORITE, lang))
                    Switch(checked = favorite, onCheckedChange = onFavoriteChange)
                }

                if (isEditMode && onRemove != null) {
                    TextButton(
                        onClick = onRemove,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(AppTextProvider.getText(AppTextKey.REMOVE_ITEM, lang))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onSave) { Text(AppTextProvider.getText(AppTextKey.SAVE, lang)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(AppTextProvider.getText(AppTextKey.CANCEL, lang)) } }
    )
}
