package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNextCardUseCaseTest {
    @Test
    fun ziehtKarteAusBestimmterKategorie() {
        val frage = kartentext(id = "frage")
        val hinweis = kartentext(id = "hinweis")
        val spiel = spiel(
            kategorie(id = "wissen", frage, hinweis),
            kategorie(id = "musik", kartentext(id = "lied")),
        )

        val gezogeneKarte = GetNextCardFromCategoryUseCase()(
            spiel = spiel,
            kategorieId = "wissen",
        )

        assertEquals(
            setOf("frage", "hinweis"),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id
            }.toSet(),
        )
        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                gezogenerKartentext.kategorieId == "wissen"
            },
        )
    }

    @Test
    fun ziehtZufaelligeKarteAusAllenKategorien() {
        val spiel = spiel(
            kategorie(id = "wissen", kartentext(id = "frage")),
            kategorie(id = "musik", kartentext(id = "lied")),
        )

        val gezogeneKarte = GetNextRandomCardUseCase()(spiel)

        assertEquals(
            setOf("frage", "lied"),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id
            }.toSet(),
        )
        assertEquals(
            setOf("wissen", "musik"),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kategorieId
            }.toSet(),
        )
    }

    private fun spiel(vararg kategorien: Kategorie): Spiel =
        Spiel(
            id = "spiel",
            lokalisierung = lokalisierung(id = "spiel-name"),
            kategorien = kategorien.toCollection(LinkedHashSet()),
        )

    private fun kategorie(
        id: String,
        vararg kartentexte: Kartentext,
    ): Kategorie =
        Kategorie(
            id = id,
            lokalisierung = lokalisierung(id = "$id-name"),
            kartentexte = kartentexte.toCollection(LinkedHashSet()),
        )

    private fun kartentext(id: String): Kartentext =
        Kartentext(
            id = id,
            lokalisierung = lokalisierung(id = "$id-text"),
        )

    private fun lokalisierung(id: String): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = setOf(Translation(sprachCode = "de", text = id)),
        )
}
