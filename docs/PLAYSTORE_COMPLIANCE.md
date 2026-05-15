# PLAYSTORE_COMPLIANCE

## Goal
Ship the app to Google Play with policy-safe metadata, privacy disclosure, and stable release quality.

## Compliance Checklist

### Privacy & Data Safety
- **Privacy policy URL (hosted on GitHub):** https://github.com/preshan/GroceryList/blob/main/docs/PRIVACY_POLICY.md
- **Terms of use (optional, GitHub):** https://github.com/preshan/GroceryList/blob/main/docs/TERMS_OF_USE.md
- In-app privacy policy access (Settings → Privacy policy; links open hosted docs)
- Complete Data Safety form accurately
- Reflect AdMob SDK behavior correctly
- Do not claim "no data collected" for the app overall — grocery list data stays local; **Google AdMob** may process ad-related data per Google policies

### Ads Declaration
- Declare **"Contains ads" = Yes** in Play Console (AdMob banner on home screen)
- Keep ad behavior aligned with app implementation (banner only; no ads in shopping mode)
- **Production AdMob IDs** in `app/src/main/res/values/admob.xml`; **debug** builds use Google test IDs in `app/src/debug/res/values/admob.xml`
- Complete AdMob / Data safety disclosures per [AdMob Play data disclosure](https://developers.google.com/admob/android/privacy/play-data-disclosure)
- **UMP / consent** integrated in app (`AdConsentManager`); configure messages in AdMob → Privacy & messaging

### Permissions
- Keep permissions minimal
- For ads, likely only:
  - `INTERNET`
  - `ACCESS_NETWORK_STATE`
- Avoid unrelated sensitive permissions

### Targeting & Content
- Set correct target audience
- Complete content rating questionnaire accurately
- Ensure app is not child-directed unless intentionally designed so

### Technical Release
- Build signed release AAB
- Target current Play-required API level
- Verify min SDK support decision (23/24)
- Run Proguard/R8 compatibility checks if enabled

### Testing Track Readiness
- Create closed testing track if required
- Recruit enough opted-in testers
- Gather feedback and stability evidence
- Complete required testing duration before production request (if applicable)

## In-App Requirements to Match Declarations
- Grocery list data remains local to device
- Sharing only happens on explicit user action
- Ads can fail gracefully without blocking app actions
- Do not claim "This app collects no data" when AdMob is present

## Pre-Submission QA Gate
- Offline core flow works end-to-end
- Session recovery after app restart works
- Share output readable in WhatsApp/SMS/email
- Ads do not cover key actions
- Settings data-control actions work safely

## Development Order Alignment
Play Store and final compliance work belongs to phase 7 after core product stability:
1. Hardcoded UI flow
2. Room database and seed data
3. Item management, favorites, frequent logic
4. Sharing via Android share sheet
5. Settings/privacy screens
6. AdMob banner ads
7. Play Store preparation

## Release Artifacts
- App icon + feature graphic
- Screenshots for primary flows
- Short description + full description
- Privacy policy URL
- Support/contact details

## Useful References
- [Google Play Data Safety](https://support.google.com/googleplay/android-developer/answer/10787469)
- [Google Play User Data Policy](https://support.google.com/googleplay/android-developer/answer/10144311)
- [Closed Testing / Production Access](https://support.google.com/googleplay/android-developer/answer/14151465)
- [AdMob Play Data Disclosure](https://developers.google.com/admob/android/privacy/play-data-disclosure)
- [Target SDK Requirements](https://developer.android.com/google/play/requirements/target-sdk)
