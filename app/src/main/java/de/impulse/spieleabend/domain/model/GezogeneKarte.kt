package de.impulse.spieleabend.domain.model

data class GezogeneKarte(
    val kartentexte: List<GezogenerKartentext>,
) {
    init {
        require(kartentexte.isNotEmpty()) {
            "Eine gezogene Karte muss mindestens einen Kartentext enthalten."
        }
    }
}

data class GezogenerKartentext(
    val kartentext: Kartentext,
    val kategorieId: Int,
)
