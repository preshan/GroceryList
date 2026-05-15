# Shopping List — Simple Offline List

Offline-first Android shopping list app. Data stays on the device. No account or backend.

**Author:** [Preshan Pradeepa Kariyawasam](mailto:preshanpradeepa@gmail.com)

## What it does

- Prepare a list from categories and items (quick select, favorites, frequent items)
- Shopping mode checklist (mark items purchased, share list via Android share sheet)
- Manage categories and items; restore default catalogue
- Country/region and language on first launch (10 UI languages)
- Export/import catalogue as CSV (Settings → Data)
- Local SQLite database (Room)

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Room, Navigation Compose, ViewModel, Coroutines/Flow
- Min SDK 23, target SDK 36

## Requirements

- [Android Studio](https://developer.android.com/studio) (recent stable; project uses AGP 9.x)
- Android SDK (installed via SDK Manager)
- JDK 11+ (Android Studio bundled JDK is fine)

## Build and run

1. Clone the repo.
2. Open the project folder in Android Studio.
3. Wait for Gradle sync. Android Studio creates `local.properties` with your SDK path automatically.
4. Run the `app` configuration on a device or emulator.

From the command line (with `JAVA_HOME` set):

```bash
./gradlew assembleDebug
```

Debug APK: `app/build/outputs/apk/debug/` (after a successful build).

## Project layout

```
app/src/main/java/com/preshan/grocerylist/
  data/          Room entities, DAOs, repositories, seed data
  domain/        Domain models (minimal today)
  ui/            Compose screens, navigation, theme
  util/          App text keys, CSV helpers, constants
docs/            SRS, UI notes, compliance notes
.cursor/rules/   Editor/agent guidelines for contributors (optional)
```

## Configuration

- **No API keys** are required for the current app build.
- **Do not commit** `local.properties`, keystores (`.jks`), or `google-services.json` with real keys.
- AdMob is not integrated yet; banner placeholder on home is UI-only.

## Privacy

Grocery list data is stored locally. The app does not upload your list to developer servers. See in-app **Settings → Privacy policy** and `docs/PLAYSTORE_COMPLIANCE.md` for Play Store notes.

## License

MIT — see [LICENSE](LICENSE).

## Docs

| File | Purpose |
|------|---------|
| `docs/SRS.md` | Product requirements |
| `docs/DB_STRUCTURE.md` | Database overview |
| `docs/UI_TEXT_KEYS.md` | UI string key catalogue |
| `docs/PLAYSTORE_COMPLIANCE.md` | Play Console checklist |
