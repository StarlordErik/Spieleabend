package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SpieleabendDomainModelTest {
    @Test
    fun spielEnthaeltKategorienAlsVieleZuVieleBeziehung() {
        val teamKategorie = kategorie(1)
        val wissenKategorie = kategorie(2)

        val spiel = spiel(10, teamKategorie, wissenKategorie)
        val weiteresSpiel = spiel(11, teamKategorie)

        assertEquals(setOf(teamKategorie, wissenKategorie), spiel.kategorien)
        assertEquals(setOf(teamKategorie), weiteresSpiel.kategorien)
    }

    @Test
    fun kategorieEnthaeltKartentexteAlsVieleZuVieleBeziehung() {
        val pantomimeText = kartentext(101)
        val quizText = kartentext(102)

        val aktivKategorie = kategorie(20, pantomimeText, quizText)
        val partyKategorie = kategorie(21, pantomimeText)

        assertEquals(setOf(pantomimeText, quizText), aktivKategorie.kartentexte)
        assertEquals(setOf(pantomimeText), partyKategorie.kartentexte)
    }

    @Test
    fun lokalisierungLiefertTextUndFaelltSonstAufOgZurueck() {
        val lokalisierung = lokalisierung(
            id = 1,
            ogText = "Game Night",
            ogSprache = Sprache.EN,
            translations = setOf(
                Translation(sprache = Sprache.OG, text = "Game Night"),
                Translation(sprache = Sprache.DE, text = "Spieleabend"),
            ),
        )

        assertEquals("Spieleabend", lokalisierung.text(Sprache.DE))
        assertEquals("Game Night", lokalisierung.text(Sprache.EN))
        assertEquals("Game Night", lokalisierung.text(Sprache.OG))
    }

    @Test
    fun lokalisierungErlaubtProSpracheNurEineTranslation() {
        assertThrows(IllegalArgumentException::class.java) {
            lokalisierung(
                id = 2,
                translations = setOf(
                    Translation(sprache = Sprache.OG, text = "Original"),
                    Translation(sprache = Sprache.DE, text = "Erster Text"),
                    Translation(sprache = Sprache.DE, text = "Zweiter Text"),
                ),
            )
        }
    }

    @Test
    fun lokalisierungFaelltBeiFehlenderTranslationAufOgZurueck() {
        val lokalisierung = lokalisierung(
            id = 3,
            ogText = "Game Night",
            ogSprache = Sprache.EN,
            translations = setOf(Translation(sprache = Sprache.OG, text = "Game Night")),
        )

        assertEquals("Game Night", lokalisierung.text(Sprache.DE))
    }

    @Test
    fun spielErlaubtKeineDoppelteKategorieId() {
        val alteKategorie = kategorie(30)
        val neueKategorie = kategorie(30, kartentext(301))

        assertThrows(IllegalArgumentException::class.java) {
            Spiel(
                id = 12,
                lokalisierung = lokalisierung(120),
                kategorien = linkedSetOf(alteKategorie, neueKategorie),
            )
        }
    }

    @Test
    fun kategorieErlaubtKeineDoppelteKartentextId() {
        val alterKartentext = Kartentext(id = 301, lokalisierung = lokalisierung(3010))
        val neuerKartentext = Kartentext(id = 301, lokalisierung = lokalisierung(3011))

        assertThrows(IllegalArgumentException::class.java) {
            Kategorie(
                id = 30,
                lokalisierung = lokalisierung(300),
                kartentexte = linkedSetOf(alterKartentext, neuerKartentext),
            )
        }
    }

    @Test
    fun spielErlaubtLeereKategorien() {
        val spiel = Spiel(
            id = 13,
            lokalisierung = lokalisierung(3),
            kategorien = emptySet(),
        )

        assertEquals(emptySet<Kategorie>(), spiel.kategorien)
    }

    @Test
    fun lokalisierungBenoetigtOgTranslation() {
        assertThrows(IllegalArgumentException::class.java) {
            Lokalisierung(
                id = 4,
                translationen = setOf(Translation(sprache = Sprache.DE, text = "Spieleabend")),
                ogSprache = Sprache.DE,
            )
        }
    }

    @Test
    fun gezogeneKarteBenoetigtMindestensEinenKartentext() {
        assertThrows(IllegalArgumentException::class.java) {
            GezogeneKarte(kartentexte = emptyList())
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

    private fun kategorie(
        id: Int,
        vararg kartentexte: Kartentext,
    ): Kategorie =
        Kategorie(
            id = id,
            lokalisierung = lokalisierung(id * 10),
            kartentexte = kartentexte.toSet(),
        )

    private fun kartentext(id: Int): Kartentext =
        Kartentext(
            id = id,
            lokalisierung = lokalisierung(id * 10),
        )

    private fun lokalisierung(
        id: Int,
        ogText: String = "lokalisierung-$id",
        ogSprache: Sprache = Sprache.DE,
        translations: Set<Translation> = setOf(Translation(sprache = Sprache.OG, text = ogText)),
    ): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = translations,
            ogSprache = ogSprache,
        )
}
