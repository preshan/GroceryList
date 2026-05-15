# UI_UX_PLAN

## Design Goals
- Minimize taps from open app to usable shopping list
- Keep interactions obvious and forgiving
- Ensure readability and accessibility on smaller/older devices

## Core Principles
- One-tap start from Home
- Full-row tap for item selection
- Search always available in selection contexts
- Grouping by category/store for cognitive ease
- Shopping mode stays distraction-free
- Ads never near critical controls

## Screen Map
1. Splash / First Launch
2. Home
3. Quick Item Selection
4. Add/Edit Item (Bottom Sheet)
5. Manage Items
6. Manage Categories
7. Manage Store Types
8. Shopping Mode
9. Favorites
10. Frequent Items
11. Templates (optional in MVP)
12. Share Bottom Sheet
13. Export / Import (optional in MVP)
14. Settings
15. Privacy Policy
16. About / Support

## Primary User Flow
`Home -> Prepare Shopping List -> Quick Select -> Confirm -> Shopping Mode -> Finish -> Home`

## MVP UX Success Criteria
- One-tap start from home.
- Swipe or tap between categories.
- Whole item row is tappable.
- Selected count stays visible during selection.
- Quick buttons are always available: Select All, Deselect All, Favorites, Frequent Items, Clear.
- Final list supports grouping by category or store type.
- Shopping mode remains distraction-free.
- No banner ads in active shopping mode for MVP.
- UI supports older users and small phones (readable text + large targets).

## Interaction Details

### Home
- Primary CTA: "Prepare Shopping List"
- Secondary CTA: "Continue Shopping" (if active session exists)
- Quick shortcuts: favorites, frequent, manage items, settings

### Quick Item Selection
- Top search bar with instant filter
- Category tabs or horizontal pager
- Item rows: checkbox + label + optional store badge + favorite toggle
- Quick actions:
  - Select all
  - Deselect all
  - Select favorites
  - Select frequent
  - Clear selected
- Sticky confirm button showing selected count

### Shopping Mode
- Group items by user preference (`category` or `store`)
- Filter chips: all / pending / purchased
- Purchased toggle on row tap
- Progress indicator (`purchased/total`)
- Actions: share, finish session

## Empty States
- Friendly copy for no items/no favorites/no frequent/no templates
- Include one clear action button in each empty state

## Accessibility Baseline
- Large touch targets (minimum 48dp)
- High contrast text/icons
- Clear text hierarchy and spacing
- Content readable at larger font scales

## Ad Placement Guidance
- Banner on Home and non-critical screens only
- No ad near confirm/finish/share critical actions
- App remains fully functional when ad fails to load

## UI Technical Notes
- Compose Navigation for screen flow
- Reusable design tokens (colors/spacing/typography)
- Shared list item components for consistency

## Development Order Alignment
1. Hardcoded UI flow
2. Room database and seed data
3. Item management, favorites, frequent logic
4. Sharing via Android share sheet
5. Settings/privacy screens
6. AdMob banner ads
7. Play Store preparation
