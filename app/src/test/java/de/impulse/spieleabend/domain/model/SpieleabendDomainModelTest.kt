package de.impulse.spieleabend.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SpieleabendDomainModelTest {
    @Test
    fun spielVerwaltetKategorienAlsVieleZuVieleBeziehung() {
        val teamKategorie = kategorie("team")
        val wissenKategorie = kategorie("wissen")

        val spiel = spiel("party")
            .mitKategorie(teamKategorie)
            .mitKategorie(wissenKategorie)
        val weiteresSpiel = spiel("quiz").mitKategorie(teamKategorie)

        assertTrue(spiel.enthaeltKategorie("team"))
        assertTrue(spiel.enthaeltKategorie("wissen"))
        assertTrue(weiteresSpiel.enthaeltKategorie("team"))
        assertEquals(setOf(teamKategorie, wissenKategorie), spiel.kategorien)
    }

    @Test
    fun kategorieVerwaltetKartentexteAlsVieleZuVieleBeziehung() {
        val pantomimeText = kartentext("pantomime")
        val quizText = kartentext("quiz")

        val aktivKategorie = kategorie("aktiv")
            .mitKartentext(pantomimeText)
            .mitKartentext(quizText)
        val partyKategorie = kategorie("party").mitKartentext(pantomimeText)

        assertTrue(aktivKategorie.enthaeltKartentext("pantomime"))
        assertTrue(aktivKategorie.enthaeltKartentext("quiz"))
        assertTrue(partyKategorie.enthaeltKartentext("pantomime"))
        assertEquals(setOf(pantomimeText, quizText), aktivKategorie.kartentexte)
    }

    @Test
    fun lokalisierungLiefertTextFuerSprachcodeMitFallback() {
        val lokalisierung = lokalisierung(
            id = "spiel-name",
            translations = setOf(
                Translation(sprachCode = "de", text = "Spieleabend"),
                Translation(sprachCode = "en", text = "Game Night"),
            ),
        )

        assertEquals("Spieleabend", lokalisierung.textFuer("DE"))
        assertEquals("Game Night", lokalisierung.textFuer("fr", fallbackSprachCode = "en"))
        assertNull(lokalisierung.textFuer("fr"))
    }

    @Test
    fun lokalisierungErlaubtProSprachcodeNurEineTranslation() {
        assertThrows(IllegalArgumentException::class.java) {
            lokalisierung(
                id = "doppelt",
                translations = setOf(
                    Translation(sprachCode = "de", text = "Erster Text"),
                    Translation(sprachCode = "DE", text = "Zweiter Text"),
                ),
            )
        }
    }

    @Test
    fun spielErsetztKategorieMitGleicherId() {
        val alteKategorie = kategorie("aktion")
        val neueKategorie = kategorie("aktion").mitKartentext(kartentext("rennen"))

        val spiel = spiel("party")
            .mitKategorie(alteKategorie)
            .mitKategorie(neueKategorie)

        assertEquals(setOf(neueKategorie), spiel.kategorien)
        assertFalse(alteKategorie in spiel.kategorien)
    }

    private fun spiel(id: String): Spiel =
        Spiel(
            id = id,
            lokalisierung = lokalisierung("$id-lokalisierung"),
        )

    private fun kategorie(id: String): Kategorie =
        Kategorie(
            id = id,
            lokalisierung = lokalisierung("$id-lokalisierung"),
        )

    private fun kartentext(id: String): Kartentext =
        Kartentext(
            id = id,
            lokalisierung = lokalisierung("$id-lokalisierung"),
        )

    private fun lokalisierung(
        id: String,
        translations: Set<Translation> = setOf(Translation(sprachCode = "de", text = id)),
    ): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = translations,
        )
}
