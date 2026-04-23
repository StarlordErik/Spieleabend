package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SpieleabendDomainModelTest {
    @Test
    fun spielEnthältKategorienAlsVieleZuVieleBeziehung() {
        val teamKategorie = kategorie(1)
        val wissenKategorie = kategorie(2)

        val spiel = spiel(10, teamKategorie, wissenKategorie)
        val weiteresSpiel = spiel(11, teamKategorie)

        assertEquals(setOf(teamKategorie, wissenKategorie), spiel.kategorien)
        assertEquals(setOf(teamKategorie), weiteresSpiel.kategorien)
    }

    @Test
    fun spielLeitetAktiveKategorienAusJoinZuständenAb() {
        val originaleKategorie = kategorie(1)
        val hinzugefuegteKategorie = kategorie(2)

        val spiel = Spiel(
            lokalisierung = lokalisierung(10),
            originaleKategorien = setOf(originaleKategorie),
            hinzugefuegteKategorien = setOf(hinzugefuegteKategorie),
            inaktiveKategorien = setOf(hinzugefuegteKategorie),
        )

        assertEquals(setOf(originaleKategorie), spiel.kategorien)
    }

    @Test
    fun kategorieEnthältKartentexteAlsVieleZuVieleBeziehung() {
        val pantomimeText = kartentext(101)
        val quizText = kartentext(102)

        val aktivKategorie = kategorie(20, pantomimeText, quizText)
        val partyKategorie = kategorie(21, pantomimeText)

        assertEquals(setOf(pantomimeText, quizText), aktivKategorie.kartentexte)
        assertEquals(setOf(pantomimeText), partyKategorie.kartentexte)
    }

    @Test
    fun kategorieLeitetAktiveKartentexteAusJoinZuständenAb() {
        val originalerKartentext = kartentext(101)
        val hinzugefuegterKartentext = kartentext(102)

        val kategorie = Kategorie(
            lokalisierung = lokalisierung(20),
            originaleKartentexte = setOf(originalerKartentext),
            hinzugefuegteKartentexte = setOf(hinzugefuegterKartentext),
            inaktiveKartentexte = setOf(hinzugefuegterKartentext),
        )

        assertEquals(setOf(originalerKartentext), kategorie.kartentexte)
    }

    @Test
    fun lokalisierungLiefertTextUndFälltSonstAufOgZurück() {
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
    fun lokalisierungFälltBeiFehlenderTranslationAufOgZurück() {
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
                lokalisierung = lokalisierung(12),
                originaleKategorien = linkedSetOf(alteKategorie),
                hinzugefuegteKategorien = linkedSetOf(neueKategorie),
            )
        }
    }

    @Test
    fun kategorieErlaubtKeineDoppelteKartentextId() {
        val alterKartentext = Kartentext(lokalisierung = lokalisierung(301, ogText = "Alt"))
        val neuerKartentext =
            Kartentext(
                lokalisierung = lokalisierung(301, ogText = "Neu"),
                gesehen = true,
            )

        assertThrows(IllegalArgumentException::class.java) {
            Kategorie(
                lokalisierung = lokalisierung(30),
                originaleKartentexte = linkedSetOf(alterKartentext),
                hinzugefuegteKartentexte = linkedSetOf(neuerKartentext),
            )
        }
    }

    @Test
    fun spielErlaubtLeereKategorien() {
        val spiel = Spiel(
            lokalisierung = lokalisierung(13),
            originaleKategorien = emptySet(),
        )

        assertEquals(emptySet<Kategorie>(), spiel.kategorien)
    }

    @Test
    fun lokalisierungBenötigtOgTranslation() {
        assertThrows(IllegalArgumentException::class.java) {
            Lokalisierung(
                id = 4,
                translationen = setOf(Translation(sprache = Sprache.DE, text = "Spieleabend")),
                ogSprache = Sprache.DE,
            )
        }
    }

    @Test
    fun gezogeneKarteBenötigtMindestensEinenKartentext() {
        assertThrows(IllegalArgumentException::class.java) {
            GezogeneKarte(kartentexte = emptyList())
        }
    }

    private fun spiel(
        id: Int,
        vararg kategorien: Kategorie,
    ): Spiel =
        Spiel(
            lokalisierung = lokalisierung(id),
            originaleKategorien = kategorien.toSet(),
        )

    private fun kategorie(
        id: Int,
        vararg kartentexte: Kartentext,
    ): Kategorie =
        Kategorie(
            lokalisierung = lokalisierung(id),
            originaleKartentexte = kartentexte.toSet(),
        )

    private fun kartentext(id: Int): Kartentext =
        Kartentext(
            lokalisierung = lokalisierung(id),
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
