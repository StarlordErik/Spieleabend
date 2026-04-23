package de.impulse.spieleabend.domain.model

data class Kategorie(
    override val lokalisierung: Lokalisierung,
    val originaleKartentexte: Set<Kartentext> = emptySet(),
    val hinzugefuegteKartentexte: Set<Kartentext> = emptySet(),
    val inaktiveKartentexte: Set<Kartentext> = emptySet(),
    override val inaktiv: Boolean = false,
    override val selbstErstellt: Boolean = false,
    override val favorit: Boolean = false,
) : Spielelement(lokalisierung, inaktiv, selbstErstellt, favorit) {
    val kartentexte: Set<Kartentext>
        get() {
            val inaktiveKartentextIds = inaktiveKartentexte.map { kartentext -> kartentext.id() }.toSet()

            return (originaleKartentexte + hinzugefuegteKartentexte)
                .filterNot { kartentext -> kartentext.id() in inaktiveKartentextIds }
                .toSet()
        }

    init {
        val originaleKartentextIds = originaleKartentexte.map { kartentext -> kartentext.id() }
        require(originaleKartentextIds.distinct().size == originaleKartentextIds.size) {
            "Eine Kategorie darf einen originalen Kartentext nur einmal referenzieren."
        }

        val hinzugefuegteKartentextIds = hinzugefuegteKartentexte.map { kartentext -> kartentext.id() }
        require(hinzugefuegteKartentextIds.distinct().size == hinzugefuegteKartentextIds.size) {
            "Eine Kategorie darf einen hinzugefuegten Kartentext nur einmal referenzieren."
        }

        val inaktiveKartentextIds = inaktiveKartentexte.map { kartentext -> kartentext.id() }
        require(inaktiveKartentextIds.distinct().size == inaktiveKartentextIds.size) {
            "Eine Kategorie darf einen inaktiven Kartentext nur einmal referenzieren."
        }

        val doppelteKartentextIds = originaleKartentextIds.intersect(hinzugefuegteKartentextIds.toSet())
        require(doppelteKartentextIds.isEmpty()) {
            "Eine Kategorie darf einen Kartentext nicht gleichzeitig original und hinzugefuegt referenzieren."
        }

        val bekannteKartentextIds = (originaleKartentextIds + hinzugefuegteKartentextIds).toSet()
        require(inaktiveKartentextIds.all { kartentextId -> kartentextId in bekannteKartentextIds }) {
            "Eine Kategorie darf nur referenzierte Kartentexte als inaktiv markieren."
        }

        val kartentextIds = bekannteKartentextIds.toList()
        require(kartentextIds.distinct().size == kartentextIds.size) {
            "Eine Kategorie darf einen Kartentext nur einmal referenzieren."
        }
    }
}
