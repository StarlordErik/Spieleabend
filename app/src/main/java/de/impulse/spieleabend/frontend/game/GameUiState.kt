package de.impulse.spieleabend.frontend.game

import androidx.compose.runtime.Immutable
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.BearbeiteteKartentexteModus
import de.impulse.spieleabend.domain.model.FavoritenModus
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.GeloeschteKartentexteModus
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
    val spielId: Int,
    val spielName: String,
    val aktuelleKarte: GameCardUiModel,
    val kategorien: List<GameKategorieUiModel>,
    val texteProKarte: Int,
    val standardTexteProKarte: Int,
    val hasPreviousCard: Boolean,
    val funFactsModeEnabled: Boolean = true,
    val sprache: Sprache = Sprache.DE,
    val geloeschteKartentexteModus: GeloeschteKartentexteModus = GeloeschteKartentexteModus.ALS_LETZTE,
    val favoritenModus: FavoritenModus = FavoritenModus.UNBEACHTET,
    val bearbeiteteKartentexteModus: BearbeiteteKartentexteModus = BearbeiteteKartentexteModus.UNBEACHTET,
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
    val geloescht: Boolean = false,
    val favorit: Boolean = false,
    val eigeneLokalisierung: Boolean = false,
    val eigeneLokalisierungFuerAktuelleSprache: Boolean = false,
    val uebersetzungFehlt: Boolean = false,
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
    hasPreviousCard: Boolean = false,
): GameUiState =
    GameUiState(
        spielId = id(),
        spielName = text(sprache),
        aktuelleKarte = aktuelleKarte.toGameCardUiModel(sprache, cardInstanceId),
        kategorien = kategorien.map { kategorie ->
            GameKategorieUiModel(
                id = kategorie.id(),
                name = kategorie.text(sprache),
            )
        },
        texteProKarte = texteProKarte,
        standardTexteProKarte = standardTexteProKarte,
        hasPreviousCard = hasPreviousCard,
        sprache = sprache,
        geloeschteKartentexteModus = geloeschteKartentexteModus,
        favoritenModus = favoritenModus,
        bearbeiteteKartentexteModus = bearbeiteteKartentexteModus,
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
): GameKartentextUiModel {
    val lokalisierterText = kartentext.lokalisierung.lokalisierterText(sprache)
    return GameKartentextUiModel(
        id = kartentext.id(),
        text = lokalisierterText.text,
        kategorieId = kategorieId,
        gespielt = kartentext.gespielt,
        geloescht = kartentext.inaktiv,
        favorit = kartentext.favorit,
        eigeneLokalisierung = kartentext.lokalisierung.hatEigeneLokalisierung(),
        eigeneLokalisierungFuerAktuelleSprache =
            kartentext.lokalisierung.hatEigeneLokalisierungFuer(sprache),
        uebersetzungFehlt = lokalisierterText.uebersetzungFehlt,
    )
}

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

internal fun GameUiState.withAllCardTextsUnplayed(): GameUiState =
    copy(
        aktuelleKarte = aktuelleKarte.copy(
            kartentexte = aktuelleKarte.kartentexte.map { it.copy(gespielt = false) },
        ),
    )

internal fun GameUiState.withCardTextDeletedState(
    cardTextId: Int,
    deleted: Boolean,
): GameUiState =
    updateCardText(cardTextId) { cardText -> cardText.copy(geloescht = deleted) }

internal fun GameUiState.withCardTextFavoriteState(
    cardTextId: Int,
    favorite: Boolean,
): GameUiState =
    updateCardText(cardTextId) { cardText -> cardText.copy(favorit = favorite) }

private fun GameUiState.updateCardText(
    cardTextId: Int,
    transform: (GameKartentextUiModel) -> GameKartentextUiModel,
): GameUiState =
    copy(
        aktuelleKarte = aktuelleKarte.copy(
            kartentexte = aktuelleKarte.kartentexte.map { cardText ->
                if (cardText.id == cardTextId) transform(cardText) else cardText
            },
        ),
    )
