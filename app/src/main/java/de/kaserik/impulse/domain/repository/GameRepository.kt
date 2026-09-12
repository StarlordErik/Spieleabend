package de.kaserik.impulse.domain.repository

import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.model.BearbeiteteKartentexteModus
import de.kaserik.impulse.domain.model.CardHistoryState
import de.kaserik.impulse.domain.model.FavoritenModus
import de.kaserik.impulse.domain.model.GeloeschteKartentexteModus
import de.kaserik.impulse.domain.model.GezogeneKarte
import de.kaserik.impulse.domain.model.Spiel

@Suppress("kotlin:S6517", "TooManyFunctions")
interface GameRepository {
    suspend fun getGames(): List<Spiel>

    suspend fun getGame(gameId: Int): Spiel

    suspend fun commitCardDraw(
        gameId: Int,
        resetSeenCardTextIds: Set<Int>,
        resetSeenAndPlayedCardTextIds: Set<Int>,
        card: GezogeneKarte,
    ): CardHistoryState

    suspend fun getCurrentCard(gameId: Int): CardHistoryState?

    suspend fun popCurrentCard(gameId: Int): CardHistoryState?

    suspend fun setCardTextsPlayedState(
        cardTextIds: Set<Int>,
        gespielt: Boolean,
    )

    suspend fun setCardTextDeletedState(
        cardTextId: Int,
        deleted: Boolean,
    )

    suspend fun setCardTextFavoriteState(
        cardTextId: Int,
        favorite: Boolean,
    )

    suspend fun setCustomCardTextTranslation(
        cardTextId: Int,
        language: Sprache,
        text: String?,
    )

    suspend fun applyErikCardTextTranslations(gameId: Int?, overwriteExisting: Boolean)

    suspend fun resetCustomCardTextTranslations(gameId: Int?)

    suspend fun resetSeenCards(gameId: Int)

    suspend fun resetAllCards(gameId: Int)

    suspend fun resetAllCardsForAllGames()

    suspend fun setTextsPerCardOverride(
        gameId: Int,
        value: Int?,
    )

    suspend fun setDeletedCardTextsMode(
        gameId: Int,
        mode: GeloeschteKartentexteModus,
    )

    suspend fun setFavoritesMode(
        gameId: Int,
        mode: FavoritenModus,
    )

    suspend fun setEditedCardTextsMode(
        gameId: Int,
        mode: BearbeiteteKartentexteModus,
    )
}
