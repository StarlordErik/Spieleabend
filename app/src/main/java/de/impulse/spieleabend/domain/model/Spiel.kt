package de.impulse.spieleabend.domain.model

data class Spiel(
    override val lokalisierung: Lokalisierung,
    val originaleKategorien: Set<Kategorie> = emptySet(),
    val hinzugefuegteKategorien: Set<Kategorie> = emptySet(),
    val inaktiveKategorien: Set<Kategorie> = emptySet(),
    override val inaktiv: Boolean = false,
    override val selbstErstellt: Boolean = false,
    override val favorit: Boolean = false,
    val bildDateiname: String? = null,
    val texteProKarte: Int = 1,
) : Spielelement(lokalisierung, inaktiv, selbstErstellt, favorit) {
    val kategorien: Set<Kategorie>
        get() {
            val inaktiveKategorieIds = inaktiveKategorien.map { kategorie -> kategorie.id() }.toSet()

            return (originaleKategorien + hinzugefuegteKategorien)
                .filterNot { kategorie -> kategorie.id() in inaktiveKategorieIds }
                .toSet()
        }

    init {
        require(texteProKarte > 0) {
            "Ein Spiel muss mindestens einen Kartentext pro Karte anzeigen."
        }

        val originaleKategorieIds = originaleKategorien.map { kategorie -> kategorie.id() }
        require(originaleKategorieIds.distinct().size == originaleKategorieIds.size) {
            "Ein Spiel darf eine originale Kategorie nur einmal referenzieren."
        }

        val hinzugefuegteKategorieIds = hinzugefuegteKategorien.map { kategorie -> kategorie.id() }
        require(hinzugefuegteKategorieIds.distinct().size == hinzugefuegteKategorieIds.size) {
            "Ein Spiel darf eine hinzugefuegte Kategorie nur einmal referenzieren."
        }

        val inaktiveKategorieIds = inaktiveKategorien.map { kategorie -> kategorie.id() }
        require(inaktiveKategorieIds.distinct().size == inaktiveKategorieIds.size) {
            "Ein Spiel darf eine inaktive Kategorie nur einmal referenzieren."
        }

        val doppelteKategorieIds = originaleKategorieIds.intersect(hinzugefuegteKategorieIds.toSet())
        require(doppelteKategorieIds.isEmpty()) {
            "Ein Spiel darf eine Kategorie nicht gleichzeitig original und hinzugefuegt referenzieren."
        }

        val bekannteKategorieIds = (originaleKategorieIds + hinzugefuegteKategorieIds).toSet()
        require(inaktiveKategorieIds.all { kategorieId -> kategorieId in bekannteKategorieIds }) {
            "Ein Spiel darf nur referenzierte Kategorien als inaktiv markieren."
        }

        val kategorieIds = bekannteKategorieIds.toList()
        require(kategorieIds.distinct().size == kategorieIds.size) {
            "Ein Spiel darf eine Kategorie nur einmal referenzieren."
        }
    }
}
