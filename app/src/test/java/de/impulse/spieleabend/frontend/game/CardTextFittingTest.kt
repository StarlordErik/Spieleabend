package de.impulse.spieleabend.frontend.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardTextFittingTest {
    @Test
    fun schuetztLangeWoerterAlsEinSegment() {
        val text = "Donaudampfschifffahrtsgesellschaft und kurz"

        assertEquals("Donaudampfschifffahrtsgesellschaft", unbrokenCardTextSegments(text).first())
    }

    @Test
    fun bindetNichtBuchstabenAmZeilenanfangAnErstesWort() {
        val protected = protectCardTextLineStarts("... erstes Wort\n- nächstes Wort")
        val visibleProtected = protected.replace("\u2060", "")

        assertTrue(visibleProtected.startsWith("...\u00A0erstes"))
        assertTrue(visibleProtected.contains("\n-\u00A0nächstes"))
        assertFalse(unbrokenCardTextSegments(protected).contains("..."))
    }

    @Test
    fun erkenntUmlauteUndEszettAlsBuchstaben() {
        val protected = protectCardTextLineStarts("Äpfel\n... Überraschung\n- großartig")
            .replace("\u2060", "")

        assertTrue(protected.contains("...\u00A0Überraschung"))
        assertTrue(protected.contains("-\u00A0großartig"))
    }

    @Test
    fun schuetztDasVollstaendigeErsteWortMitSatzzeichen() {
        val protected = protectCardTextLineStarts("... E-Mail folgt\n\"Hallo!\" danach")
            .replace("\u2060", "")

        assertTrue(protected.startsWith("...\u00A0E-Mail folgt"))
        assertTrue(protected.contains("\n\"Hallo!\" danach"))
        assertEquals("...\u00A0E-Mail", unbrokenCardTextSegments(protected).first())
        assertEquals("\"Hallo!\"", unbrokenCardTextSegments(protected)[2])
    }

    @Test
    fun trenntKeineSurrogatpaareAmZeilenanfang() {
        val protected = protectCardTextLineStarts("\uD83D\uDE0A Frage")

        assertEquals("\uD83D\uDE0A", protected.substring(0, 2))
        assertEquals("\uD83D\uDE0A Frage", protected.replace("\u2060", "").replace('\u00A0', ' '))
    }

    @Test
    fun trenntKeineZusammengesetztenEmojiAmZeilenanfang() {
        val emoji = listOf(
            "\u2764\uFE0F",
            "\uD83C\uDDE9\uD83C\uDDEA",
            "\uD83D\uDC69\u200D\uD83D\uDC69",
        )

        emoji.forEach { grapheme ->
            val protected = protectCardTextLineStarts("$grapheme Frage")

            assertEquals(grapheme, protected.substringBefore('\u2060'))
            assertEquals("$grapheme Frage", protected.replace("\u2060", "").replace('\u00A0', ' '))
        }
    }
}
