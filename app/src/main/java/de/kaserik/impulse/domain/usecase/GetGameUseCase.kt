package de.kaserik.impulse.domain.usecase

import de.kaserik.impulse.domain.model.Spiel
import de.kaserik.impulse.domain.repository.GameRepository
import javax.inject.Inject

class GetGameUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke(gameId: Int): Spiel = repository.getGame(gameId)
}
