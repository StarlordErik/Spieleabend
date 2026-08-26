package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class ResetAllCardsForAllGamesUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke() = repository.resetAllCardsForAllGames()
}
