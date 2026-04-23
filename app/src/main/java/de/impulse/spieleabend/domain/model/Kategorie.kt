package de.impulse.spieleabend.domain.model

data class Kategorie(
    val id: Int,
    val lokalisierung: Lokalisierung,
    val kartentexte: Set<Kartentext> = emptySet(),
) {
    init {
        require(id > 0) { "Die ID einer Kategorie muss positiv sein." }

        val kartentextIds = kartentexte.map { kartentext -> kartentext.id }
        require(kartentextIds.distinct().size == kartentextIds.size) {
            "Eine Kategorie darf einen Kartentext nur einmal referenzieren."
        }
    }

    fun mitKartentext(kartentext: Kartentext): Kategorie =
        copy(kartentexte = kartentexte.ohneKartentext(kartentext.id) + kartentext)

    fun ohneKartentext(kartentextId: Int): Kategorie =
        copy(kartentexte = kartentexte.ohneKartentext(kartentextId))

    fun enthaeltKartentext(kartentextId: Int): Boolean =
        kartentexte.any { kartentext -> kartentext.id == kartentextId }
}

private fun Set<Kartentext>.ohneKartentext(kartentextId: Int): Set<Kartentext> =
    filterNot { kartentext -> kartentext.id == kartentextId }.toSet()
