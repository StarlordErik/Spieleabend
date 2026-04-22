package de.impulse.spieleabend.frontend.game

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
        val frage = kartentext(id = "frage", text = "Frage")
        val hinweis = kartentext(id = "hinweis", text = "Hinweis")
        val spiel = Spiel(
            id = "quiz",
            lokalisierung = lokalisierung(id = "quiz-name", text = "Quiz"),
            kategorien = linkedSetOf(
                kategorie(id = "wissen", name = "Wissen", frage, hinweis),
                kategorie(id = "finale", name = "Finale", frage),
            ),
        )

        val uiState = spiel.toGameUiState(
            aktuelleKarte = GezogeneKarte(
                kartentexte = listOf(
                    GezogenerKartentext(kartentext = frage, kategorieId = "wissen"),
                    GezogenerKartentext(kartentext = hinweis, kategorieId = "wissen"),
                ),
            ),
            sprachCode = "de",
        )

        assertEquals("Quiz", uiState.spielName)
        assertEquals(listOf("Wissen", "Finale"), uiState.kategorien.map { it.name })
        assertEquals(listOf("frage", "hinweis"), uiState.aktuelleKarte.kartentexte.map { it.id })
        assertEquals(listOf("Frage", "Hinweis"), uiState.aktuelleKarte.kartentexte.map { it.text })
        assertEquals(
            listOf("wissen", "wissen"),
            uiState.aktuelleKarte.kartentexte.map { it.kategorieId },
        )
    }

    @Test
    fun bevorzugtDeutscheAnzeigeUndNutztSonstVorhandeneTranslation() {
        val frage = Kartentext(
            id = "frage",
            lokalisierung = lokalisierung(
                id = "frage-text",
                Translation(sprachCode = "en", text = "English text"),
                Translation(sprachCode = "de", text = "Deutscher Text"),
            ),
        )
        val hinweis = Kartentext(
            id = "hinweis",
            lokalisierung = lokalisierung(
                id = "hinweis-text",
                Translation(sprachCode = "en", text = "English only"),
            ),
        )
        val spiel = Spiel(
            id = "spiel",
            lokalisierung = lokalisierung(
                id = "spiel-name",
                Translation(sprachCode = "en", text = "Game"),
                Translation(sprachCode = "de", text = "Spiel"),
            ),
            kategorien = linkedSetOf(
                Kategorie(
                    id = "kategorie",
                    lokalisierung = lokalisierung(
                        id = "kategorie-name",
                        Translation(sprachCode = "en", text = "Category"),
                        Translation(sprachCode = "de", text = "Kategorie"),
                    ),
                    kartentexte = linkedSetOf(frage, hinweis),
                ),
            ),
        )

        val uiState = spiel.toGameUiState(
            aktuelleKarte = GezogeneKarte(
                kartentexte = listOf(
                    GezogenerKartentext(kartentext = frage, kategorieId = "kategorie"),
                    GezogenerKartentext(kartentext = hinweis, kategorieId = "kategorie"),
                ),
            ),
            sprachCode = "en",
        )

        assertEquals("Spiel", uiState.spielName)
        assertEquals(listOf("Kategorie"), uiState.kategorien.map { kategorie -> kategorie.name })
        assertEquals(
            listOf("Deutscher Text", "English only"),
            uiState.aktuelleKarte.kartentexte.map { kartentext -> kartentext.text },
        )
    }

    private fun kategorie(
        id: String,
        name: String,
        vararg kartentexte: Kartentext,
    ): Kategorie =
        Kategorie(
            id = id,
            lokalisierung = lokalisierung(id = "$id-name", text = name),
            kartentexte = kartentexte.toCollection(LinkedHashSet()),
        )

    private fun kartentext(
        id: String,
        text: String,
    ): Kartentext =
        Kartentext(
            id = id,
            lokalisierung = lokalisierung(id = "$id-text", text = text),
        )

    private fun lokalisierung(
        id: String,
        text: String,
    ): Lokalisierung =
        lokalisierung(
            id = id,
            Translation(sprachCode = "de", text = text),
        )

    private fun lokalisierung(
        id: String,
        vararg translationen: Translation,
    ): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = translationen.toCollection(LinkedHashSet()),
        )
}
