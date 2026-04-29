package de.impulse.spieleabend.frontend.game

import androidx.compose.runtime.Immutable
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Spiel

@Immutable
sealed interface GameScreenUiState {
    @Immutable
    data object Loading : GameScreenUiState

    @Immutable
    data class Loaded(
        val game: GameUiState,
    ) : GameScreenUiState
}

@Immutable
data class GameUiState(
    val spielName: String,
    val aktuelleKarte: GameCardUiModel,
    val kategorien: List<GameKategorieUiModel>,
)

@Immutable
data class GameCardUiModel(
    val instanceId: Long,
    val kartentexte: List<GameKartentextUiModel>,
)

@Immutable
data class GameKartentextUiModel(
    val id: Int,
    val text: String,
    val kategorieId: Int,
    val gespielt: Boolean,
)

@Immutable
data class GameKategorieUiModel(
    val id: Int,
    val name: String,
)

internal fun Spiel.toGameUiState(
    aktuelleKarte: GezogeneKarte,
    sprache: Sprache,
    cardInstanceId: Long,
): GameUiState =
    GameUiState(
        spielName = text(sprache),
        aktuelleKarte = aktuelleKarte.toGameCardUiModel(sprache, cardInstanceId),
        kategorien = kategorien.map { kategorie ->
            GameKategorieUiModel(
                id = kategorie.id(),
                name = kategorie.text(sprache),
            )
        },
    )

private fun GezogeneKarte.toGameCardUiModel(
    sprache: Sprache,
    cardInstanceId: Long,
): GameCardUiModel =
    GameCardUiModel(
        instanceId = cardInstanceId,
        kartentexte = kartentexte.map { gezogenerKartentext ->
            gezogenerKartentext.toGameKartentextUiModel(sprache)
        },
    )

private fun GezogenerKartentext.toGameKartentextUiModel(
    sprache: Sprache,
): GameKartentextUiModel =
    GameKartentextUiModel(
        id = kartentext.id(),
        text = kartentext.text(sprache),
        kategorieId = kategorieId,
        gespielt = kartentext.gespielt,
    )

internal fun GameUiState.withPlayedCardText(cardTextId: Int): GameUiState =
    withCardTextPlayedState(
        cardTextId = cardTextId,
        gespielt = true,
    )

internal fun GameUiState.withCardTextPlayedState(
    cardTextId: Int,
    gespielt: Boolean,
): GameUiState =
    copy(
        aktuelleKarte =
            aktuelleKarte.copy(
                kartentexte =
                    aktuelleKarte.kartentexte.map { kartentext ->
                        if (kartentext.id == cardTextId) {
                            kartentext.copy(gespielt = gespielt)
                        } else {
                            kartentext
                        }
                    },
            ),
    )
