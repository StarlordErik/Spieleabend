package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class GetOrDrawInitialCardUseCase @Inject constructor(
    private val repository: GameRepository,
    private val drawNextRandomCard: DrawNextRandomCardUseCase,
) {
    suspend operator fun invoke(gameId: Int): DrawCardResult {
        val current = repository.getCurrentCard(gameId) ?: return drawNextRandomCard(gameId)

        return DrawCardResult(
            spiel = repository.getGame(gameId),
            karte = current.card,
            instanceId = current.instanceId,
            hasPrevious = current.hasPrevious,
        )
    }
}
