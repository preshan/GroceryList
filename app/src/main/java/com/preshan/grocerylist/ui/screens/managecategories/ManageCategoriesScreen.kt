package com.preshan.grocerylist.ui.screens.managecategories

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.preshan.grocerylist.data.local.entity.CategoryEntity
import com.preshan.grocerylist.ui.components.AppPrimaryButton
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider
import com.preshan.grocerylist.util.CategoryDisplayNames
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(
    viewModel: ManageCategoriesViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState()
    val pendingRemove by viewModel.pendingRemove.collectAsState()
    val scope = rememberCoroutineScope()

    var editorOpen by remember { mutableStateOf(false) }
    var editingCategoryId by remember { mutableStateOf<Long?>(null) }
    var editorName by remember { mutableStateOf("") }

    fun openAdd() {
        editingCategoryId = null
        editorName = ""
        editorOpen = true
    }

    fun openEdit(category: CategoryEntity) {
        editingCategoryId = category.id
        editorName = category.name
        editorOpen = true
    }

    val lang = AppTextProvider.LocalAppLanguage.current

    pendingRemove?.let { cat ->
        AlertDialog(
            onDismissRequest = viewModel::dismissRemoveConfirm,
            title = { Text(AppTextProvider.getText(AppTextKey.REMOVE_CATEGORY_CONFIRM_TITLE, lang)) },
            text = {
                Text(
                    AppTextProvider.getText(AppTextKey.REMOVE_CATEGORY_CONFIRM_MESSAGE, lang)
                        .replace("%1\$s", cat.name)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val err = viewModel.confirmRemovePending()
                            if (err != null) {
                                Toast.makeText(
                                    context,
                                    AppTextProvider.getText(err, lang),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                ) {
                    Text(AppTextProvider.getText(AppTextKey.REMOVE, lang))
                }
            },
            dismissButton = { TextButton(onClick = viewModel::dismissRemoveConfirm) { Text(AppTextProvider.getText(AppTextKey.CANCEL, lang)) } }
        )
    }

    if (editorOpen) {
        AlertDialog(
            onDismissRequest = { editorOpen = false },
            title = {
                Text(
                    if (editingCategoryId == null) AppTextProvider.getText(AppTextKey.ADD_CATEGORY, lang)
                    else AppTextProvider.getText(AppTextKey.RENAME_CATEGORY, lang)
                )
            },
            text = {
                OutlinedTextField(
                    value = editorName,
                    onValueChange = { editorName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(AppTextProvider.getText(AppTextKey.CATEGORY_NAME_LABEL, lang)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val err = if (editingCategoryId == null) {
                                viewModel.addCategory(editorName)
                            } else {
                                viewModel.updateCategory(editingCategoryId!!, editorName)
                            }
                            if (err != null) {
                                Toast.makeText(
                                    context,
                                    AppTextProvider.getText(err, lang),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                editorOpen = false
                            }
                        }
                    }
                ) {
                    Text(AppTextProvider.getText(AppTextKey.SAVE, lang))
                }
            },
            dismissButton = { TextButton(onClick = { editorOpen = false }) { Text(AppTextProvider.getText(AppTextKey.CANCEL, lang)) } }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(AppTextProvider.getText(AppTextKey.MANAGE_CATEGORIES, lang)) },
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
            Text(
                text = AppTextProvider.getText(AppTextKey.MANAGE_CATEGORIES_HELP_TEXT, lang),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AppPrimaryButton(
                text = AppTextProvider.getText(AppTextKey.ADD_CATEGORY, lang),
                onClick = { openAdd() },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryRowCard(
                        category = category,
                        lang = lang,
                        onEditClick = { openEdit(category) },
                        onRemoveClick = { viewModel.requestRemove(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRowCard(
    category: CategoryEntity,
    lang: com.preshan.grocerylist.util.AppLanguage,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = CategoryDisplayNames.localizedName(category.name, lang),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (category.isDefault) AppTextProvider.getText(AppTextKey.BUILT_IN, lang) 
                           else AppTextProvider.getText(AppTextKey.YOUR_CATEGORY, lang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onEditClick) {
                Text(AppTextProvider.getText(AppTextKey.RENAME, lang))
            }
            if (!category.isDefault) {
                TextButton(onClick = onRemoveClick) {
                    Text(AppTextProvider.getText(AppTextKey.REMOVE, lang))
                }
            }
        }
    }
}
