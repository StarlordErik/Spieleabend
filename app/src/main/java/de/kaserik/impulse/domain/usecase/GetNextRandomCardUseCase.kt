package de.kaserik.impulse.domain.usecase

import de.kaserik.impulse.domain.model.GezogeneKarte
import de.kaserik.impulse.domain.model.Spiel
import javax.inject.Inject

class GetNextRandomCardUseCase @Inject constructor() {
    operator fun invoke(spiel: Spiel): GezogeneKarte = planNextRandomCard(spiel).karte
}
