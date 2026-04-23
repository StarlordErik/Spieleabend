package de.impulse.spieleabend.domain.model

data class Kategorie(
    override val lokalisierung: Lokalisierung,
    val kartentexte: Set<Kartentext> = emptySet(),
    override val inaktiv: Boolean = false,
    override val selbstErstellt: Boolean = false,
    override val favorit: Boolean = false,
) : Spielelement(lokalisierung, inaktiv, selbstErstellt, favorit) {
    init {
        val kartentextIds = kartentexte.map { kartentext -> kartentext.id() }
        require(kartentextIds.distinct().size == kartentextIds.size) {
            "Eine Kategorie darf einen Kartentext nur einmal referenzieren."
        }
    }
}
