package de.impulse.spieleabend.common

import java.util.Locale

enum class Sprache(
    val code: String,
) {
    DE("de"),
    EN("en"),
    OG("og"),
    ;

    companion object {
        fun fromCode(code: String): Sprache? {
            val normalisierterCode = code.trim().lowercase(Locale.ROOT)
            if (normalisierterCode.isBlank()) {
                return null
            }

            val sprache = normalisierterCode.substringBefore('-')
            return entries.firstOrNull { eintrag -> eintrag.code == sprache }
        }

        fun fromLocale(locale: Locale): Sprache? = fromCode(locale.toLanguageTag())
    }
}
