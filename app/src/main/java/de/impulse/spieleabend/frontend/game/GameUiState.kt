package de.impulse.spieleabend.frontend.game

import androidx.compose.runtime.Immutable
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel

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
    sprachCode: String,
    fallbackSprachCode: String = FALLBACK_SPRACH_CODE,
): GameUiState =
    GameUiState(
        spielName = lokalisierung.textOderId(sprachCode, fallbackSprachCode),
        aktuelleKarte = aktuelleKarte.toGameCardUiModel(sprachCode, fallbackSprachCode),
        kategorien = kategorien.map { kategorie ->
            GameKategorieUiModel(
                id = kategorie.id,
                name = kategorie.lokalisierung.textOderId(sprachCode, fallbackSprachCode),
            )
        },
    )

private const val FALLBACK_SPRACH_CODE = "de"

private fun GezogeneKarte.toGameCardUiModel(
    sprachCode: String,
    fallbackSprachCode: String,
): GameCardUiModel =
    GameCardUiModel(
        kartentexte = kartentexte.map { gezogenerKartentext ->
            gezogenerKartentext.toGameKartentextUiModel(sprachCode, fallbackSprachCode)
        },
    )

private fun GezogenerKartentext.toGameKartentextUiModel(
    sprachCode: String,
    fallbackSprachCode: String,
): GameKartentextUiModel =
    GameKartentextUiModel(
        id = kartentext.id,
        text = kartentext.lokalisierung.textOderId(sprachCode, fallbackSprachCode),
        kategorieId = kategorieId,
    )

private fun Lokalisierung.textOderId(
    sprachCode: String,
    fallbackSprachCode: String,
): String =
    textFuer(sprachCode = sprachCode, fallbackSprachCode = fallbackSprachCode) ?: id
