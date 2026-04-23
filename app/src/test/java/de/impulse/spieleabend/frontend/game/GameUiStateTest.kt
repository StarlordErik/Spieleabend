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
            id = 1,
            lokalisierung = lokalisierung(id = 10, text = "Quiz"),
            kategorien = linkedSetOf(
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
        )

        assertEquals("Quiz", uiState.spielName)
        assertEquals(listOf("Wissen", "Finale"), uiState.kategorien.map { it.name })
        assertEquals(listOf(101, 102), uiState.aktuelleKarte.kartentexte.map { it.id })
        assertEquals(listOf("Frage", "Hinweis"), uiState.aktuelleKarte.kartentexte.map { it.text })
        assertEquals(
            listOf(11, 11),
            uiState.aktuelleKarte.kartentexte.map { it.kategorieId },
        )
    }

    @Test
    fun bevorzugtDeutscheAnzeigeUndNutztSonstVorhandeneTranslation() {
        val frage = Kartentext(
            id = 101,
            lokalisierung = lokalisierung(
                id = 1001,
                Translation(sprache = "en", text = "English text"),
                Translation(sprache = "de", text = "Deutscher Text"),
            ),
        )
        val hinweis = Kartentext(
            id = 102,
            lokalisierung = lokalisierung(
                id = 1002,
                Translation(sprache = "en", text = "English only"),
            ),
        )
        val spiel = Spiel(
            id = 2,
            lokalisierung = lokalisierung(
                id = 2001,
                Translation(sprache = "en", text = "Game"),
                Translation(sprache = "de", text = "Spiel"),
            ),
            kategorien = linkedSetOf(
                Kategorie(
                    id = 21,
                    lokalisierung = lokalisierung(
                        id = 2002,
                        Translation(sprache = "en", text = "Category"),
                        Translation(sprache = "de", text = "Kategorie"),
                    ),
                    kartentexte = linkedSetOf(frage, hinweis),
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
        )

        assertEquals("Spiel", uiState.spielName)
        assertEquals(listOf("Kategorie"), uiState.kategorien.map { kategorie -> kategorie.name })
        assertEquals(
            listOf("Deutscher Text", "English only"),
            uiState.aktuelleKarte.kartentexte.map { kartentext -> kartentext.text },
        )
    }

    private fun kategorie(
        id: Int,
        name: String,
        vararg kartentexte: Kartentext,
    ): Kategorie =
        Kategorie(
            id = id,
            lokalisierung = lokalisierung(id = id * 10, text = name),
            kartentexte = kartentexte.toCollection(LinkedHashSet()),
        )

    private fun kartentext(
        id: Int,
        text: String,
    ): Kartentext =
        Kartentext(
            id = id,
            lokalisierung = lokalisierung(id = id * 10, text = text),
        )

    private fun lokalisierung(
        id: Int,
        text: String,
    ): Lokalisierung =
        lokalisierung(
            id = id,
            Translation(sprache = "de", text = text),
        )

    private fun lokalisierung(
        id: Int,
        vararg translationen: Translation,
    ): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = translationen.toCollection(LinkedHashSet()),
        )
}
