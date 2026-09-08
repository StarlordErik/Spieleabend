package de.kaserik.impulse.frontend.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.kaserik.impulse.R

private val PreviewKartentexte: List<GameKartentextUiModel>
    @Composable get() = listOf(
        GameKartentextUiModel(
            id = 101,
            text = stringResource(R.string.preview_question_city),
            kategorieId = 11,
            gespielt = false,
        ),
        GameKartentextUiModel(
            id = 102,
            text = stringResource(R.string.preview_question_city_hint),
            kategorieId = 11,
            gespielt = false,
        ),
        GameKartentextUiModel(
            id = 201,
            text = stringResource(R.string.preview_question_team),
            kategorieId = 12,
            gespielt = false,
        ),
    )

internal val PreviewUiState: GameUiState
    @Composable get() = GameUiState(
        spielId = 1,
        spielName = stringResource(R.string.preview_quiz),
        aktuelleKarte = GameCardUiModel(instanceId = 0, kartentexte = PreviewKartentexte),
        kategorien = listOf(
            GameKategorieUiModel(
                id = 11,
                name = stringResource(R.string.preview_category_knowledge),
            ),
            GameKategorieUiModel(
                id = 12,
                name = stringResource(R.string.preview_category_music),
            ),
            GameKategorieUiModel(
                id = 13,
                name = stringResource(R.string.preview_category_film),
            ),
            GameKategorieUiModel(
                id = 14,
                name = stringResource(R.string.preview_category_finale),
            ),
        ),
        texteProKarte = 3,
        standardTexteProKarte = 2,
        hasPreviousCard = true,
    )
