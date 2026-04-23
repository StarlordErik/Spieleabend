package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache

data class Lokalisierung(
    val id: Int,
    val translationen: Set<Translation>,
    val ogSprache: Sprache,
) {
    init {
        require(id > 0) { "Die ID einer Lokalisierung muss positiv sein." }
        require(translationen.isNotEmpty()) { "Eine Lokalisierung braucht mindestens eine Translation." }

        val sprachen = translationen.map { it.sprache }
        require(sprachen.distinct().size == sprachen.size) {
            "Eine Lokalisierung darf pro Sprache nur eine Translation enthalten."
        }
    }

    fun textFuer(sprache: Sprache): String? =
        translationen.firstOrNull { translation -> translation.istFuer(sprache) }?.text

    fun textFuer(
        sprache: Sprache,
        fallbackSprache: Sprache,
    ): String? = textFuer(sprache) ?: textFuer(fallbackSprache)
}
