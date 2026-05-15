# DB_STRUCTURE

## Database Choice
- **Engine**: SQLite (via Room)
- **Reason**: Offline-first reliability, local performance, simple migration path

## Core Tables (MVP)
1. `categories`
2. `store_types`
3. `items`
4. `shopping_sessions`
5. `shopping_session_items`
6. `app_settings`

## Optional Tables (Post-MVP / v1.x)
- `list_templates`
- `list_template_items`
- `privacy_consents`
- `import_logs`
- `item_usage_events` (if later needed)

## Local Data Policy
- Grocery list data remains on device.
- Do not upload grocery data to any developer-owned server.
- CSV/text is for export/import/share only, not primary storage.

## Table Overview

### `categories`
- Purpose: Item grouping such as vegetables, dairy, pantry
- Key fields: `id`, `name`, `sort_order`, `is_default`, `is_active`, timestamps

### `store_types`
- Purpose: Where items are usually purchased
- Key fields: `id`, `name`, `sort_order`, `is_default`, `is_active`, timestamps

### `items`
- Purpose: Master catalog of groceries
- Key fields:
  - Identity: `id`, `name`, `normalized_name`
  - Relations: `category_id`, `store_type_id`
  - UX flags: `is_favorite`, `excluded_from_frequent`, `is_active`, `is_default`
  - Stats: `selected_count`, `purchase_count`, `last_selected_at`, `last_purchased_at`
  - timestamps

### `shopping_sessions`
- Purpose: One shopping run lifecycle
- Key fields: `id`, `title`, `status`, `group_by`, `total_items`, `purchased_items`, timestamps
- Status values: `draft`, `active`, `completed`, `cancelled`

### `shopping_session_items`
- Purpose: Snapshot and state of items in a specific session
- Key fields:
  - Relations: `shopping_session_id`, `item_id`
  - Snapshot values: `item_name_snapshot`, `category_id_snapshot`, `store_type_id_snapshot`
  - Session state: `is_selected`, `is_purchased`, `purchased_at`, `sort_order`
  - timestamps

### `app_settings`
- Purpose: Key-value settings storage
- Key fields: `key`, `value`, `updated_at`

## Relationships
- `categories` 1 -> many `items`
- `store_types` 1 -> many `items`
- `shopping_sessions` 1 -> many `shopping_session_items`
- `items` 1 -> many `shopping_session_items`

## Index Recommendations
- `items(normalized_name)`
- `items(category_id, is_active)`
- `items(store_type_id, is_active)`
- `items(is_favorite, is_active)`
- `items(purchase_count)`
- `shopping_sessions(status)`
- `shopping_session_items(shopping_session_id)`
- `shopping_session_items(item_id)`

## Data Rules
- Avoid hard delete for catalog records; prefer `is_active=false`
- Update `selected_count` on selection confirmation
- Update `purchase_count` only when session is completed
- Use session snapshot fields to preserve historical list integrity

## Migration Strategy
- Start with Room schema version `1`
- Use explicit migration objects for each version bump
- Add migration tests before release upgrades
- Keep destructive migrations disabled for production builds

## Development Order Dependency
This database work starts in agreed phase 2:
1. Hardcoded UI flow
2. Room database and seed data
3. Item management, favorites, frequent logic
4. Sharing via Android share sheet
5. Settings/privacy screens
6. AdMob banner ads
7. Play Store preparation
