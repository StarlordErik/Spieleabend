package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class ShowPreviousCardUseCase @Inject constructor(
    private val repository: GameRepository,
) {
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
