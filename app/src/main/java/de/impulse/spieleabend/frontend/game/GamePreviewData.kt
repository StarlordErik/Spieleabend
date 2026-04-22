package de.impulse.spieleabend.frontend.game

internal val PreviewUiState = GameUiState(
    spielName = "Kneipenquiz",
    kartentexte = listOf(
        GameKartentextUiModel(
            id = "frage",
            text = "Welche Stadt trägt den Spitznamen Big Apple?",
        ),
        GameKartentextUiModel(
            id = "hinweis",
            text = "Tipp: Sie liegt an der Ostküste.",
        ),
        GameKartentextUiModel(
            id = "joker",
            text = "Ein Team darf eine Zusatzfrage stellen.",
        ),
    ),
    kategorien = listOf(
        GameKategorieUiModel(id = "wissen", name = "Wissen"),
        GameKategorieUiModel(id = "musik", name = "Musik"),
        GameKategorieUiModel(id = "film", name = "Film"),
        GameKategorieUiModel(id = "finale", name = "Finale"),
    ),
)
