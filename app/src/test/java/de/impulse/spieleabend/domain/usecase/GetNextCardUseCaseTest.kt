package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNextCardUseCaseTest {
    @Test
    fun ziehtKarteAusBestimmterKategorie() {
        val frage = kartentext(id = 101)
        val hinweis = kartentext(id = 102)
        val spiel = spiel(
            texteProKarte = 2,
            kategorie(id = 1, frage, hinweis),
            kategorie(id = 2, kartentext(id = 201)),
        )

        val gezogeneKarte = GetNextCardFromCategoryUseCase()(
            spiel = spiel,
            kategorieId = 1,
        )

        assertEquals(
            setOf(101, 102),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                gezogenerKartentext.kategorieId == 1
            },
        )
    }

    @Test
    fun ziehtAusKategorieNurUngeseheneUndUngespielteKartentexte() {
        val spiel = spiel(
            texteProKarte = 2,
            kategorie(
                id = 1,
                kartentext(id = 101, gesehen = false),
                kartentext(id = 102, gesehen = true),
                kartentext(id = 103, gesehen = false, gespielt = true),
                kartentext(id = 104, gesehen = false),
            ),
        )

        val gezogeneKarte = GetNextCardFromCategoryUseCase()(
            spiel = spiel,
            kategorieId = 1,
        )

        assertEquals(
            setOf(101, 104),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
    }

    @Test
    fun ziehtAusKategorieMitWenigerAlsTexteProKarteWennNochUngeseheneUngespielteVorhandenSind() {
        val spiel = spiel(
            texteProKarte = 2,
            kategorie(
                id = 1,
                kartentext(id = 101, gesehen = false),
                kartentext(id = 102, gesehen = true),
            ),
        )

        val gezogeneKarte = GetNextCardFromCategoryUseCase()(
            spiel = spiel,
            kategorieId = 1,
        )

        assertEquals(
            1,
            gezogeneKarte.kartentexte.size,
        )
        assertEquals(
            setOf(101),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
    }

    @Test
    fun ziehtAusKategorieNachSeenResetNurUngespielteKartentexte() {
        val spiel = spiel(
            texteProKarte = 2,
            kategorie(
                id = 1,
                kartentext(id = 101, gesehen = true, gespielt = false),
                kartentext(id = 102, gesehen = false, gespielt = true),
            ),
        )

        val gezogeneKarte = GetNextCardFromCategoryUseCase()(
            spiel = spiel,
            kategorieId = 1,
        )

        assertEquals(
            setOf(101),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gespielt
            },
        )
        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gesehen
            },
        )
    }

    @Test
    fun ziehtAusKategorieNachPlayedResetMitUngelesenenUndUngespieltenKartentexten() {
        val spiel = spiel(
            texteProKarte = 2,
            kategorie(
                id = 1,
                kartentext(id = 101, gesehen = true, gespielt = true),
                kartentext(id = 102, gesehen = false, gespielt = true),
            ),
        )

        val gezogeneKarte = GetNextCardFromCategoryUseCase()(
            spiel = spiel,
            kategorieId = 1,
        )

        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gespielt
            },
        )
        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gesehen
            },
        )
    }

    @Test
    fun ziehtZufaelligeKarteAusAllenKategorien() {
        val spiel = spiel(
            texteProKarte = 1,
            kategorie(id = 1, kartentext(id = 101)),
            kategorie(id = 2, kartentext(id = 201)),
        )

        val gezogeneKarte = GetNextRandomCardUseCase()(spiel)

        assertEquals(
            1,
            gezogeneKarte.kartentexte.size,
        )
        assertTrue(
            gezogeneKarte.kartentexte.single().kartentext.id() in setOf(101, 201),
        )
        assertTrue(
            gezogeneKarte.kartentexte.single().kategorieId in setOf(1, 2),
        )
    }

    @Test
    fun ziehtZufaelligNurUngeseheneUndUngespielteKartentexte() {
        val spiel = spiel(
            texteProKarte = 2,
            kategorie(
                id = 1,
                kartentext(id = 101, gesehen = false),
                kartentext(id = 102, gesehen = true),
                kartentext(id = 103, gesehen = false, gespielt = true),
            ),
            kategorie(
                id = 2,
                kartentext(id = 201, gesehen = false),
            ),
        )

        val gezogeneKarte = GetNextRandomCardUseCase()(spiel)

        assertEquals(
            setOf(101, 201),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
    }

    @Test
    fun ziehtZufaelligMitWenigerAlsTexteProKarteWennNochUngeseheneUngespielteVorhandenSind() {
        val spiel = spiel(
            texteProKarte = 2,
            kategorie(
                id = 1,
                kartentext(id = 101, gesehen = true, gespielt = false),
            ),
            kategorie(
                id = 2,
                kartentext(id = 201, gesehen = false, gespielt = false),
            ),
        )

        val gezogeneKarte = GetNextRandomCardUseCase()(spiel)

        assertEquals(1, gezogeneKarte.kartentexte.size)
        assertEquals(
            setOf(201),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
    }

    @Test
    fun ziehtZufaelligNachSeenResetNurUngespielteKartentexte() {
        val spiel = spiel(
            texteProKarte = 2,
            kategorie(
                id = 1,
                kartentext(id = 101, gesehen = true, gespielt = false),
            ),
            kategorie(
                id = 2,
                kartentext(id = 201, gesehen = false, gespielt = true),
            ),
        )

        val gezogeneKarte = GetNextRandomCardUseCase()(spiel)

        assertEquals(
            setOf(101),
            gezogeneKarte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gespielt
            },
        )
        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gesehen
            },
        )
    }

    @Test
    fun ziehtZufaelligNachPlayedResetMitUngelesenenUndUngespieltenKartentexten() {
        val spiel = spiel(
            texteProKarte = 2,
            kategorie(
                id = 1,
                kartentext(id = 101, gesehen = true, gespielt = true),
            ),
            kategorie(
                id = 2,
                kartentext(id = 201, gesehen = false, gespielt = true),
            ),
        )

        val gezogeneKarte = GetNextRandomCardUseCase()(spiel)

        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gespielt
            },
        )
        assertTrue(
            gezogeneKarte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gesehen
            },
        )
    }

    @Test
    fun ziehtNieBereitsGespielteKartentexteSolangeUngespielteVerfuegbarSind() {
        val spiel = spiel(
            texteProKarte = 1,
            kategorie(
                id = 1,
                kartentext(id = 101, gesehen = true, gespielt = false),
                kartentext(id = 102, gesehen = false, gespielt = true),
            ),
        )

        val gezogeneKarte = GetNextCardFromCategoryUseCase()(
            spiel = spiel,
            kategorieId = 1,
        )

        assertEquals(101, gezogeneKarte.kartentexte.single().kartentext.id())
        assertFalse(gezogeneKarte.kartentexte.single().kartentext.gespielt)
    }

    private fun spiel(
        texteProKarte: Int,
        vararg kategorien: Kategorie,
    ): Spiel =
        Spiel(
            lokalisierung = lokalisierung(id = 10),
            originaleKategorien = kategorien.toCollection(LinkedHashSet()),
            texteProKarte = texteProKarte,
        )

    private fun kategorie(
        id: Int,
        vararg kartentexte: Kartentext,
    ): Kategorie =
        Kategorie(
            lokalisierung = lokalisierung(id = id),
            originaleKartentexte = kartentexte.toCollection(LinkedHashSet()),
        )

    private fun kartentext(
        id: Int,
        gesehen: Boolean = false,
        gespielt: Boolean = false,
    ): Kartentext =
        Kartentext(
            lokalisierung = lokalisierung(id = id),
            gesehen = gesehen,
            gespielt = gespielt,
        )

    private fun lokalisierung(id: Int): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = setOf(Translation(sprache = Sprache.OG, text = "lokalisierung-$id")),
            ogSprache = Sprache.DE,
        )
}
