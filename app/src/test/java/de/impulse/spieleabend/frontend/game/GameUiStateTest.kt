package de.impulse.spieleabend.frontend.game

import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation
import org.junit.Assert.assertEquals
import org.junit.Test

class GameUiStateTest {
    @Test
    fun lokalisiertGezogeneKarteUndKategorien() {
        val frage = kartentext(id = 101, text = "Frage")
        val hinweis = kartentext(id = 102, text = "Hinweis")
        val spiel = Spiel(
            lokalisierung = lokalisierung(id = 1, text = "Quiz"),
            originaleKategorien = linkedSetOf(
                kategorie(id = 11, name = "Wissen", frage, hinweis),
                kategorie(id = 12, name = "Finale", frage),
            ),
        )

        val uiState = spiel.toGameUiState(
            aktuelleKarte = GezogeneKarte(
                kartentexte = listOf(
                    GezogenerKartentext(kartentext = frage, kategorieId = 11),
                    GezogenerKartentext(kartentext = hinweis, kategorieId = 11),
                ),
            ),
            sprache = Sprache.DE,
            cardInstanceId = 7,
        )

        assertEquals("Quiz", uiState.spielName)
        assertEquals(7L, uiState.aktuelleKarte.instanceId)
        assertEquals(listOf("Wissen", "Finale"), uiState.kategorien.map { it.name })
        assertEquals(listOf(101, 102), uiState.aktuelleKarte.kartentexte.map { it.id })
        assertEquals(listOf("Frage", "Hinweis"), uiState.aktuelleKarte.kartentexte.map { it.text })
        assertEquals(listOf(false, false), uiState.aktuelleKarte.kartentexte.map { it.gespielt })
        assertEquals(
            listOf(11, 11),
            uiState.aktuelleKarte.kartentexte.map { it.kategorieId },
        )
    }

    @Test
    fun nutztAngeforderteSpracheUndFaelltFuerOriginalspracheAufOgZurueck() {
        val frage = Kartentext(
            lokalisierung = lokalisierung(
                id = 101,
                ogText = "English text",
                ogSprache = Sprache.EN,
                Translation(sprache = Sprache.DE, text = "Deutscher Text"),
            ),
        )
        val hinweis = Kartentext(
            lokalisierung = lokalisierung(
                id = 102,
                ogText = "English only",
                ogSprache = Sprache.EN,
            ),
        )
        val spiel = Spiel(
            lokalisierung = lokalisierung(
                id = 2,
                ogText = "Game",
                ogSprache = Sprache.EN,
                Translation(sprache = Sprache.DE, text = "Spiel"),
            ),
            originaleKategorien = linkedSetOf(
                Kategorie(
                    lokalisierung = lokalisierung(
                        id = 21,
                        ogText = "Category",
                        ogSprache = Sprache.EN,
                        Translation(sprache = Sprache.DE, text = "Kategorie"),
                    ),
                    originaleKartentexte = linkedSetOf(frage, hinweis),
                ),
            ),
        )

        val uiState = spiel.toGameUiState(
            aktuelleKarte = GezogeneKarte(
                kartentexte = listOf(
                    GezogenerKartentext(kartentext = frage, kategorieId = 21),
                    GezogenerKartentext(kartentext = hinweis, kategorieId = 21),
                ),
            ),
            sprache = Sprache.EN,
            cardInstanceId = 9,
        )

        assertEquals("Game", uiState.spielName)
        assertEquals(listOf("Category"), uiState.kategorien.map { kategorie -> kategorie.name })
        assertEquals(
            listOf("English text", "English only"),
            uiState.aktuelleKarte.kartentexte.map { kartentext -> kartentext.text },
        )
    }

    @Test
    fun markiertKartentextImUiStateAlsGespielt() {
        val frage = kartentext(id = 101, text = "Frage")
        val hinweis = kartentext(id = 102, text = "Hinweis", gespielt = true)
        val spiel = Spiel(
            lokalisierung = lokalisierung(id = 1, text = "Quiz"),
            originaleKategorien = linkedSetOf(kategorie(id = 11, name = "Wissen", frage, hinweis)),
        )

        val uiState =
            spiel.toGameUiState(
                aktuelleKarte = GezogeneKarte(
                    kartentexte = listOf(
                        GezogenerKartentext(kartentext = frage, kategorieId = 11),
                        GezogenerKartentext(kartentext = hinweis, kategorieId = 11),
                    ),
                ),
                sprache = Sprache.DE,
                cardInstanceId = 3,
            ).withPlayedCardText(101)

        assertEquals(listOf(true, true), uiState.aktuelleKarte.kartentexte.map { kartentext -> kartentext.gespielt })
        assertEquals(3L, uiState.aktuelleKarte.instanceId)
    }

    @Test
    fun hebtGespieltMarkierungImUiStateWiederAuf() {
        val frage = kartentext(id = 101, text = "Frage", gespielt = true)
        val spiel = Spiel(
            lokalisierung = lokalisierung(id = 1, text = "Quiz"),
            originaleKategorien = linkedSetOf(kategorie(id = 11, name = "Wissen", frage)),
        )

        val uiState =
            spiel.toGameUiState(
                aktuelleKarte = GezogeneKarte(
                    kartentexte = listOf(GezogenerKartentext(kartentext = frage, kategorieId = 11)),
                ),
                sprache = Sprache.DE,
                cardInstanceId = 4,
            ).withCardTextPlayedState(
                cardTextId = 101,
                gespielt = false,
            )

        assertEquals(listOf(false), uiState.aktuelleKarte.kartentexte.map { kartentext -> kartentext.gespielt })
        assertEquals(4L, uiState.aktuelleKarte.instanceId)
    }

    private fun kategorie(
        id: Int,
        name: String,
        vararg kartentexte: Kartentext,
    ): Kategorie =
        Kategorie(
            lokalisierung = lokalisierung(id = id, text = name),
            originaleKartentexte = kartentexte.toCollection(LinkedHashSet()),
        )

    private fun kartentext(
        id: Int,
        text: String,
        gespielt: Boolean = false,
    ): Kartentext =
        Kartentext(
            lokalisierung = lokalisierung(id = id, text = text),
            gespielt = gespielt,
        )

    private fun lokalisierung(
        id: Int,
        text: String,
    ): Lokalisierung =
        lokalisierung(
            id = id,
            ogText = text,
            ogSprache = Sprache.DE,
        )

    private fun lokalisierung(
        id: Int,
        ogText: String = "lokalisierung-$id",
        ogSprache: Sprache = Sprache.DE,
        vararg translationen: Translation,
    ): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen =
                listOf(Translation(sprache = Sprache.OG, text = ogText))
                    .plus(translationen)
                    .toCollection(LinkedHashSet()),
            ogSprache = ogSprache,
        )
}
