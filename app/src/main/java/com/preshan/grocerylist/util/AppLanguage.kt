package com.preshan.grocerylist.util

import java.util.Locale

enum class AppLanguage {
    ENGLISH,
    SINHALA,
    TAMIL,
    HINDI,
    GERMAN,
    FRENCH,
    SPANISH,
    ARABIC,
    PORTUGUESE,
    INDONESIAN;

    companion object {
        /**
         * Parses the stored value from `app_settings.selected_language`.
         * Falls back to [ENGLISH] for unknown/empty values.
         */
        fun fromStoredValue(stored: String?): AppLanguage {
            val normalized = stored?.trim()?.lowercase(Locale.ROOT)
            return when (normalized) {
                "english" -> ENGLISH
                "sinhala" -> SINHALA
                "tamil" -> TAMIL
                "hindi" -> HINDI
                "german" -> GERMAN
                "french" -> FRENCH
                "spanish" -> SPANISH
                "arabic" -> ARABIC
                "portuguese" -> PORTUGUESE
                "indonesian" -> INDONESIAN
                else -> ENGLISH
            }
        }
    }
}

