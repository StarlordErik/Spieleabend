package de.impulse.spieleabend.domain.model

data class GezogeneKarte(
    val kartentexte: List<GezogenerKartentext>,
)

data class GezogenerKartentext(
    val kartentext: Kartentext,
    val kategorieId: String,
) {
    init {
        require(kategorieId.isNotBlank()) { "Die Kategorie-ID eines gezogenen Kartentextes darf nicht leer sein." }
    }
}
