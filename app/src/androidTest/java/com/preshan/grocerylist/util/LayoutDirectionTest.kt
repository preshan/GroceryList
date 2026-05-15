package com.preshan.grocerylist.util

import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LayoutDirectionTest {

    @Test
    fun layoutDirection_arabicIsRtl_othersAreLtr() {
        assertEquals(LayoutDirection.Rtl, layoutDirectionFor(AppLanguage.ARABIC))
        assertEquals(LayoutDirection.Ltr, layoutDirectionFor(AppLanguage.ENGLISH))
        assertEquals(LayoutDirection.Ltr, layoutDirectionFor(AppLanguage.SINHALA))
    }
}
