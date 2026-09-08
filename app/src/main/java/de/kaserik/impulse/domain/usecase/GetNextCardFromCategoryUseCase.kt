package de.kaserik.impulse.domain.usecase

import de.kaserik.impulse.domain.model.GezogeneKarte
import de.kaserik.impulse.domain.model.Spiel
import javax.inject.Inject

class GetNextCardFromCategoryUseCase @Inject constructor() {
    operator fun invoke(
        spiel: Spiel,
        kategorieId: Int,
    ): GezogeneKarte = planNextCardFromCategory(spiel, kategorieId).karte
}
