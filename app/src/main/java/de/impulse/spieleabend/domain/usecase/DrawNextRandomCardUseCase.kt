package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class DrawNextRandomCardUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke(gameId: Int): DrawCardResult {
        val spiel = repository.getGame(gameId)
        val plannedCardDraw = planNextRandomCard(spiel)

        repository.applyCardDrawStateChanges(
            resetSeenCategoryIds = plannedCardDraw.resetSeenKategorieIds,
            resetSeenAndPlayedCategoryIds = plannedCardDraw.resetSeenUndGespieltKategorieIds,
            seenCardTextIds =
                plannedCardDraw.karte.kartentexte
                    .map { gezogenerKartentext -> gezogenerKartentext.kartentext.id() }
                    .toSet(),
        )

        return DrawCardResult(
            spiel = repository.getGame(gameId),
            karte = plannedCardDraw.karte,
        )
    }
}
