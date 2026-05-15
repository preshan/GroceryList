package com.preshan.grocerylist.util

/**
 * Minimal RFC 4180–style CSV parsing and writing for UTF-8 text.
 * Handles quoted fields, escaped quotes, and embedded newlines inside quotes.
 */
object CsvRfc4180 {

    fun escapeField(value: String): String {
        val needsQuotes = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        if (!needsQuotes) return value
        val doubled = value.replace("\"", "\"\"")
        return "\"$doubled\""
    }

    fun formatRow(cells: List<String>): String =
        cells.joinToString(",") { escapeField(it) }

    /**
     * Splits [text] into physical record lines, merging segments separated by newlines
     * that fall inside a quoted field.
     */
    fun splitLogicalLines(text: String): List<String> {
        if (text.isEmpty()) return emptyList()
        val lines = mutableListOf<StringBuilder>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < text.length && text[i + 1] == '"') {
                        current.append('"')
                        current.append('"')
                        i += 2
                        continue
                    }
                    inQuotes = !inQuotes
                    current.append(c)
                    i++
                }
                (c == '\n' || c == '\r') && !inQuotes -> {
                    if (c == '\r' && i + 1 < text.length && text[i + 1] == '\n') {
                        i++
                    }
                    lines.add(current)
                    current = StringBuilder()
                    i++
                }
                else -> {
                    current.append(c)
                    i++
                }
            }
        }
        lines.add(current)
        return lines.map { it.toString() }.filter { it.isNotEmpty() }
    }

    /**
     * Parses a single CSV line into cells.
     */
    fun parseLine(line: String): List<String> {
        val out = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        sb.append('"')
                        i += 2
                        continue
                    }
                    inQuotes = !inQuotes
                    i++
                }
                c == ',' && !inQuotes -> {
                    out.add(sb.toString())
                    sb.clear()
                    i++
                }
                else -> {
                    sb.append(c)
                    i++
                }
            }
        }
        out.add(sb.toString())
        return out
    }

    fun stripBom(text: String): String =
        if (text.startsWith('\uFEFF')) text.substring(1) else text
}
