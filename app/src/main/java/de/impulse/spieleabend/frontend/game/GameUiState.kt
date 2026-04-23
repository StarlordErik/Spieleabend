package de.impulse.spieleabend.frontend.game

import androidx.compose.runtime.Immutable
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Lokalisierung
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
    val kartentexte: List<GameKartentextUiModel>,
)

@Immutable
data class GameKartentextUiModel(
    val id: String,
    val text: String,
    val kategorieId: String,
)

@Immutable
data class GameKategorieUiModel(
    val id: String,
    val name: String,
)

internal fun Spiel.toGameUiState(
    aktuelleKarte: GezogeneKarte,
    sprache: Sprache,
    fallbackSprache: Sprache = FALLBACK_SPRACHE,
): GameUiState =
    GameUiState(
        spielName = lokalisierung.textOderId(sprache, fallbackSprache),
        aktuelleKarte = aktuelleKarte.toGameCardUiModel(sprache, fallbackSprache),
        kategorien = kategorien.map { kategorie ->
            GameKategorieUiModel(
                id = kategorie.id,
                name = kategorie.lokalisierung.textOderId(sprache, fallbackSprache),
            )
        },
    )

private val FALLBACK_SPRACHE = Sprache.DE

private fun GezogeneKarte.toGameCardUiModel(
    sprache: Sprache,
    fallbackSprache: Sprache,
): GameCardUiModel =
    GameCardUiModel(
        kartentexte = kartentexte.map { gezogenerKartentext ->
            gezogenerKartentext.toGameKartentextUiModel(sprache, fallbackSprache)
        },
    )

private fun GezogenerKartentext.toGameKartentextUiModel(
    sprache: Sprache,
    fallbackSprache: Sprache,
): GameKartentextUiModel =
    GameKartentextUiModel(
        id = kartentext.id,
        text = kartentext.lokalisierung.textOderId(sprache, fallbackSprache),
        kategorieId = kategorieId,
    )

private fun Lokalisierung.textOderId(
    sprache: Sprache,
    fallbackSprache: Sprache,
): String =
    textFuer(fallbackSprache)
        ?: textFuer(sprache)
        ?: translationen.firstOrNull()?.text
        ?: id
