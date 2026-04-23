package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache

data class Lokalisierung(
    val id: String,
    val translationen: Set<Translation>,
) {
    init {
        require(id.isNotBlank()) { "Die ID einer Lokalisierung darf nicht leer sein." }
        require(translationen.isNotEmpty()) { "Eine Lokalisierung braucht mindestens eine Translation." }

        val sprachen = translationen.map { it.sprache }
        require(sprachen.distinct().size == sprachen.size) {
            "Eine Lokalisierung darf pro Sprache nur eine Translation enthalten."
        }
    }

    fun textFuer(sprache: Sprache): String? =
        translationen.firstOrNull { translation -> translation.istFuer(sprache) }?.text

    fun textFuer(sprache: String): String? = Sprache.fromCode(sprache)?.let(::textFuer)

    fun textFuer(
        sprache: Sprache,
        fallbackSprache: Sprache,
    ): String? = textFuer(sprache) ?: textFuer(fallbackSprache)

    fun textFuer(
        sprache: String,
        fallbackSprache: String,
    ): String? = textFuer(sprache) ?: textFuer(fallbackSprache)
}
