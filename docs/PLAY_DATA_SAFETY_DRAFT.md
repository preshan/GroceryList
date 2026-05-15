# Google Play Data Safety — Draft Answers

**App:** Shopping List - Simple Offline List  
**Package:** `com.preshan.grocerylist`  
**Based on codebase review:** [date of review: verify before submit]  
**Privacy policy URL:** https://github.com/preshan/GroceryList/blob/main/docs/PRIVACY_POLICY.md  
**Terms of use URL:** https://github.com/preshan/GroceryList/blob/main/docs/TERMS_OF_USE.md  
**Delete data URL:** https://github.com/preshan/GroceryList/blob/main/docs/DATA_DELETION.md

Use this document as a **worksheet** when completing [Play Console → App content → Data safety](https://support.google.com/googleplay/android-developer/answer/10787469). Wording in the console may differ slightly by year—**verify each answer in Play Console** before publishing.

---

## Quick reference — recommended position (current release)

| Question area | Draft answer |
|---------------|--------------|
| Does your app collect or share **required** user data types? | **No** — for data collected or shared **by you (the developer)** to your servers or for developer analytics |
| Account / login | **No** |
| Developer backend for list data | **No** |
| Developer-owned analytics | **No** |
| Ads SDK active | **Yes** — Google Mobile Ads SDK (`play-services-ads`) |
| Contains ads (Store listing) | **Yes** — banner on home screen only |
| Data encrypted in transit (developer) | **N/A** — no developer transmission of list data |
| User can request data deletion | **Yes** — via in-app **Clear All Local Data** and Android app storage settings |
| Prominent disclosure / consent for collection | **N/A** for developer collection (none) |

---

## 1. Data collected by the developer

### Position

**The developer does not collect** grocery list content or related catalogue/shopping data on developer-operated servers.

| Data category | Collected by developer? | Notes |
|---------------|-------------------------|--------|
| Name, email, user IDs (accounts) | **No** | No login or registration |
| Shopping list / item names / categories | **No** (off-device) | Stored **locally** in Room (`grocery_list.db`) |
| Favorites, purchase/frequent counters | **No** (off-device) | Local only |
| Shopping session history | **No** (off-device) | Local only |
| App settings (language, region) | **No** (off-device) | Local only |
| Location | **No** | Not used |
| Contacts, photos, SMS, etc. | **No** | Not used |
| Crash / diagnostics (developer SDK) | **No** | No Firebase Crashlytics / Analytics in `app/build.gradle.kts` |
| Advertising ID / ad data (developer) | **No** (developer does not collect list data) | **Google AdMob** may process ad-related data — declare per section 8 |

### Play Console guidance

- If asked **“Does your app collect any of the following user data?”** for types like *Personal info*, *Financial*, *Health*, etc.: select **none** that apply to **developer collection/transmission**, after reading each definition in the form.
- **Do not** select “App activity” or “Other” for shopping list content **unless** the form treats strictly on-device storage as collection—**verify in Play Console** (Google’s form evolution varies; many offline apps answer **no collection** when data never leaves the device to the developer).

### Wording for internal consistency

✅ *“This app does not collect or upload your grocery list data to developer servers.”*  
❌ *“This app collects no data”* — too broad once ads or OS-level processing exist.

---

## 2. Data shared by the developer

### Position

**The developer does not share** user list data with third parties (no server APIs, no analytics partners). **Google AdMob** is embedded for advertising; Google processes ad-related data under its policies — not developer sharing of grocery list content.

| Sharing type | Shared by developer? | Notes |
|--------------|----------------------|--------|
| Share with other companies / SDKs (developer-initiated) | **No** | |
| Sale of data | **No** | |
| Sharing for legal / fraud (developer) | **No** | |

### User-initiated share (see section 4)

When the user taps **Share**, Android passes text to **another app they choose** (e.g. WhatsApp). That is **not** the developer receiving or sharing data. **Verify in Play Console** whether the form asks separately about “data transferred to third parties when the user uses Share”—many developers report **no developer sharing** if they never send data to their own backend or embedded SDKs.

---

## 3. Local-only data

### What stays on device

Stored in **Room / SQLite** on the device, including:

- Categories, items, store types (catalogue)
- Favorites, frequent-item statistics (`purchase_count`, `selected_count`, timestamps)
- Active/completed shopping sessions and **session snapshots** (item/category names frozen for a trip)
- App settings (e.g. country/region, language, first-launch flags)

### Developer access

**None** — no cloud sync, no account, no developer dashboard.

### Ephemeral UI state

In-memory UI state (selected items during Quick Select) is not uploaded; session rows are persisted locally when the user confirms a list.

---

## 4. User-initiated sharing (Android share sheet)

### Behavior

- User opens **Share** in Shopping Mode.
- App builds **plain text** from the current **session snapshot** (not live catalogue names after session start).
- User picks target app via **Android share sheet** (`ACTION_SEND`).
- **Developer does not** receive a copy of the shared text.

### Data Safety implication

| Item | Draft |
|------|--------|
| Developer collects shared content | **No** |
| Developer shares to third parties | **No** (user’s chosen app handles it) |
| Optional disclosure in app | In-app privacy bullets already describe manual share |

**Verify in Play Console** if a specific “Sharing” checkbox appears for user-directed exports.

---

## 5. CSV import / export

### Behavior

- **Export:** User triggers export in Settings → system file picker (`CreateDocument`) → CSV written to a location **they** choose.
- **Import:** User picks a CSV file → app parses locally and merges into Room.
- **No** automatic upload to developer servers.
- UTF-8 text; may contain Sinhala/Tamil/other characters user entered.

### Data Safety implication

| Item | Draft |
|------|--------|
| Developer collects CSV contents | **No** |
| Files on device/cloud | Under user control (Drive, email, etc.) |

Mention in **Data safety → optional details** or privacy policy only if the form asks about file access—SAF does not grant broad storage read without user picking a file.

---

## 6. Android backup behavior

### Current configuration (verify in release manifest merge)

| Setting | Value |
|---------|--------|
| `android:allowBackup` | `true` |
| Cloud backup / device transfer for `grocery_list.db` | **Excluded** (`backup_rules.xml`, `data_extraction_rules.xml`) |
| Database files excluded | `grocery_list.db`, `-shm`, `-wal` |

### Implication for Data Safety

- List database is **not** included in the app’s configured **cloud backup / device-transfer** rules for that DB file.
- Google’s **Android backup infrastructure** may still back up other app components—**verify in Play Console** whether any “backup” question applies; answer consistently with `docs/PRIVACY_POLICY.md`.
- User-controlled backup path: **CSV export**.

---

## 7. Third-party SDKs currently used

From `app/build.gradle.kts` and merged dependencies:

| SDK / library family | Purpose | Sends list data to developer? |
|----------------------|---------|-------------------------------|
| **AndroidX Jetpack** (Compose, Room, Navigation, Lifecycle, Activity, Core) | UI, local DB, navigation | **No** |
| **Google Mobile Ads (AdMob)** | Banner ads on home screen | **No** (list content); Google may process ad-related data |
| **Kotlin** standard library | Language runtime | **No** |
| **KSP / Room compiler** | Build-time code gen | **No** (not in APK networking) |

**Not present:**

- Firebase (Analytics, Crashlytics, etc.)  
- Social / attribution SDKs  

**Verify in Play Console → App content → SDK indexing** (if enabled) matches release AAB after build.

---

## 8. Google AdMob (active)

### Current release behaviour

- **SDK:** `com.google.android.gms:play-services-ads` (see `gradle/libs.versions.toml`)  
- **Placement:** Banner on **home screen** only; **no ads** in shopping mode  
- **List data:** Not sent to developer servers; not used as ad targeting input by developer  
- **Play listing:** **Contains ads = Yes**  
- **Privacy policy:** https://github.com/preshan/GroceryList/blob/main/docs/PRIVACY_POLICY.md  

### Play Console / Data safety actions

1. Complete **Data safety** using [AdMob Play data disclosure](https://developers.google.com/admob/android/privacy/play-data-disclosure).  
2. Declare third-party collection/processing by **Google** for advertising as required by the form (not developer collection of grocery lists).  
3. **UMP / consent** — integrated in app; ensure a **Privacy & messaging** message is published in AdMob for your app ID.  
4. **Release builds** use production AdMob IDs in `app/src/main/res/values/admob.xml`; **debug** builds use Google test IDs in `app/src/debug/res/values/admob.xml`.  
5. Do **not** claim “no data collected” globally while AdMob is active — declare **Google** ad-related processing in Data safety.

### Likely AdMob-related declaration categories (verify in Play form)

- Advertising ID  
- App interactions (ads)  
- Device or other IDs  
- Data processed by Google for ads  

---

## 9. Permissions list

### Declared in `AndroidManifest.xml`

| Permission | Declared? | Used for |
|------------|-----------|----------|
| `INTERNET` | **Yes** | AdMob ad requests |
| `ACCESS_NETWORK_STATE` | **Yes** | Connectivity checks (ads SDK) |
| Location, camera, contacts, SMS, storage (legacy broad), phone | **No** | — |

**Action before production:** Run **Build → Analyze APK / merged manifest** on release AAB and confirm final permission list.

### Sensitive permissions

**None** intended for core features.

---

## 10. Security practices (common Play questions)

| Practice | Draft answer | Notes |
|----------|--------------|--------|
| Data encrypted in transit (developer servers) | **N/A** or **No data transmitted** | No developer API |
| Data encrypted at rest | **Verify in Play Console** | Android encrypts device storage when lock screen secure; app does not add separate SQLCipher |
| Users can request deletion | **Yes** | Clear All Local Data + Android Settings → Clear storage |
| Committed to Play Families / designed for children | **No** (general audience) — **verify** content rating answers |

---

## 11. Store listing alignment

| Field | Current draft |
|-------|----------------|
| Privacy policy URL | https://github.com/preshan/GroceryList/blob/main/docs/PRIVACY_POLICY.md |
| Data safety | Complete per this draft (include AdMob) |
| Ads | **Yes** (banner, home only) |
| Target audience | Not child-directed — match questionnaire |
| Category | Shopping / Productivity — **verify** |

---

## 12. Pre-production checklist (revisit before submit)

- [ ] Privacy policy merged to `main` on GitHub (URL live)  
- [ ] Confirm merged **release manifest** permissions (section 9)  
- [ ] Run release AAB through Play **pre-launch report**  
- [ ] Complete **Data safety** in Play Console using this draft; resolve any “verify” items  
- [ ] Confirm **Contains ads** = **Yes**  
- [ ] Confirm **Clear All Local Data** behavior matches deletion answers  
- [ ] After any schema/backup change, re-read sections 3 and 6  
- [ ] AdMob **Privacy & messaging** message published in AdMob console (UMP)  
- [ ] **Contains ads** = **Yes**; Data safety includes Google Mobile Ads SDK  

---

## 13. Suggested short answers (copy-paste starting points)

**Privacy policy — data collection summary (if free-text field):**  
> Shopping list data is stored on your device. We do not upload your lists to our servers. We do not use developer-owned analytics. Sharing and CSV export/import are manual and user-controlled. Banner ads on the home screen are served by Google AdMob; Google may process advertising-related data under its policies.

**Deletion:**  
> You can delete all local app data using Settings → Clear All Local Data, or clear app storage in Android system settings.

**Security — transmission:**  
> The app does not transmit your grocery list content to developer-operated servers.

---

*This draft is not legal advice. Final answers must match the live app and the current Google Play Data safety questionnaire.*
