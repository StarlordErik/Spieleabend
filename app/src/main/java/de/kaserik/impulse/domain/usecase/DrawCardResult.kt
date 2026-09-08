package de.kaserik.impulse.domain.usecase

import de.kaserik.impulse.domain.model.GezogeneKarte
import de.kaserik.impulse.domain.model.Spiel

data class DrawCardResult(
    val spiel: Spiel,
    val karte: GezogeneKarte,
    val instanceId: Long,
    val hasPrevious: Boolean,
)
