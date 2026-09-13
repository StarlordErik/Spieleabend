package de.kaserik.impulse.domain.usecase

import de.kaserik.impulse.domain.repository.GameRepository
import javax.inject.Inject

class ShowPreviousCardUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend fun preview(gameId: Int) = repository.getPreviousCard(gameId)

    suspend operator fun invoke(gameId: Int): DrawCardResult? {
        val previous = repository.popCurrentCard(gameId) ?: return null

        return DrawCardResult(
            spiel = repository.getGame(gameId),
            karte = previous.card,
            instanceId = previous.instanceId,
            hasPrevious = previous.hasPrevious,
        )
    }
}
