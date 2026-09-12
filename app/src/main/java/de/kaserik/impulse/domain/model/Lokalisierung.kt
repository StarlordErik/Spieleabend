package de.kaserik.impulse.domain.model

import de.kaserik.impulse.common.AppMessages
import de.kaserik.impulse.common.Sprache

data class Lokalisierung(
    val id: Int,
    val translationen: Set<Translation>,
    val ogSprache: Sprache,
) {
    init {
        require(translationen.any { translation -> translation.sprache == Sprache.OG }) {
            AppMessages.MISSING_ORIGINAL_TRANSLATION
        }

        val sprachen = translationen.map { it.sprache }
        require(sprachen.distinct().size == sprachen.size) {
            AppMessages.DUPLICATE_TRANSLATION_LANGUAGE
        }
    }

    fun text(inSprache: Sprache): String = lokalisierterText(inSprache).text

    @Suppress("ReturnCount")
    fun lokalisierterText(inSprache: Sprache): LokalisierterText {
        val translationenNachSprache = translationen.associateBy { translation -> translation.sprache }
        val original = translationenNachSprache.getValue(Sprache.OG)

        if (inSprache == Sprache.OG) {
            return LokalisierterText(text = original.text, uebersetzungFehlt = false)
        }

        if (inSprache == Sprache.EIGENE_DE || inSprache == Sprache.EIGENE_EN) {
            val direkt = translationenNachSprache[inSprache]
            return LokalisierterText(
                text = direkt?.text ?: original.text,
                uebersetzungFehlt = direkt == null,
            )
        }

        require(inSprache.auswaehlbar || inSprache == Sprache.ERIK) {
            AppMessages.unsupportedLanguage(inSprache)
        }

        translationenNachSprache[inSprache.eigeneSprache()]?.let { eigeneTranslation ->
            return LokalisierterText(
                text = eigeneTranslation.text,
                uebersetzungFehlt = false,
                eigeneLokalisierung = true,
            )
        }

        translationenNachSprache[inSprache]?.let { direkteTranslation ->
            return LokalisierterText(text = direkteTranslation.text, uebersetzungFehlt = false)
        }

        if (inSprache == Sprache.ERIK) {
            translationenNachSprache[Sprache.DE]?.let { deutscheTranslation ->
                return LokalisierterText(text = deutscheTranslation.text, uebersetzungFehlt = false)
            }
        }

        val passendeOriginalsprache = when (inSprache) {
            Sprache.ERIK -> ogSprache == Sprache.DE || ogSprache == Sprache.ERIK
            else -> ogSprache == inSprache
        }
        return LokalisierterText(
            text = original.text,
            uebersetzungFehlt = !passendeOriginalsprache,
        )
    }

    fun hatEigeneLokalisierung(): Boolean =
        translationen.any { translation ->
            translation.sprache == Sprache.EIGENE_DE || translation.sprache == Sprache.EIGENE_EN
        }

    fun hatEigeneLokalisierungFuer(sprache: Sprache): Boolean =
        (sprache.auswaehlbar || sprache == Sprache.ERIK) && translationen.any { translation ->
            translation.sprache == sprache.eigeneSprache()
        }
}

data class LokalisierterText(
    val text: String,
    val uebersetzungFehlt: Boolean,
    val eigeneLokalisierung: Boolean = false,
)
