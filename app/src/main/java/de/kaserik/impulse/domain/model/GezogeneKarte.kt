package de.kaserik.impulse.domain.model

data class GezogeneKarte(
    val kartentexte: List<GezogenerKartentext>,
)

data class GezogenerKartentext(
    val kartentext: Kartentext,
    val kategorieId: Int,
)
