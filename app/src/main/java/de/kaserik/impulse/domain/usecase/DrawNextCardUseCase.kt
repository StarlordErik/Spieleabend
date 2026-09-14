package de.kaserik.impulse.domain.usecase

import de.kaserik.impulse.domain.repository.GameRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DrawNextCardUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke(gameId: Int, categoryId: Int?): DrawCardResult =
        commit(gameId, prepare(gameId, categoryId))

    // Preparing a swipe must not mark texts as seen or change the card history.
    internal suspend fun prepare(gameId: Int, categoryId: Int?): PlannedCardDraw = withContext(Dispatchers.Default) {
        val game = repository.getGame(gameId)
        if (categoryId == null) planNextRandomCard(game) else planNextCardFromCategory(game, categoryId)
    }

    internal suspend fun prepareAll(gameId: Int): Map<Int?, PlannedCardDraw> = withContext(Dispatchers.Default) {
        val game = repository.getGame(gameId)
        buildMap {
            put(null, planNextRandomCard(game))
            game.kategorien.forEach { category ->
                put(category.id(), planNextCardFromCategory(game, category.id()))
            }
        }
    }

    internal suspend fun commit(gameId: Int, draw: PlannedCardDraw): DrawCardResult {
        val history = repository.commitCardDraw(
            gameId = gameId,
            resetSeenCardTextIds = draw.resetSeenKartentextIds,
            resetSeenAndPlayedCardTextIds = draw.resetSeenUndGespieltKartentextIds,
            card = draw.karte,
        )
        return DrawCardResult(repository.getGame(gameId), history.card, history.instanceId, history.hasPrevious)
    }
}
