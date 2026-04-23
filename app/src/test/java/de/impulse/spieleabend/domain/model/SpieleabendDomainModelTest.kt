package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SpieleabendDomainModelTest {
    @Test
    fun spielVerwaltetKategorienAlsVieleZuVieleBeziehung() {
        val teamKategorie = kategorie(1)
        val wissenKategorie = kategorie(2)

        val spiel = spiel(10, teamKategorie)
            .mitKategorie(wissenKategorie)
        val weiteresSpiel = spiel(11, teamKategorie)

        assertTrue(spiel.enthaeltKategorie(1))
        assertTrue(spiel.enthaeltKategorie(2))
        assertTrue(weiteresSpiel.enthaeltKategorie(1))
        assertEquals(setOf(teamKategorie, wissenKategorie), spiel.kategorien)
    }

    @Test
    fun kategorieVerwaltetKartentexteAlsVieleZuVieleBeziehung() {
        val pantomimeText = kartentext(101)
        val quizText = kartentext(102)

        val aktivKategorie = kategorie(20)
            .mitKartentext(pantomimeText)
            .mitKartentext(quizText)
        val partyKategorie = kategorie(21).mitKartentext(pantomimeText)

        assertTrue(aktivKategorie.enthaeltKartentext(101))
        assertTrue(aktivKategorie.enthaeltKartentext(102))
        assertTrue(partyKategorie.enthaeltKartentext(101))
        assertEquals(setOf(pantomimeText, quizText), aktivKategorie.kartentexte)
    }

    @Test
    fun lokalisierungLiefertTextFuerSpracheMitFallback() {
        val lokalisierung = lokalisierung(
            id = 1,
            translations = setOf(
                Translation(sprache = Sprache.DE, text = "Spieleabend"),
                Translation(sprache = Sprache.EN, text = "Game Night"),
            ),
        )

        assertEquals("Spieleabend", lokalisierung.textFuer(Sprache.DE))
        assertEquals("Game Night", lokalisierung.textFuer(Sprache.OG, fallbackSprache = Sprache.EN))
        assertNull(lokalisierung.textFuer(Sprache.OG))
    }

    @Test
    fun lokalisierungErlaubtProSpracheNurEineTranslation() {
        assertThrows(IllegalArgumentException::class.java) {
            lokalisierung(
                id = 2,
                translations = setOf(
                    Translation(sprache = Sprache.DE, text = "Erster Text"),
                    Translation(sprache = Sprache.DE, text = "Zweiter Text"),
                ),
            )
        }
    }

    @Test
    fun spielErsetztKategorieMitGleicherId() {
        val alteKategorie = kategorie(30)
        val neueKategorie = kategorie(30).mitKartentext(kartentext(301))

        val spiel = spiel(12, alteKategorie)
            .mitKategorie(neueKategorie)

        assertEquals(setOf(neueKategorie), spiel.kategorien)
        assertFalse(alteKategorie in spiel.kategorien)
    }

    @Test
    fun spielBenoetigtMindestensEineKategorie() {
        assertThrows(IllegalArgumentException::class.java) {
            Spiel(
                id = 13,
                lokalisierung = lokalisierung(3),
                kategorien = emptySet(),
            )
        }
    }

    private fun spiel(
        id: Int,
        vararg kategorien: Kategorie,
    ): Spiel =
        Spiel(
            id = id,
            lokalisierung = lokalisierung(id * 10),
            kategorien = kategorien.toSet(),
        )

    private fun kategorie(id: Int): Kategorie =
        Kategorie(
            id = id,
            lokalisierung = lokalisierung(id * 10),
        )

    private fun kartentext(id: Int): Kartentext =
        Kartentext(
            id = id,
            lokalisierung = lokalisierung(id * 10),
        )

    private fun lokalisierung(
        id: Int,
        translations: Set<Translation> = setOf(Translation(sprache = Sprache.DE, text = "lokalisierung-$id")),
    ): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = translations,
            ogSprache = translations.first().sprache,
        )
}
