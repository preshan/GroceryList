# Project Brain - Shopping List - Simple Offline List

## App Purpose

A simple offline-first grocery/shopping list app for Sri Lankan users.

## Main Problem

Users currently use Samsung Notes checkboxes for shopping lists, but it is hard to reset, reselect common items, categorize items, and share lists.

## Main Solution

The app provides:
- Preloaded Sri Lankan shopping items
- Quick item selection
- Categories
- Store types
- Favorites
- Frequently purchased items
- Shopping mode
- WhatsApp/default sharing
- Local-only storage
- Small banner ads

## MVP Flow

1. User opens app.
2. User taps Prepare Shopping List.
3. User selects items from categories.
4. User can select favorites/frequent items quickly.
5. User confirms list.
6. App shows final shopping list grouped by category/store.
7. User marks items as purchased.
8. User finishes shopping.
9. App updates local purchase counts.

## Non-goals

Do not build:
- Budgeting
- User login
- Cloud sync
- Analytics
- Location tracking
- Barcode scanning
- AI suggestions
- Full-screen ads

## Architecture Direction
- Kotlin + Jetpack Compose + Navigation Compose
- MVVM + repository pattern
- Room/SQLite local database
- Coroutines/Flow where useful
- No backend/API for MVP

## Development Order (Agreed)
1. Hardcoded UI flow
2. Room database and seed data
3. Item management, favorites, frequent logic
4. Sharing via Android share sheet
5. Settings/privacy screens
6. AdMob banner ads
7. Play Store preparation
