package de.kaserik.impulse.domain.model

import de.kaserik.impulse.common.Sprache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalizationFallbackTest {
    @Test
    fun erikBleibtInternUndIstKeineAuswaehlbareSprache() {
        assertEquals(listOf(Sprache.DE, Sprache.EN), Sprache.AuswaehlbareSprachen)
        assertFalse(Sprache.ERIK.auswaehlbar)
        assertEquals(Sprache.EIGENE_DE, Sprache.ERIK.eigeneSprache())
    }

    @Test
    fun deutschVerwendetErikErstNachUebernahmeAlsEigeneLokalisierung() {
        val original = localization(
            ogLanguage = Sprache.DE,
            Sprache.DE to "Deutsch",
            Sprache.ERIK to "Erik",
        )
        assertEquals("Deutsch", original.text(Sprache.DE))
        assertFalse(original.lokalisierterText(Sprache.DE).eigeneLokalisierung)

        val edited = original.copy(
            translationen = original.translationen + Translation(Sprache.EIGENE_DE, "Erik", bearbeitet = true),
        )
        assertEquals("Erik", edited.text(Sprache.DE))
        assertTrue(edited.lokalisierterText(Sprache.DE).eigeneLokalisierung)
        assertTrue(edited.hatEigeneLokalisierungFuer(Sprache.DE))
    }

    @Test
    fun eigeneDeLokalisierungUeberschreibtAuchErik() {
        val localization = localization(
            ogLanguage = Sprache.DE,
            Sprache.ERIK to "Erik",
            Sprache.DE to "Deutsch",
            Sprache.EIGENE_DE to "Mein Text",
        )

        val result = localization.lokalisierterText(Sprache.ERIK)

        assertEquals("Mein Text", result.text)
        assertTrue(result.eigeneLokalisierung)
        assertFalse(result.uebersetzungFehlt)
    }

    @Test
    fun erikFaelltOhneWarnungAufDeZurueck() {
        val result = localization(
            ogLanguage = Sprache.EN,
            Sprache.DE to "Deutsch",
        ).lokalisierterText(Sprache.ERIK)

        assertEquals("Deutsch", result.text)
        assertFalse(result.uebersetzungFehlt)
    }

    @Test
    fun direkteErikLokalisierungHatVorrangVorDe() {
        val result = localization(
            ogLanguage = Sprache.EN,
            Sprache.DE to "Deutsch",
            Sprache.ERIK to "Erik",
        ).lokalisierterText(Sprache.ERIK)

        assertEquals("Erik", result.text)
        assertFalse(result.uebersetzungFehlt)
    }

    @Test
    fun eigeneEnLokalisierungHatVorrangVorDirekterEnLokalisierung() {
        val result = localization(
            ogLanguage = Sprache.DE,
            Sprache.EN to "English",
            Sprache.EIGENE_EN to "My text",
        ).lokalisierterText(Sprache.EN)

        assertEquals("My text", result.text)
        assertTrue(result.eigeneLokalisierung)
        assertFalse(result.uebersetzungFehlt)
    }

    @Test
    fun originalInPassenderSpracheBrauchtKeineWarnung() {
        val localization = localization(ogLanguage = Sprache.DE)

        assertFalse(localization.lokalisierterText(Sprache.DE).uebersetzungFehlt)
        assertFalse(localization.lokalisierterText(Sprache.ERIK).uebersetzungFehlt)
    }

    @Test
    fun fremdsprachigesOriginalZeigtFehlendeUebersetzung() {
        val result = localization(ogLanguage = Sprache.DE).lokalisierterText(Sprache.EN)

        assertEquals("Original", result.text)
        assertTrue(result.uebersetzungFehlt)
    }

    @Test
    fun eigeneLokalisierungEinerAnderenSpracheWirdNichtAlsUebersetzungVerwendet() {
        val localization = localization(
            ogLanguage = Sprache.DE,
            Sprache.EIGENE_DE to "Mein Text",
        )

        val result = localization.lokalisierterText(Sprache.EN)

        assertEquals("Original", result.text)
        assertTrue(result.uebersetzungFehlt)
        assertTrue(localization.hatEigeneLokalisierung())
        assertFalse(localization.hatEigeneLokalisierungFuer(Sprache.EN))
    }

    private fun localization(
        ogLanguage: Sprache,
        vararg translations: Pair<Sprache, String>,
    ): Lokalisierung =
        Lokalisierung(
            id = 1,
            translationen = buildSet {
                add(Translation(Sprache.OG, "Original"))
                translations.forEach { (language, text) -> add(Translation(language, text)) }
            },
            ogSprache = ogLanguage,
        )
}
