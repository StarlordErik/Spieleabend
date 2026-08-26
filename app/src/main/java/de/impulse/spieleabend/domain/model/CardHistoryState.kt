package de.impulse.spieleabend.domain.model

data class CardHistoryState(
    val card: GezogeneKarte,
    val instanceId: Long,
    val hasPrevious: Boolean,
)
