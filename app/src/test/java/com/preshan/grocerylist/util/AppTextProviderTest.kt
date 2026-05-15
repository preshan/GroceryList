package com.preshan.grocerylist.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppTextProviderTest {

    @Test
    fun everyTextKey_hasNonBlankEnglishDefault() {
        AppTextKey.entries.forEach { key ->
            val text = AppTextProvider.getText(key, AppLanguage.ENGLISH)
            assertTrue("English text missing for $key", text.isNotBlank())
            assertNotEquals("English should not fall back to raw key id for $key", key.id, text)
        }
    }

    @Test
    fun getText_usesLanguageOverride_whenPresent() {
        val english = AppTextProvider.getText(AppTextKey.SETTINGS, AppLanguage.ENGLISH)
        val sinhala = AppTextProvider.getText(AppTextKey.SETTINGS, AppLanguage.SINHALA)
        assertNotEquals(english, sinhala)
    }

    @Test
    fun getText_fallsBackToEnglish_whenLanguageHasNoOverride() {
        val english = AppTextProvider.getText(AppTextKey.DEVELOPER_APP_TITLE, AppLanguage.ENGLISH)
        val sinhala = AppTextProvider.getText(AppTextKey.DEVELOPER_APP_TITLE, AppLanguage.SINHALA)
        val tamil = AppTextProvider.getText(AppTextKey.DEVELOPER_APP_TITLE, AppLanguage.TAMIL)
        assertEquals(english, sinhala)
        assertEquals(english, tamil)
    }

}
