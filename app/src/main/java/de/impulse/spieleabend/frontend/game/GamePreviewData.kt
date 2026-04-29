package de.impulse.spieleabend.frontend.game

private val PreviewKartentexte = listOf(
    GameKartentextUiModel(
        id = 101,
        text = "Welche Stadt tr\u00e4gt den Spitznamen Big Apple?",
        kategorieId = 11,
        gespielt = false,
    ),
    GameKartentextUiModel(
        id = 102,
        text = "Tipp: Sie liegt an der Ostk\u00fcste.",
        kategorieId = 11,
        gespielt = false,
    ),
    GameKartentextUiModel(
        id = 201,
        text = "Ein Team darf eine Zusatzfrage stellen.",
        kategorieId = 12,
        gespielt = false,
    ),
)

internal val PreviewUiState = GameUiState(
    spielName = "Kneipenquiz",
    aktuelleKarte = GameCardUiModel(instanceId = 0, kartentexte = PreviewKartentexte),
    kategorien = listOf(
        GameKategorieUiModel(
            id = 11,
            name = "Wissen",
        ),
        GameKategorieUiModel(
            id = 12,
            name = "Musik",
        ),
        GameKategorieUiModel(
            id = 13,
            name = "Film",
        ),
        GameKategorieUiModel(
            id = 14,
            name = "Finale",
        ),
    ),
)
