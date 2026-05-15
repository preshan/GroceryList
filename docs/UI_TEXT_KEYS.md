# UI text keys (English catalogue)

Central registry of **stable logical keys** (`AppTextKeys` in `app/src/main/java/com/preshan/grocerylist/util/AppTextKeys.kt`) and the **English copy currently shown** in the app (from `strings.xml` or inline in Kotlin). Full in-app localization is **not** enabled yet.

| Key | English text | Screen / area | Notes |
|-----|----------------|---------------|-------|
| `app_name` | Shopping List - Simple Offline List | App label | `strings.xml` `app_name` |
| `continue` | Continue | Locale setup (first launch) | `locale_continue` |
| `cancel` | Cancel | Multiple dialogs | Hardcoded |
| `save` | Save | Locale edit, item editor | `locale_save` / hardcoded |
| `delete` | Delete | Quick Select delete confirm | Hardcoded |
| `remove` | Remove | Manage Items, Quick Select sheet | Hardcoded |
| `edit` | Edit | Manage Items bar | Hardcoded |
| `done` | Done | Manage Items selection | Hardcoded |
| `close` | Close | — | Reserved |
| `yes` | Yes | — | Reserved |
| `no` | No | — | Reserved |
| `ok` | OK | Manage Items loading dialogs | Hardcoded |
| `search` | Search by name | Manage Items | Hardcoded label |
| `settings` | Settings | Home shortcut, Settings top bar | `shortcut_settings_title` / hardcoded |
| `privacy_policy` | Privacy policy & developer | Settings row | Hardcoded subtitle |
| `app_version` | App version | Privacy & about | Hardcoded label |
| `support_email` | (developer email) | Privacy & about | `DeveloperEmail` constant |
| `setup_title` | Set up your list | Locale setup | `locale_setup_title` |
| `setup_subtitle` | Choose your country or region and preferred language. | Locale setup | `locale_setup_subtitle` |
| `country_region` | Country / Region | Locale setup | `locale_label_country_region` |
| `language` | Language | Locale setup | `locale_label_language` |
| `setup_helper` | You can change this later in Settings. | Locale setup card | `locale_setup_helper` |
| `international` | International | Dropdown value | `CountryLanguageDefaults` |
| `english` | English | Dropdown value | Seed / defaults |
| `sinhala` | Sinhala | Dropdown value | Seed / defaults |
| `tamil` | Tamil | Dropdown value | Seed / defaults |
| `hindi` | Hindi | Dropdown value | Seed / defaults |
| `german` | German | Dropdown value | Seed / defaults |
| `french` | French | Dropdown value | Seed / defaults |
| `spanish` | Spanish | Dropdown value | Seed / defaults |
| `arabic` | Arabic | Dropdown value | Seed / defaults |
| `portuguese` | Portuguese | Dropdown value | Seed / defaults |
| `indonesian` | Indonesian | Dropdown value | Seed / defaults |
| `locale_illustration_line1` | Shopping list | Setup illustration | `locale_illustration_line1` |
| `locale_illustration_line2` | Ready to go | Setup illustration | `locale_illustration_line2` |
| `prepare_shopping_list` | Prepare Shopping List | Home CTA | `home_prepare_list` |
| `favorites` | Favorites | Home shortcut | `shortcut_favorites_title` |
| `frequent_items` | Frequent | Home shortcut title | `shortcut_frequent_title` (UI says “Frequent”, not “Frequent Items”) |
| `manage_items` | Manage | Home shortcut title | `shortcut_manage_title` |
| `home_ad_placeholder` | Banner advertisement (legacy placeholder card) | Unused if live AdMob banner | `home_ad_placeholder` |
| `continue_shopping` | Continue Shopping | Home | Planned (see UI/UX plan); not shown in UI yet |
| `quick_select` | Quick Select | — | No screen title; uses search + sections |
| `search_items` | Search items | Quick Select | Hardcoded |
| `categories` | Categories | Quick Select | Hardcoded |
| `all_categories` | All Categories | Quick Select chip | Hardcoded constant |
| `filter_by` | Filter by | Quick Select | Hardcoded |
| `all` | All | Quick Select / Shopping filters | Hardcoded |
| `clear` | Clear | Quick Select filter chips | Hardcoded (`Clear`, not “Deselect”) |
| `select_all` | — | — | Not used as label |
| `deselect` | — | — | Not used |
| `confirm_list` | Confirm / Confirm (N items) | Quick Select bottom bar | Hardcoded dynamic |
| `selected_items_count` | selected items | — | Phrase not literal; count embedded in Confirm |
| `no_items_found` | — | — | Empty search not separately messaged |
| `no_favorite_items` | No favorites yet. Tap the star… | Quick Select banner | `GroceryListViewModel` |
| `no_frequent_items` | No frequent items yet (purchase… | Quick Select banner | `GroceryListViewModel` |
| `shopping_list` | Shopping List | Share plain text helper | `ShoppingListShareText.kt` header |
| `share` | Share | Shopping Mode | Hardcoded |
| `share_as` | Share as | Shopping share sheet | Hardcoded |
| `all_items` | All items | Share sheet row | Hardcoded |
| `pending_only` | Pending only | Share sheet row | Hardcoded |
| `purchased_only` | Purchased only | Share sheet row | Hardcoded |
| `pending` | Pending | Shopping filter chip | Hardcoded |
| `purchased` | Purchased | Shopping filter chip | Hardcoded |
| `finish_shopping` | Finish Shopping | Shopping Mode | Hardcoded |
| `completed_count` | N of M completed | Shopping Mode progress | Hardcoded pattern |
| `nothing_to_share` | Nothing to share for this option. | Shopping share | Toast hardcoded |
| `finish_shopping_confirm_title` | Finish shopping? | — | Confirm dialog not implemented |
| `finish_shopping_confirm_message` | Purchased items will be saved… | — | Confirm dialog not implemented |
| `add_item` | Add | Bulk add dialog | Hardcoded (`Add`) |
| `edit_item` | Edit item | Quick Select sheet | Hardcoded |
| `item_name` | Item name | Manage Items editor | Hardcoded |
| `category` | Category | Manage / Quick Select | Hardcoded |
| `favorite` | Favorite | Editors | Hardcoded (`Favorite`) |
| `remove_item` | Remove item | Quick Select / Manage | Hardcoded |
| `remove_item_confirm_title` | Delete item? / Remove item? | Quick Select vs Manage | Wording differs by screen |
| `remove_item_confirm_message` | (contextual) | Dialogs | Hardcoded |
| `duplicate_item_message` | An item with this name already exists… | Manage / rename | `ManageItemsViewModel` / `GroceryListViewModel` |
| `item_name_required` | Name is required. | Rename pipeline | `GroceryListViewModel` |
| `category_required` | Please select a category. | — | Toast uses “Choose a category.” |
| `catalogue` | Catalogue | Settings | Hardcoded |
| `shopping` | Shopping | Settings | Hardcoded |
| `data` | Data | Settings | Hardcoded |
| `privacy` | Privacy | Settings | Hardcoded |
| `about` | About | — | Combined into “Privacy & about” screen |
| `country_region_language` | Country / Region & Language | Settings section | `settings_section_locale` / `locale_settings_title` |
| `manage_categories` | Manage Categories | Settings | Hardcoded |
| `restore_default_items` | Restore Default Items | Settings | Hardcoded |
| `reset_frequent_item_data` | Reset frequent item data | Settings | Hardcoded |
| `clear_current_shopping_list` | Clear current shopping list | Settings | Hardcoded |
| `clear_all_local_data` | Clear all local data | Settings | Hardcoded |
| `ad_privacy_options` | Ad Privacy Options | Settings | `AppTextProvider` |
| `ad_privacy_options_subtitle` | Manage ad consent and personalization | Settings | `AppTextProvider` |
| `default_grouping` | Default grouping | — | Not in UI |
| `restore_default_items_confirm_title` | Restore default items? | Settings dialog | Hardcoded |
| `restore_default_items_confirm_message` | This restores missing default… | Settings dialog | Hardcoded (differs from SRS template) |
| `reset_frequent_confirm_title` | — | — | No dialog; action immediate |
| `reset_frequent_confirm_message` | — | — | — |
| `clear_all_data_confirm_title` | Clear all local data? | Settings dialog | Hardcoded |
| `clear_all_data_confirm_message` | This action is not implemented yet… | Settings dialog | Hardcoded |
| `privacy_title` | Privacy Policy | — | Screen title is “Privacy & about” |
| `privacy_intro` | This app keeps your shopping lists… | Privacy screen | Opening paragraph |
| `privacy_section_handled` | How data is handled | Privacy screen | Section title |
| `privacy_local_data` | Grocery list data is stored locally… | Privacy bullets | Approx. `privacy_title` SRS wording |
| `privacy_no_login` | The app does not require login. | Privacy bullets | |
| `privacy_no_upload` | The app does not upload grocery list… | Privacy bullets | |
| `privacy_no_analytics` | The app does not use developer-owned analytics. | Privacy bullets | |
| `privacy_ads_future` | The app shows banner ads through Google AdMob on the home screen (not in shopping mode). | Privacy bullets | Present-tense; key id kept for compatibility |
| `privacy_ads_data` | AdMob may process advertising-related data according to Google policies. | Privacy bullets | |
| `privacy_ads_consent` | Change ad consent in Settings → Ad Privacy Options (where available). | Privacy bullets | |
| `privacy_manual_share` | You may manually share lists… | Privacy bullets | |
| `privacy_no_auto_share` | The app does not automatically share shopping data. | Privacy bullets | |
| `food_grocery` | Food & Grocery | Default categories / seed | DB seed English |
| `vegetables` | Vegetables | Default categories | DB seed |
| `fruits` | Fruits | Default categories | DB seed |
| `meat_shop` | Meat Shop | Default categories | DB seed |
| `health_pharmacy` | Health & Pharmacy | Default categories | DB seed |
| `household_personal_care` | Household & Personal Care | Default categories | DB seed |

### Toasts & short messages

| Key | English text | Where |
|-----|----------------|-------|
| `toast_restore_defaults_summary` | Defaults restored: … | `GroceryListNavHost` |
| `toast_frequent_reset` | Frequent item data reset. | `GroceryListNavHost` |
| `toast_list_cleared` | Current shopping list cleared. | `GroceryListNavHost` |
| `toast_clear_all_placeholder` | Clear all local data is not implemented yet. | `GroceryListNavHost` |
| `toast_ad_privacy_not_available` | Ad privacy options are not available right now… | `GroceryListNavHost` |
| `toast_ad_privacy_error` | Could not open ad privacy options… | `GroceryListNavHost` |
| `toast_still_loading` | Still loading… | `ManageItemsScreen` |
| `toast_item_not_found` | Item not found. | `ManageItemsScreen` |
| `export_data` | Export Data | Settings → Data |
| `import_data` | Import Data | Settings → Data |
| `export_data_subtitle` | Save a CSV backup… | Settings row subtitle |
| `import_data_subtitle` | Restore from a CSV file… | Settings row subtitle |
| `export_data_message` | Export your categories and items as a CSV file. | Export confirm dialog |
| `choose_import_options` | Choose what to import | Import options dialog |
| `import_favorites` / `import_frequent_data` / `import_removed_items` | Toggle labels | Import options dialog |
| `choose_csv_file` | Choose CSV file | Import dialog action |
| `save_csv_file` | Save CSV file | Export dialog action |
| `export_success` / `export_failed` | Toasts | After SAF save |
| `import_success` / `import_failed` | Toasts / errors | After import |
| `import_summary` | Import summary | Summary dialog title |
| `import_invalid_csv_header` / `import_empty_file` | Errors | Invalid CSV |
| `categories_added` … `rows_failed` | Summary lines | Post-import dialog |
| `privacy_manual_import_export` | Manual CSV export/import note | Privacy policy |

---

## Future localization plan

1. **String resources first**  
   Move remaining hardcoded `Text("…")` and `Toast` strings into `res/values/strings.xml` (or a small `AppStrings` provider), keyed consistently with **`AppTextKeys`** so each key has exactly one Android string resource name.

2. **Per-locale resource folders**  
   Add translations under `values-si` (Sinhala), `values-ta`, `values-hi`, `values-de`, `values-fr`, `values-es`, `values-ar`, `values-pt`, `values-in` (Indonesian), matching the languages offered in onboarding. Optionally use Android **pseudo-locales** for QA (`values-en-rXA`) before translated files exist.

3. **Runtime selection vs system locale**  
   Today `selected_language` in `app_settings` only drives **seed pack** rules (with country). Later, wire `selected_language` to `AppCompatDelegate`/Compose `Localization` or a custom `CompositionLocalProvider` so UI language follows the user’s in-app choice instead of (or in addition to) system locale.

4. **Separate item packs from UI language**  
   Keep catalogue seed data (`DefaultSeedData`, `EnglishPlaceholderSeed`, future packs) independent of UI strings so users can run English UI with Sinhala item names, etc.

5. **Country / region**  
   Continue using `selected_country_region` for **default item pack** and regional defaults; avoid coupling it to UI language unless intentional.

---

## Related code

| Concern | Location |
|---------|-----------|
| First-launch detection | `GroceryListNavHost` reads `first_launch_completed` |
| Persisted locale | `LocaleSetupViewModel`, `AppSettingsRepository`, `AppSettingKeys` |
| Setup UI | `LocaleSetupScreen`, `LocaleSetupIllustration` |
| Stable key constants | `util/AppTextKeys.kt` |
