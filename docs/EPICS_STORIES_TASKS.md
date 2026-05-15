# EPICS_STORIES_TASKS

## Epic 1: App Foundation (Offline-first)
### Stories
- As a user, I can open the app quickly and navigate core screens.
- As a developer, I can maintain a clear architecture baseline.
### Tasks
- Set up Compose + navigation + theme
- Define package structure
- Add Home, Quick Select, Shopping routes

## Epic 2: Local Data Layer
### Stories
- As a user, my lists persist offline.
- As a developer, I can evolve schema safely.
### Tasks
- Add Room entities/DAOs/repositories
- Add schema versioning and migrations
- Wire ViewModels to repository

## Epic 3: Seeded Sri Lankan Catalog
### Stories
- As a user, I can start without manual setup.
### Tasks
- Seed categories/store types/items on first launch
- Prevent duplicate seed inserts
- Allow restore defaults

## Epic 4: Quick Selection Experience
### Stories
- As a user, I can prepare a list faster than note-app checkboxes.
### Tasks
- Build search + category tabs + full-row selection
- Add quick actions (all/favorites/frequent/clear)
- Confirm selected list into session

## Epic 5: Shopping Mode
### Stories
- As a shopper, I can check off purchased items easily.
### Tasks
- Build grouped checklist view
- Add all/pending/purchased filters
- Persist state and recover active session
- Finish session workflow

## Epic 6: Favorites & Frequent
### Stories
- As a user, I can quickly reselect commonly used items.
### Tasks
- Favorite toggle and favorites view
- Frequency counters and frequent selector
- Reset frequent data option

## Epic 7: Item/Category/Store Management
### Stories
- As a user, I can maintain my catalog.
### Tasks
- CRUD for items/categories/store types
- Search/filter in manage screens
- Prevent duplicate active item names

## Epic 8: Share List
### Stories
- As a user, I can share my shopping list via WhatsApp/share sheet.
### Tasks
- Build text formatter
- Add share options for all/pending/purchased
- Integrate `ACTION_SEND` and test apps

## Epic 9: Settings & Data Control
### Stories
- As a user, I can control my local data and preferences.
### Tasks
- Settings screen with grouping preference
- Clear current list
- Clear all local data
- Restore defaults

## Epic 10: Privacy, Ads, and Play Store Readiness
### Stories
- As an app owner, I can publish compliantly.
### Tasks
- Add privacy policy link in-app
- Integrate AdMob banner test ads
- Complete Data Safety + ads declarations
- Prepare closed test + release checklist

## Suggested Execution Sequence
1. Epics 1-2 (foundation + data)
2. Epics 3-5 (core user value)
3. Epics 6-9 (speed, control, share)
4. Epic 10 (publish readiness)
