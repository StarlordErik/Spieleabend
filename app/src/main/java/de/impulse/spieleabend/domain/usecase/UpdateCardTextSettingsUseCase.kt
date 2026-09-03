package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.BearbeiteteKartentexteModus
import de.impulse.spieleabend.domain.model.FavoritenModus
import de.impulse.spieleabend.domain.model.GeloeschteKartentexteModus
import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class UpdateCardTextSettingsUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend fun setDeleted(
        cardTextId: Int,
        deleted: Boolean,
    ) = repository.setCardTextDeletedState(cardTextId, deleted)

    suspend fun setFavorite(
        cardTextId: Int,
        favorite: Boolean,
    ) = repository.setCardTextFavoriteState(cardTextId, favorite)

    suspend fun setCustomTranslation(
        cardTextId: Int,
        language: Sprache,
        text: String?,
    ) = repository.setCustomCardTextTranslation(cardTextId, language, text)

    suspend fun setDeletedMode(
        gameId: Int,
        mode: GeloeschteKartentexteModus,
    ) = repository.setDeletedCardTextsMode(gameId, mode)

    suspend fun setFavoritesMode(
        gameId: Int,
        mode: FavoritenModus,
    ) = repository.setFavoritesMode(gameId, mode)

    suspend fun setEditedMode(
        gameId: Int,
        mode: BearbeiteteKartentexteModus,
    ) = repository.setEditedCardTextsMode(gameId, mode)
}
