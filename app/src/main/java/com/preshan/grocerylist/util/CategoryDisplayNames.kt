package com.preshan.grocerylist.util

/**
 * Maps built-in seed category names (stored in the DB) to localized UI labels.
 * User-created category names are returned unchanged.
 */
object CategoryDisplayNames {
    fun localizedName(storedName: String, language: AppLanguage): String {
        val key = builtInKey(storedName) ?: return storedName
        return AppTextProvider.getText(key, language)
    }

    private fun builtInKey(name: String): AppTextKey? = when (name) {
        "Food & Grocery" -> AppTextKey.FOOD_GROCERY
        "Vegetables" -> AppTextKey.VEGETABLES
        "Fruits" -> AppTextKey.FRUITS
        "Meat Shop" -> AppTextKey.MEAT_SHOP
        "Health & Pharmacy" -> AppTextKey.HEALTH_PHARMACY
        "Household & Personal Care" -> AppTextKey.HOUSEHOLD_PERSONAL_CARE
        else -> null
    }
}
