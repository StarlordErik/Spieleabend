package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache

data class Lokalisierung(
    val id: Int,
    val translationen: Set<Translation>,
    val ogSprache: Sprache,
) {
    init {
        require(translationen.any { translation -> translation.sprache == Sprache.OG }) {
            "Eine Lokalisierung braucht eine OG-Translation."
        }

        val sprachen = translationen.map { it.sprache }
        require(sprachen.distinct().size == sprachen.size) {
            "Eine Lokalisierung darf pro Sprache nur eine Translation enthalten."
        }
    }

    fun text(inSprache: Sprache): String =
        translationen.firstOrNull { translation -> translation.sprache == inSprache }?.text
            ?: translationen.first { translation -> translation.sprache == Sprache.OG }.text
}
