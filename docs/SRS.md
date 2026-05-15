# Shopping List - Simple Offline List - Software Requirements Specification (Markdown)

## Document Metadata
- **Project**: Offline Sri Lankan Grocery / Shopping List Android App
- **Version**: 1.0
- **Date**: 2026-04-25
- **Status**: Planning / Pre-development

## 1) Introduction
This project defines an offline-first Android grocery/shopping list app targeted mainly at Sri Lankan household users. The app focuses on speed, simplicity, and local-first usage, while remaining Play Store compliant and easy to maintain.

## 2) Product Scope
### MVP In Scope
- Offline local database
- Default Sri Lankan item catalog
- Categories and store types
- Quick item selection
- Shopping mode checklist
- Favorites and frequent items
- List sharing (Android share sheet / WhatsApp)
- Settings and privacy access
- Non-intrusive banner ads

### Optional (v1.0/v1.1)
- Reusable templates
- CSV/text export/import
- Advanced backup/restore
- Theme settings

### Out of Scope (MVP)
- Login/accounts
- Backend/cloud sync
- Location tracking
- Contacts access
- Developer-owned analytics
- Barcode scanning
- Budgeting and AI suggestions
- Full-screen ads

## 3) System Overview
- **Platform**: Native Android
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Storage**: Room + SQLite
- **Architecture**: MVVM + repository
- **Sharing**: `ACTION_SEND` with `text/plain`
- **Ads**: AdMob banner only
- **Backend**: None for MVP

## 4) Core User Flow
`Home -> Quick Select -> Confirm List -> Shopping Mode -> Mark Purchased -> Finish Shopping -> Home`

## 5) Functional Requirements (Condensed)
- Offline core features
- Local Room storage
- Seed default Sri Lankan data on first launch
- Item/category/store management
- Search/filter/quick actions
- Shopping session lifecycle + purchased state
- Favorites + frequent items (local calculation)
- Share list text via Android share sheet
- Settings, privacy policy, local data control
- Non-intrusive banner ads

## 6) Non-Functional Requirements
- Fast launch and list rendering on older devices
- Session recovery after restart
- Local privacy: no developer server upload for grocery data
- Minimal permissions
- Good tap targets and readability
- Maintainable Room migrations and repositories

## 7) Compliance and Publishing Highlights
- Privacy policy URL in Play Console and in-app
- Accurate Data Safety declaration (including AdMob behavior)
- Declare app contains ads
- Minimal required permissions (likely internet/network state for ads)
- Prepare for closed testing requirement before production

## 8) Suggested Development Order
1. Build UI flow with hardcoded data
2. Add Room schema + seed data
3. Implement item management + favorites + frequent logic
4. Add sharing
5. Add settings + privacy page
6. Add AdMob banners using test ads
7. Prepare Play listing + testing + release

## 9) Related Files
- Source document: `docs/Grocery_List_App_SRS.docx`
- Brain doc: `docs/PROJECT_BRAIN.md`
- Database doc: `docs/DB_STRUCTURE.md`
- Plan doc: `docs/DEVELOPMENT_PLAN.md`
