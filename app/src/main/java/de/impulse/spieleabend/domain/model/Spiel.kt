package de.impulse.spieleabend.domain.model

data class Spiel(
    override val lokalisierung: Lokalisierung,
    val kategorien: Set<Kategorie>,
    override val inaktiv: Boolean = false,
    override val selbstErstellt: Boolean = false,
    override val favorit: Boolean = false,
    val bildDateiname: String? = null,
    val texteProKarte: Int = 1,
) : Spielelement(lokalisierung, inaktiv, selbstErstellt, favorit) {
    init {
        require(texteProKarte > 0) {
            "Ein Spiel muss mindestens einen Kartentext pro Karte anzeigen."
        }

        val kategorieIds = kategorien.map { kategorie -> kategorie.id() }
        require(kategorieIds.distinct().size == kategorieIds.size) {
            "Ein Spiel darf eine Kategorie nur einmal referenzieren."
        }
    }
}
