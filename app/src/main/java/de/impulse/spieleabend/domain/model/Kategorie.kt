package de.impulse.spieleabend.domain.model

data class Kategorie(
    val id: String,
    val lokalisierung: Lokalisierung,
    val kartentexte: Set<Kartentext> = emptySet(),
) {
    init {
        require(id.isNotBlank()) { "Die ID einer Kategorie darf nicht leer sein." }

        val kartentextIds = kartentexte.map { kartentext -> kartentext.id }
        require(kartentextIds.distinct().size == kartentextIds.size) {
            "Eine Kategorie darf einen Kartentext nur einmal referenzieren."
        }
    }

    fun mitKartentext(kartentext: Kartentext): Kategorie =
        copy(kartentexte = kartentexte.ohneKartentext(kartentext.id) + kartentext)

    fun ohneKartentext(kartentextId: String): Kategorie =
        copy(kartentexte = kartentexte.ohneKartentext(kartentextId))

    fun enthaeltKartentext(kartentextId: String): Boolean =
        kartentexte.any { kartentext -> kartentext.id == kartentextId }
}

private fun Set<Kartentext>.ohneKartentext(kartentextId: String): Set<Kartentext> =
    filterNot { kartentext -> kartentext.id == kartentextId }.toSet()
