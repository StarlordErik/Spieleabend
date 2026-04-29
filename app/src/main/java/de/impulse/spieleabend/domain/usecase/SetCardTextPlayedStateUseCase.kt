package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class SetCardTextPlayedStateUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke(
        cardTextId: Int,
        gespielt: Boolean,
    ) {
        repository.setCardTextsPlayedState(
            cardTextIds = setOf(cardTextId),
            gespielt = gespielt,
        )
    }
}
