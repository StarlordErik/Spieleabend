package de.impulse.spieleabend.data

import de.impulse.spieleabend.common.Sprache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SpracheRoomConverterTest {
    private val converter = SpracheRoomConverter()

    @Test
    fun speichertKanonischeSprache() {
        assertEquals("de", converter.fromSprache(Sprache.DE))
        assertEquals("og", converter.fromSprache(Sprache.OG))
    }

    @Test
    fun liestKanonischeSpracheAusDatenbank() {
        assertEquals(Sprache.EN, converter.toSprache("en"))
    }

    @Test
    fun lehntUnbekannteSpracheAb() {
        assertThrows(IllegalArgumentException::class.java) {
            converter.toSprache("fr")
        }
    }
}
