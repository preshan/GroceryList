package com.preshan.grocerylist.data.locale

/** Single dropdown for country / region (display strings match persisted values). */
object CountryLanguageDefaults {

    const val DEFAULT_COUNTRY_REGION = "International"
    const val DEFAULT_LANGUAGE = "English"

    val countryRegions: List<String> = listOf(
        "International",
        "United States",
        "United Kingdom",
        "Australia",
        "Canada",
        "New Zealand",
        "Sri Lanka",
        "India",
        "Singapore",
        "Malaysia",
        "Indonesia",
        "United Arab Emirates",
        "Saudi Arabia",
        "Qatar",
        "Kuwait",
        "Germany",
        "France",
        "Spain",
        "Italy",
        "Netherlands",
        "Europe",
        "Asia",
        "South Asia",
        "Middle East",
        "Oceania",
        "Africa",
        "South America"
    )

    val languages: List<String> = listOf(
        "English",
        "Sinhala",
        "Tamil",
        "Hindi",
        "German",
        "French",
        "Spanish",
        "Arabic",
        "Portuguese",
        "Indonesian"
    )

    /** Default language when country / region changes (must exist in [languages]). */
    fun defaultLanguageForCountry(countryRegion: String): String {
        return when (countryRegion) {
            "International",
            "United States",
            "United Kingdom",
            "Australia",
            "Canada",
            "New Zealand",
            "Singapore",
            "Malaysia",
            "Italy",
            "Netherlands",
            "Europe",
            "Asia",
            "South Asia",
            "Oceania",
            "Africa" -> "English"

            "Sri Lanka" -> "Sinhala"
            "India" -> "Hindi"
            "Indonesia" -> "Indonesian"
            "United Arab Emirates",
            "Saudi Arabia",
            "Qatar",
            "Kuwait",
            "Middle East" -> "Arabic"

            "Germany" -> "German"
            "France" -> "French"
            "Spain" -> "Spanish"
            "South America" -> "Spanish"
            else -> DEFAULT_LANGUAGE
        }
    }
}
