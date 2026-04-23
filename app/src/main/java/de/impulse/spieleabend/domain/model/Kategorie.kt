package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache

data class Kategorie(
    val id: Int,
    val lokalisierung: Lokalisierung,
    val kartentexte: Set<Kartentext> = emptySet(),
    val inaktiv: Boolean = false,
    val selbstErstellt: Boolean = false,
    val favorit: Boolean = false,
) {
    init {
        val kartentextIds = kartentexte.map { kartentext -> kartentext.id }
        require(kartentextIds.distinct().size == kartentextIds.size) {
            "Eine Kategorie darf einen Kartentext nur einmal referenzieren."
        }
    }

    fun text(inSprache: Sprache): String = lokalisierung.text(inSprache)
}
