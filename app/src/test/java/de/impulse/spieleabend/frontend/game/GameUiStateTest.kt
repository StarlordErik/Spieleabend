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
        Lokalisierung(
            id = id,
            translationen = setOf(Translation(sprachCode = "de", text = text)),
        )
}
