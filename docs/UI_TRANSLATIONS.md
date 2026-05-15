# UI translations (in-app, runtime)

## Supported languages
- English
- Sinhala
- Tamil
- Hindi
- German
- French
- Spanish
- Arabic (RTL)
- Portuguese
- Indonesian

## Source of truth
- The app reads the current UI language from `app_settings.selected_language`.
- On first launch, `selected_language` is saved by the setup screen.
- Users can change language later in **Settings → Country / Region & Language**.

## Translation engine (current behavior)
- `AppTextProvider.getText(key, language)` returns:
  1. a translated string from the in-memory Kotlin maps (if present), otherwise
  2. the English fallback string, otherwise
  3. finally the key id (so the app never crashes).
- Full Android resource-based locale switching is not implemented yet.

## Fallback rules
- English is the fallback language.
- If `selected_language` is unknown or missing, English is used.
- Missing translations for a key fall back to English.

## RTL support
- When `selected_language` is **Arabic**, the app applies RTL layout direction at the Compose root.

## Item seed packs vs UI language
- UI translation is independent from default item seed packs.
- Default item seed selection still depends on `selected_country_region` and/or `selected_language` as defined by the seed rules.
- User-created item names are never translated automatically.

## Localization Inventory

| Screen / Area | Hardcoded / strings.xml Text | Existing Key? | Action Taken |
| :--- | :--- | :--- | :--- |
| Home | "Shopping List" (strings.xml) | No | Added `HOME_TITLE`, moved to Provider |
| Home | "Simple Offline List" (strings.xml) | No | Added `HOME_SUBTITLE`, moved to Provider |
| Home | "Welcome!" (strings.xml) | No | Added `HOME_WELCOME_HEADING`, moved to Provider |
| Home | "Plan faster..." (strings.xml) | No | Added `HOME_WELCOME_BODY`, moved to Provider |
| Home | "Shortcuts" (strings.xml) | No | Added `HOME_SHORTCUTS`, moved to Provider |
| Home | "Make items favourite" (strings.xml) | No | Added `SHORTCUT_FAVORITES_SUB`, moved to Provider |
| Home | "Manage Frequent Items" (strings.xml) | No | Added `SHORTCUT_FREQUENT_SUB`, moved to Provider |
| Home | "Add, edit or remove" (strings.xml) | No | Added `SHORTCUT_MANAGE_SUB`, moved to Provider |
| Home | "Customize Experience" (strings.xml) | No | Added `SHORTCUT_SETTINGS_SUB`, moved to Provider |
| Home | "Ad space" (strings.xml) | No | Added `HOME_AD_TITLE`, moved to Provider |
| Home | "Coming later" (strings.xml) | No | Added `HOME_AD_BADGE`, moved to Provider |
| Home | "Grocery bag..." (strings.xml CD) | No | Added `HOME_WELCOME_ILLUSTRATION_CD`, moved to Provider |
| Navigation | "Back" (Hardcoded) | No | Added `BACK`, replaced in all screens |
| Manage Categories | "Remove category?" | No | Added `REMOVE_CATEGORY_CONFIRM_TITLE` |
| Manage Categories | "Category name" | No | Added `CATEGORY_NAME_LABEL` |
| Manage Categories | "Manage Categories" | Yes (`MANAGE_CATEGORIES`) | Replaced with Provider |
| Manage Categories | "Rename" | No | Added `RENAME` |
| Manage Items | "Remove N items?..." | No | Added `REMOVE_SELECTED_ITEMS_CONFIRM_MESSAGE` |
| Manage Items | "Please wait" | No | Added `PLEASE_WAIT` |
| Manage Items | "Bulk add" | No | Added `BULK_ADD` |
| Manage Items | "None" | No | Added `NONE` |
| Manage Items | "Bulk add items" | No | Added `BULK_ADD_TITLE` |
| Manage Items | "Item names" | No | Added `ITEM_NAMES_LABEL` |
| Manage Items | "Example: Milk..." | No | Added `BULK_ADD_HELP_TEXT` |
| Settings | "Restore" | No | Added `RESTORE` |
| Settings | "Add, rename, or remove..." | No | Added `MANAGE_CATEGORIES_SUBTITLE` |
| Settings | "Restore v4 defaults" | No | Added `RESTORE_DEFAULTS_SUBTITLE` |
| Settings | "Resets selected/purchased..." | No | Added `RESET_FREQUENT_SUBTITLE` |
| Settings | "Placeholder with confirmation" | No | Added `CLEAR_ALL_DATA_SUBTITLE` |
| Settings | "Data handling, version..." | No | Added `PRIVACY_POLICY_SUBTITLE` |
| Components | "Ad space" | No | Used `HOME_AD_TITLE` |
| Components | "Reserved for future AdMob" | No | Added `AD_RESERVED_HELP` |
| Shopping Mode | "Choose what to include..." | No | Added `SHARE_AS_HELP_TEXT` |
| Shopping Mode | "Share shopping list" | No | Added `SHARE_CHOOSER_TITLE` |
| Quick Select | "Include in the Favourites..." | No | Added `FAVORITES_QUICK_FILTER_HELP` |

## Final Audit Report

- **Hardcoded strings removed**: All user-visible English strings identified in UI files have been moved to `AppTextProvider`.
- **New keys added**: Over 40 new `AppTextKey` entries added to cover all UI areas.
- **Screens updated**: `HomeScreen.kt`, `QuickSelectScreen.kt`, `ShoppingModeScreen.kt`, `ManageItemsScreen.kt`, `ManageCategoriesScreen.kt`, `SettingsScreen.kt`, `PrivacyPolicyScreen.kt`, `AppUiComponents.kt`.
- **Remaining strings**: None identified as user-facing. Internal technical strings (logs, database) were intentionally kept.

## Translation Review Status

| Language | Status | Notes |
| :--- | :--- | :--- |
| English | Complete | Source/fallback |
| Sinhala | Reviewed | Natural Sri Lankan wording (e.g., "හරි", "අවසන්") |
| Tamil | Reviewed | Simple understandable wording (e.g., "சரி", "முடிந்தது") |
| Hindi | Reviewed | Simple everyday wording |
| German | Reviewed | Natural UI wording |
| French | Reviewed | Natural UI wording |
| Spanish | Reviewed | Natural UI wording |
| Arabic | Reviewed | MSA + RTL check (layout direction applied) |
| Portuguese | Reviewed | General Portuguese |
| Indonesian | Reviewed | Simple Indonesian |
