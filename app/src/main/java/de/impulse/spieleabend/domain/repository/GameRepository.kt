package de.impulse.spieleabend.domain.repository

import de.impulse.spieleabend.domain.model.Spiel

@Suppress("kotlin:S6517")
interface GameRepository {
    suspend fun getGames(): List<Spiel>

    suspend fun getGame(gameId: Int): Spiel

    suspend fun applyCardDrawStateChanges(
        resetSeenCategoryIds: Set<Int>,
        resetSeenAndPlayedCategoryIds: Set<Int>,
        seenCardTextIds: Set<Int>,
    )

    suspend fun setCardTextsPlayedState(
        cardTextIds: Set<Int>,
        gespielt: Boolean,
    )
}
