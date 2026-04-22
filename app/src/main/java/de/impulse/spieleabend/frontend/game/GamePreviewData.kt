package de.impulse.spieleabend.frontend.game

private val PreviewKartentexte = listOf(
    GameKartentextUiModel(
        id = "frage",
        text = "Welche Stadt tr\u00e4gt den Spitznamen Big Apple?",
        kategorieId = "wissen",
    ),
    GameKartentextUiModel(
        id = "hinweis",
        text = "Tipp: Sie liegt an der Ostk\u00fcste.",
        kategorieId = "wissen",
    ),
    GameKartentextUiModel(
        id = "joker",
        text = "Ein Team darf eine Zusatzfrage stellen.",
        kategorieId = "musik",
    ),
)

internal val PreviewUiState = GameUiState(
    spielName = "Kneipenquiz",
    aktuelleKarte = GameCardUiModel(kartentexte = PreviewKartentexte),
    kategorien = listOf(
        GameKategorieUiModel(
            id = "wissen",
            name = "Wissen",
        ),
        GameKategorieUiModel(
            id = "musik",
            name = "Musik",
        ),
        GameKategorieUiModel(
            id = "film",
            name = "Film",
        ),
        GameKategorieUiModel(
            id = "finale",
            name = "Finale",
        ),
    ),
)
