package de.impulse.spieleabend.frontend.game

import androidx.compose.runtime.Immutable
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel

@Immutable
data class GameUiState(
    val spielName: String,
    val kartentexte: List<GameKartentextUiModel>,
    val kategorien: List<GameKategorieUiModel>,
)

@Immutable
data class GameKartentextUiModel(
    val id: String,
    val text: String,
)

@Immutable
data class GameKategorieUiModel(
    val id: String,
    val name: String,
)

internal fun Spiel.toGameUiState(
    sprachCode: String,
    fallbackSprachCode: String = FALLBACK_SPRACH_CODE,
): GameUiState =
    GameUiState(
        spielName = lokalisierung.textOderId(sprachCode, fallbackSprachCode),
        kartentexte = transitiveKartentexte().map { kartentext ->
            GameKartentextUiModel(
                id = kartentext.id,
                text = kartentext.lokalisierung.textOderId(sprachCode, fallbackSprachCode),
            )
        },
        kategorien = kategorien.map { kategorie ->
            GameKategorieUiModel(
                id = kategorie.id,
                name = kategorie.lokalisierung.textOderId(sprachCode, fallbackSprachCode),
            )
        },
    )

private const val FALLBACK_SPRACH_CODE = "de"

private fun Spiel.transitiveKartentexte(): List<Kartentext> =
    kategorien
        .flatMap { kategorie -> kategorie.kartentexte }
        .distinctBy { kartentext -> kartentext.id }

private fun Lokalisierung.textOderId(
    sprachCode: String,
    fallbackSprachCode: String,
): String =
    textFuer(sprachCode = sprachCode, fallbackSprachCode = fallbackSprachCode) ?: id
