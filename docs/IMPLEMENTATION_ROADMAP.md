# IMPLEMENTATION_ROADMAP

## Objective
- Deliver an offline-first Android grocery/shopping list app for Sri Lankan users.
- Keep implementation aligned with `docs/SRS.md` and `.cursor/rules/*.mdc`.
- Build user value first, then monetization and release readiness.

## Architecture Baseline
- Kotlin + Jetpack Compose + Navigation Compose
- MVVM + repository pattern
- Room/SQLite for local storage
- Coroutines/Flow where useful
- No backend, no login, no developer-owned analytics

## MVP Scope
- Preloaded Sri Lankan grocery catalog
- Quick item selection by category
- Favorites and frequently purchased shortcuts
- Shopping mode with purchased tracking
- Sharing through Android share sheet (including WhatsApp)
- Settings and privacy access
- Non-intrusive AdMob banner ads (after core flow)

## Non-goals (MVP)
- Cloud sync
- User accounts
- Firebase Analytics
- Location tracking
- Contacts/SMS access
- Camera/barcode scanning
- Budgeting or AI suggestions
- Full-screen/interstitial ads

## Main Screens
- Home
- Quick Item Selection
- Shopping Mode
- Manage Items
- Settings
- Privacy Policy
- About/Support

## Database Scope (MVP)
- `categories`
- `store_types`
- `items`
- `shopping_sessions`
- `shopping_session_items`
- `app_settings`

## Development Order (Agreed)
1. Hardcoded UI flow
2. Room database and seed data
3. Item management, favorites, frequent logic
4. Sharing via Android share sheet
5. Settings/privacy screens
6. AdMob banner ads
7. Play Store preparation

## Phase-by-Phase Deliverables

### 1) Hardcoded UI flow
- Build `Home -> Quick Item Selection -> Confirm List -> Shopping Mode`.
- Keep selected count visible and quick actions discoverable.
- Ensure tappable full-row selection and category switching.

### 2) Room database and seed data
- Add Room entities/DAOs/database/repositories.
- Seed default Sri Lankan categories/store types/items on first launch.
- Add indexes and migration baseline.

### 3) Item management, favorites, frequent logic
- Implement catalog management (add/edit/soft-delete/search/filter).
- Add favorites toggle and quick-select behavior.
- Update selection/purchase counters locally and deterministically.

### 4) Sharing via Android share sheet
- Add share formatter for all/pending/purchased views.
- Integrate `ACTION_SEND` (`text/plain`) and validate output readability.

### 5) Settings/privacy screens
- Add grouping preference and data-control actions.
- Add in-app privacy policy access and about/support screen.

### 6) AdMob banner ads
- Integrate test ads first; add release units later.
- Keep ads out of active shopping mode and critical-action areas.
- Ensure ad failures do not block app behavior.

### 7) Play Store preparation
- Complete Data Safety + contains-ads declaration.
- Validate privacy wording consistency with actual app behavior.
- Prepare release AAB, screenshots, listing copy, and testing requirements.

## Quality Gates
- Offline core flow works end-to-end.
- Active shopping session recovers after app restart.
- Performance remains acceptable on older devices.
- No unnecessary permissions.
- Migrations are explicit and testable before release.
