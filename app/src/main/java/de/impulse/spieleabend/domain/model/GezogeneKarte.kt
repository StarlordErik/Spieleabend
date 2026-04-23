package de.impulse.spieleabend.domain.model

data class GezogeneKarte(
    val kartentexte: List<GezogenerKartentext>,
)

data class GezogenerKartentext(
    val kartentext: Kartentext,
    val kategorieId: Int,
) {
    init {
        require(kategorieId > 0) { "Die Kategorie-ID eines gezogenen Kartentextes muss positiv sein." }
    }
}
