# Play Store Release Checklist

**App:** Shopping List - Simple Offline List  
**Package:** `com.preshan.grocerylist`  
**Use this list** before uploading to internal testing, closed testing, or production.

Related docs: [PRIVACY_POLICY.md](PRIVACY_POLICY.md) · [PLAY_DATA_SAFETY_DRAFT.md](PLAY_DATA_SAFETY_DRAFT.md) · [PLAYSTORE_COMPLIANCE.md](PLAYSTORE_COMPLIANCE.md)

---

## 1. Build

| Step | Command / action | Done |
|------|------------------|------|
| Gradle sync | Android Studio → **Sync Project with Gradle Files** | ☐ |
| Release lint | `./gradlew :app:lintRelease` (fix errors; review warnings) | ☐ |
| Unit tests | `./gradlew :app:testDebugUnitTest` | ☐ |
| Instrumented tests | `./gradlew :app:connectedDebugAndroidTest` (device/emulator) | ☐ |
| Release bundle | `./gradlew :app:bundleRelease` | ☐ |
| Signing | `keystore.properties` configured locally (not in git) | ☐ |
| Output | Confirm `app/build/outputs/bundle/release/app-release.aab` | ☐ |
| Merged manifest | Review release manifest (permissions, AdMob App ID) | ☐ |

---

## 2. App identity

| Item | Current / target | Done |
|------|----------------|------|
| **App name** | Shopping List - Simple Offline List | ☐ |
| **Package name** | `com.preshan.grocerylist` (do not change without migration plan) | ☐ |
| **App icon** | `@mipmap/ic_launcher` + round icon | ☐ |
| **Feature graphic** | 1024×500 Play asset | ☐ |
| **Screenshots** | Home, Quick Select, Shopping Mode, Settings, Manage Items (phone) | ☐ |
| **Short description** | ≤ 80 chars; offline Sri Lankan–friendly grocery list | ☐ |
| **Full description** | Offline, no account, categories, favorites, share, CSV | ☐ |

---

## 3. Privacy

| Item | Notes | Done |
|------|-------|------|
| **Hosted privacy policy URL** | https://github.com/preshan/GroceryList/blob/main/docs/PRIVACY_POLICY.md (merge to `main` before Play submit) | ☐ |
| **Terms of use URL** (optional) | https://github.com/preshan/GroceryList/blob/main/docs/TERMS_OF_USE.md | ☐ |
| **In-app privacy policy** | Settings → Privacy policy screen matches hosted policy | ☐ |
| **Data Safety form** | Complete using `docs/PLAY_DATA_SAFETY_DRAFT.md` | ☐ |
| **Backup behavior** | `grocery_list.db` excluded from cloud backup & device transfer | ☐ |
| **CSV import/export** | Manual, user-initiated; stated in policy & Data Safety | ☐ |
| **Deletion** | Clear All Local Data + Android Settings → Clear storage | ☐ |

---

## 4. Ads (Google AdMob)

| Item | Current app state | Done |
|------|-------------------|------|
| **Production AdMob IDs** | `app/src/main/res/values/admob.xml` (release builds) | ☐ |
| **Debug test ads** | `app/src/debug/res/values/admob.xml` overrides with Google sample IDs | ☐ |
| **SDK** | `play-services-ads` + UMP (`user-messaging-platform`); banner on **Home only** | ☐ |
| **Contains ads** (Play listing) | Set **Yes** — app contains ads | ☐ |
| **Data Safety** | Declare **Google Mobile Ads SDK** / AdMob per `PLAY_DATA_SAFETY_DRAFT.md` §8 | ☐ |
| **Privacy policy** | Hosted URL; states active banner ads + UMP | ☐ |
| **UMP / consent** | `AdConsentManager` in app; publish message in AdMob → Privacy & messaging | ☐ |
| **Release AAB check** | Install **release** build once; confirm real ads (not “Test Ad” label) | ☐ |
| **Permissions** | `INTERNET`, `ACCESS_NETWORK_STATE` in release manifest | ☐ |

---

## 5. Manual testing

### Core flows

| Test | Done |
|------|------|
| First launch → country/language setup | ☐ |
| Sinhala / Tamil / Hindi UI smoke check | ☐ |
| **Arabic RTL** layout (locale setup + home + settings) | ☐ |
| Prepare list → Quick Select → Confirm → Shopping Mode | ☐ |
| Purchased toggles persist; finish shopping updates stats | ☐ |
| Shopping list shows **session snapshots** after rename in catalogue | ☐ |
| Share sheet (WhatsApp or notes) readable text | ☐ |
| Add / edit / remove item (Manage Items) | ☐ |
| Restore default items (custom items kept) | ☐ |
| Reset frequent item data | ☐ |
| CSV export → re-import (UTF-8 Sinhala/Tamil spot check) | ☐ |
| Clear All Local Data → returns to setup | ☐ |

### Quality

| Test | Done |
|------|------|
| **Dark mode** (system theme) | ☐ |
| **Low-end device** or API 23–26 emulator (scroll, DB, ads load/fail gracefully) | ☐ |
| **Offline** core flow (airplane mode): list + shopping without network | ☐ |
| **Home banner ad** loads on Wi‑Fi; no crash if ad fails | ☐ |
| No banner on Shopping Mode | ☐ |
| Process death: reopen app (no active session restore expected) | ☐ |

---

## 6. Play Console

| Item | Done |
|------|------|
| **Content rating** | IARC questionnaire completed | ☐ |
| **Target audience** | Not child-directed (unless intentional) | ☐ |
| **App category** | Shopping or Productivity — **verify** best fit | ☐ |
| **News app** | No | ☐ |
| **COVID-19 / government** | No | ☐ |
| **Closed testing** | Create track; add testers; meet duration if required for production | ☐ |
| **Production access** | Complete testing requirements per [Play policy](https://support.google.com/googleplay/android-developer/answer/14151465) | ☐ |
| **Data safety** | Submitted and matches app | ☐ |
| **Ads declaration** | Matches build | ☐ |

---

## 7. Release metadata

| Field | Current (verify before upload) | Done |
|-------|-------------------------------|------|
| **versionCode** | `1` (increment every upload) | ☐ |
| **versionName** | `1.0.0` | ☐ |
| **Release notes** | First release: offline lists, categories, shopping mode, CSV, Sinhala defaults | ☐ |
| **Play App Signing** | Enroll; use upload key from `keystore.properties` | ☐ |
| **Internal testing** | Upload AAB → internal track → install via link | ☐ |
| **Closed testing** | Promote after internal smoke pass | ☐ |
| **Production** | After closed testing + policy checks | ☐ |

---

## 8. Pre-upload gate (sign-off)

All must be true:

- [ ] Release AAB builds and installs
- [ ] Privacy policy URL live and linked in Play Console
- [ ] Data Safety matches behavior (developer list data local; **Google AdMob** declared for ads)
- [ ] **Contains ads** = **Yes** in Play Console
- [ ] Release AAB uses production IDs in `main/admob.xml` (debug builds may show test ads)
- [ ] No PII in logs; no secrets in git
- [ ] `versionCode` bumped if re-uploading

---

## 9. After upload

- [ ] Install from Play internal/closed link on physical device
- [ ] Monitor pre-launch report (if available)
- [ ] Fix crashes before promoting track
- [ ] Keep release branch / tag in git

---

*Last checklist revision: align with repo before each release.*
