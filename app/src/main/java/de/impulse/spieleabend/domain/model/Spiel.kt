package de.impulse.spieleabend.domain.model

data class Spiel(
    val id: String,
    val lokalisierung: Lokalisierung,
    val kategorien: Set<Kategorie> = emptySet(),
) {
    init {
        require(id.isNotBlank()) { "Die ID eines Spiels darf nicht leer sein." }

        val kategorieIds = kategorien.map { kategorie -> kategorie.id }
        require(kategorieIds.distinct().size == kategorieIds.size) {
            "Ein Spiel darf eine Kategorie nur einmal referenzieren."
        }
    }

    fun mitKategorie(kategorie: Kategorie): Spiel =
        copy(kategorien = kategorien.ohneKategorie(kategorie.id) + kategorie)

    fun ohneKategorie(kategorieId: String): Spiel =
        copy(kategorien = kategorien.ohneKategorie(kategorieId))

    fun enthaeltKategorie(kategorieId: String): Boolean =
        kategorien.any { kategorie -> kategorie.id == kategorieId }
}

private fun Set<Kategorie>.ohneKategorie(kategorieId: String): Set<Kategorie> =
    filterNot { kategorie -> kategorie.id == kategorieId }.toSet()
