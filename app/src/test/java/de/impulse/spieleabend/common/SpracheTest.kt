package de.impulse.spieleabend.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class SpracheTest {
    @Test
    fun erkenntUnterstuetzteSprachenUndLocaleVarianten() {
        assertEquals(Sprache.DE, Sprache.fromCode("DE"))
        assertEquals(Sprache.EN, Sprache.fromCode("en-US"))
        assertEquals(Sprache.DE, Sprache.fromLocale(Locale.GERMANY))
    }

    @Test
    fun liefertNullFuerUnbekannteSprachen() {
        assertNull(Sprache.fromCode("fr"))
    }
}
