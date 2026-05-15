# Project-specific ProGuard / R8 rules.
# Applied only when release minify is enabled (isMinifyEnabled = true).

# Preserve line numbers for crash reports.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room (schemas are exported under app/schemas/).
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ViewModels used with Compose / Navigation.
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Kotlin coroutines (common R8 false positives).
-dontwarn kotlinx.coroutines.**
