package de.kaserik.impulse.domain.usecase

import de.kaserik.impulse.domain.repository.GameRepository
import javax.inject.Inject

class ResetAllCardsForAllGamesUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke() = repository.resetAllCardsForAllGames()
}
