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
            kartentexteProKarte = 2,
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
            kartentexteProKarte = 1,
            kategorie(id = "wissen", kartentext(id = "frage")),
            kategorie(id = "musik", kartentext(id = "lied")),
        )

        val gezogeneKarte = GetNextRandomCardUseCase()(spiel)

        assertEquals(
            1,
            gezogeneKarte.kartentexte.size,
        )
        assertTrue(
            gezogeneKarte.kartentexte.single().kartentext.id in setOf("frage", "lied"),
        )
        assertTrue(
            gezogeneKarte.kartentexte.single().kategorieId in setOf("wissen", "musik"),
        )
    }

    private fun spiel(
        kartentexteProKarte: Int,
        vararg kategorien: Kategorie,
    ): Spiel =
        Spiel(
            id = "spiel",
            lokalisierung = lokalisierung(id = "spiel-name"),
            kategorien = kategorien.toCollection(LinkedHashSet()),
            kartentexteProKarte = kartentexteProKarte,
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
            translationen = setOf(Translation(sprache = "de", text = id)),
        )
}
