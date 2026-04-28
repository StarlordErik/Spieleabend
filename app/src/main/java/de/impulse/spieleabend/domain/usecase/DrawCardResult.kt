package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.Spiel

data class DrawCardResult(
    val spiel: Spiel,
    val karte: GezogeneKarte,
)
