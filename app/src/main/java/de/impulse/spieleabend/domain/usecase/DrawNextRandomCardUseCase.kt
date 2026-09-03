package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class DrawNextRandomCardUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke(gameId: Int): DrawCardResult {
        val spiel = repository.getGame(gameId)
        val plannedCardDraw = planNextRandomCard(spiel)

        val historyState = repository.commitCardDraw(
            gameId = gameId,
            resetSeenCardTextIds = plannedCardDraw.resetSeenKartentextIds,
            resetSeenAndPlayedCardTextIds = plannedCardDraw.resetSeenUndGespieltKartentextIds,
            card = plannedCardDraw.karte,
        )

        return DrawCardResult(
            spiel = repository.getGame(gameId),
            karte = historyState.card,
            instanceId = historyState.instanceId,
            hasPrevious = historyState.hasPrevious,
        )
    }
}
