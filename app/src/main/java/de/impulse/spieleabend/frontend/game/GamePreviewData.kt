package de.impulse.spieleabend.frontend.game

private val PreviewKartentexte = listOf(
    GameKartentextUiModel(
        id = "frage",
        text = "Welche Stadt tr\u00e4gt den Spitznamen Big Apple?",
    ),
    GameKartentextUiModel(
        id = "hinweis",
        text = "Tipp: Sie liegt an der Ostk\u00fcste.",
    ),
    GameKartentextUiModel(
        id = "joker",
        text = "Ein Team darf eine Zusatzfrage stellen.",
    ),
)

internal val PreviewUiState = GameUiState(
    spielName = "Kneipenquiz",
    kartentexte = PreviewKartentexte,
    kategorien = listOf(
        GameKategorieUiModel(
            id = "wissen",
            name = "Wissen",
            kartentexte = PreviewKartentexte.take(2),
        ),
        GameKategorieUiModel(
            id = "musik",
            name = "Musik",
            kartentexte = PreviewKartentexte.drop(2),
        ),
        GameKategorieUiModel(
            id = "film",
            name = "Film",
            kartentexte = PreviewKartentexte.take(1),
        ),
        GameKategorieUiModel(
            id = "finale",
            name = "Finale",
            kartentexte = PreviewKartentexte,
        ),
    ),
)
