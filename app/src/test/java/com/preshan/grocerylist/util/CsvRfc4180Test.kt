package com.preshan.grocerylist.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvRfc4180Test {

    @Test
    fun formatAndParse_roundTrip_withUnicode() {
        val cells = listOf("Food", "සම්බ සහල්", "தமிழ் பொருள்")
        val line = CsvRfc4180.formatRow(cells)
        val parsed = CsvRfc4180.parseLine(line)
        assertEquals(cells, parsed)
    }

    @Test
    fun sinhalaTamil_roundTrip_preservesUtf8InSingleRow() {
        val category = "ප්‍රවර්ගය"
        val sinhalaItem = "සම්බ සහල්"
        val tamilItem = "தமிழ் பொருள்"
        val row = CsvRfc4180.formatRow(listOf(category, sinhalaItem, tamilItem, "1", "0", "0", "", "", "1"))
        val parsed = CsvRfc4180.parseLine(row)
        assertEquals(category, parsed[0])
        assertEquals(sinhalaItem, parsed[1])
        assertEquals(tamilItem, parsed[2])
    }

    @Test
    fun parseLine_handlesQuotedCommasAndNewlines() {
        val line = "\"a,b\",\"line\nbreak\",plain"
        val parsed = CsvRfc4180.parseLine(line)
        assertEquals(listOf("a,b", "line\nbreak", "plain"), parsed)
    }

    @Test
    fun splitLogicalLines_mergesQuotedMultilineField() {
        val text = "h1,h2\n\"one\ntwo\",three"
        val lines = CsvRfc4180.splitLogicalLines(text)
        assertEquals(2, lines.size)
        assertEquals("h1,h2", lines[0])
        assertEquals("\"one\ntwo\",three", lines[1])
    }

    @Test
    fun stripBom_removesUtf8Bom() {
        val text = "\uFEFFcategory,item"
        assertEquals("category,item", CsvRfc4180.stripBom(text))
    }

    @Test
    fun parseLine_escapedQuotes() {
        val parsed = CsvRfc4180.parseLine("\"say \"\"hi\"\"\"")
        assertEquals(listOf("say \"hi\""), parsed)
        assertTrue(parsed.single().contains("hi"))
    }
}
