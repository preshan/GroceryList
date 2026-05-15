package com.preshan.grocerylist.util

import com.preshan.grocerylist.ui.navigation.GroceryItem
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider

enum class ShoppingShareFormat {
    ALL_ITEMS,
    PENDING_ONLY,
    PURCHASED_ONLY
}

/**
 * Builds plain-text shopping list grouped by category.
 * Returns null if there is nothing to share for the chosen [format].
 */
fun buildShoppingListShareText(
    itemsByCategory: Map<String, List<GroceryItem>>,
    purchasedItemIds: Set<Long>,
    format: ShoppingShareFormat,
    language: AppLanguage
): String? {
    val body = StringBuilder()
    body.appendLine(AppTextProvider.getText(AppTextKey.SHOPPING_LIST, language))
    body.appendLine()
    var wroteAny = false
    for (category in itemsByCategory.keys.sorted()) {
        val items = itemsByCategory[category].orEmpty()
        val filtered = when (format) {
            ShoppingShareFormat.ALL_ITEMS -> items
            ShoppingShareFormat.PENDING_ONLY -> items.filter { !purchasedItemIds.contains(it.id) }
            ShoppingShareFormat.PURCHASED_ONLY -> items.filter { purchasedItemIds.contains(it.id) }
        }
        if (filtered.isEmpty()) continue
        wroteAny = true
        body.appendLine(category)
        for (item in filtered.sortedBy { it.name.lowercase() }) {
            val prefix = when (format) {
                ShoppingShareFormat.PENDING_ONLY -> "☐ "
                ShoppingShareFormat.PURCHASED_ONLY -> "✓ "
                ShoppingShareFormat.ALL_ITEMS ->
                    if (purchasedItemIds.contains(item.id)) "✓ " else "☐ "
            }
            body.appendLine("$prefix${item.name}")
        }
        body.appendLine()
    }
    if (!wroteAny) return null
    return body.toString().trimEnd()
}
