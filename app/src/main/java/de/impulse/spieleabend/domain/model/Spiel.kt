package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache

data class Spiel(
    val id: Int,
    val lokalisierung: Lokalisierung,
    val kategorien: Set<Kategorie>,
    val inaktiv: Boolean = false,
    val selbstErstellt: Boolean = false,
    val favorit: Boolean = false,
    val bildDateiname: String? = null,
    val texteProKarte: Int = 1,
) {
    init {
        require(texteProKarte > 0) {
            "Ein Spiel muss mindestens einen Kartentext pro Karte anzeigen."
        }

        val kategorieIds = kategorien.map { kategorie -> kategorie.id }
        require(kategorieIds.distinct().size == kategorieIds.size) {
            "Ein Spiel darf eine Kategorie nur einmal referenzieren."
        }
    }

    fun text(inSprache: Sprache): String = lokalisierung.text(inSprache)
}
