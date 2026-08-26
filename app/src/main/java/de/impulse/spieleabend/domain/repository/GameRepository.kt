package de.impulse.spieleabend.domain.repository

import de.impulse.spieleabend.domain.model.CardHistoryState
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.Spiel

@Suppress("kotlin:S6517")
interface GameRepository {
    suspend fun getGames(): List<Spiel>

    suspend fun getGame(gameId: Int): Spiel

    suspend fun commitCardDraw(
        gameId: Int,
        resetSeenCategoryIds: Set<Int>,
        resetSeenAndPlayedCategoryIds: Set<Int>,
        card: GezogeneKarte,
    ): CardHistoryState

    suspend fun getCurrentCard(gameId: Int): CardHistoryState?

    suspend fun popCurrentCard(gameId: Int): CardHistoryState?

    suspend fun setCardTextsPlayedState(
        cardTextIds: Set<Int>,
        gespielt: Boolean,
    )

    suspend fun resetSeenCards(gameId: Int)

    suspend fun resetAllCards(gameId: Int)

    suspend fun resetAllCardsForAllGames()

    suspend fun setTextsPerCardOverride(
        gameId: Int,
        value: Int?,
    )
}
