package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.Spiel
import javax.inject.Inject

class GetNextRandomCardUseCase @Inject constructor() {
    operator fun invoke(spiel: Spiel): GezogeneKarte = planNextRandomCard(spiel).karte
}
