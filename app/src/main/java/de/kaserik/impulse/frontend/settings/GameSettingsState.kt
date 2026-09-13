package de.kaserik.impulse.frontend.settings

import de.kaserik.impulse.domain.model.BearbeiteteKartentexteModus
import de.kaserik.impulse.domain.model.FavoritenModus
import de.kaserik.impulse.domain.model.GeloeschteKartentexteModus

data class GameSettingsState(
    val textsPerCard: Int,
    val defaultTextsPerCard: Int,
    val developerMode: Boolean,
    val supportsFunFactsMode: Boolean = false,
    val funFactsModeEnabled: Boolean = false,
    val supportsPrivacyMode: Boolean = false,
    val privacyModeEnabled: Boolean = false,
    val deletedCardTextsMode: GeloeschteKartentexteModus = GeloeschteKartentexteModus.ALS_LETZTE,
    val favoritesMode: FavoritenModus = FavoritenModus.UNBEACHTET,
    val editedCardTextsMode: BearbeiteteKartentexteModus = BearbeiteteKartentexteModus.UNBEACHTET,
)

data class GameSettingsActions(
    val onFunFactsModeChanged: (Boolean) -> Unit = {},
    val onPrivacyModeChanged: (Boolean) -> Unit = {},
    val onDeletedCardTextsModeChanged: (GeloeschteKartentexteModus) -> Unit = {},
    val onFavoritesModeChanged: (FavoritenModus) -> Unit = {},
    val onEditedCardTextsModeChanged: (BearbeiteteKartentexteModus) -> Unit = {},
    val onResetSeenCards: () -> Unit = {},
    val onResetAllCards: () -> Unit = {},
    val onTextsPerCardChanged: (Int) -> Unit = {},
    val onResetTextsPerCard: () -> Unit = {},
    val customTranslationActions: CustomTranslationActions = CustomTranslationActions(),
)

data class CustomTranslationActions(
    val onApplyErikTranslations: (Boolean) -> Unit = {},
    val onResetCustomTranslations: () -> Unit = {},
)
