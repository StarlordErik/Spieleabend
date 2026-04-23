package de.impulse.spieleabend.domain.model

data class Spiel(
    val id: Int,
    val lokalisierung: Lokalisierung,
    val kategorien: Set<Kategorie>,
    val kartentexteProKarte: Int = 1,
) {
    init {
        require(id > 0) { "Die ID eines Spiels muss positiv sein." }
        require(kategorien.isNotEmpty()) { "Ein Spiel muss mindestens eine Kategorie enthalten." }
        require(kartentexteProKarte > 0) {
            "Ein Spiel muss mindestens einen Kartentext pro Karte anzeigen."
        }

        val kategorieIds = kategorien.map { kategorie -> kategorie.id }
        require(kategorieIds.distinct().size == kategorieIds.size) {
            "Ein Spiel darf eine Kategorie nur einmal referenzieren."
        }
    }

    fun mitKategorie(kategorie: Kategorie): Spiel =
        copy(kategorien = kategorien.ohneKategorie(kategorie.id) + kategorie)

    fun enthaeltKategorie(kategorieId: Int): Boolean =
        kategorien.any { kategorie -> kategorie.id == kategorieId }
}

private fun Set<Kategorie>.ohneKategorie(kategorieId: Int): Set<Kategorie> =
    filterNot { kategorie -> kategorie.id == kategorieId }.toSet()
