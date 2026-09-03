package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class DrawNextCardFromCategoryUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke(
        gameId: Int,
        kategorieId: Int,
    ): DrawCardResult {
        val spiel = repository.getGame(gameId)
        val plannedCardDraw = planNextCardFromCategory(
            spiel = spiel,
            kategorieId = kategorieId,
        )

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
