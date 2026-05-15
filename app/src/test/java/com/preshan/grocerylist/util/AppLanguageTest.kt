package com.preshan.grocerylist.util

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {

    @Test
    fun fromStoredValue_parsesKnownLanguages() {
        assertEquals(AppLanguage.SINHALA, AppLanguage.fromStoredValue("Sinhala"))
        assertEquals(AppLanguage.TAMIL, AppLanguage.fromStoredValue("tamil"))
        assertEquals(AppLanguage.ARABIC, AppLanguage.fromStoredValue(" Arabic "))
    }

    @Test
    fun fromStoredValue_fallsBackToEnglish_forUnknownOrEmpty() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromStoredValue(null))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromStoredValue(""))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromStoredValue("Klingon"))
    }
}
