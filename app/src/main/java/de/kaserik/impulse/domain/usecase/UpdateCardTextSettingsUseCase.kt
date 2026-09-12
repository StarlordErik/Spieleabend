package de.kaserik.impulse.domain.usecase

import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.model.BearbeiteteKartentexteModus
import de.kaserik.impulse.domain.model.FavoritenModus
import de.kaserik.impulse.domain.model.GeloeschteKartentexteModus
import de.kaserik.impulse.domain.repository.GameRepository
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

    suspend fun applyErikTranslations(
        gameId: Int?,
        overwriteExisting: Boolean,
    ) = repository.applyErikCardTextTranslations(gameId, overwriteExisting)

    suspend fun resetCustomTranslations(gameId: Int?) = repository.resetCustomCardTextTranslations(gameId)

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
