package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.BearbeiteteKartentexteModus
import de.impulse.spieleabend.domain.model.FavoritenModus
import de.impulse.spieleabend.domain.model.GeloeschteKartentexteModus
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardTextSelectionModesTest {
    @Test
    fun geloeschteWerdenAusgeblendet() {
        val card = next(
            game(
                deletedMode = GeloeschteKartentexteModus.AUSBLENDEN,
                texts = arrayOf(cardText(1), cardText(2, deleted = true)),
            ),
        )

        assertEquals(setOf(1), card.kartentexte.map { it.kartentext.id() }.toSet())
    }

    @Test
    fun geloeschteKommenImStandardmodusErstNachAllenAnderen() {
        val normalFirst = next(
            game(
                deletedMode = GeloeschteKartentexteModus.ALS_LETZTE,
                texts = arrayOf(cardText(1), cardText(2, deleted = true)),
            ),
        )
        val deletedLast = next(
            game(
                deletedMode = GeloeschteKartentexteModus.ALS_LETZTE,
                texts = arrayOf(cardText(1, seen = true), cardText(2, deleted = true)),
            ),
        )

        assertEquals(1, normalFirst.kartentexte.single().kartentext.id())
        assertEquals(2, deletedLast.kartentexte.single().kartentext.id())
    }

    @Test
    fun geloeschteWerdenNachEigenemRefreshVollstaendigGezeigtBevorNormaleNeuStarten() {
        val firstPlan = planNextCardFromCategory(
            spiel = game(
                deletedMode = GeloeschteKartentexteModus.ALS_LETZTE,
                texts = arrayOf(
                    cardText(1, seen = true),
                    cardText(2, deleted = true, seen = true),
                    cardText(3, deleted = true, seen = true),
                ),
            ),
            kategorieId = CATEGORY_ID,
        )
        val firstDeletedId = firstPlan.karte.kartentexte.single().kartentext.id()
        val remainingDeletedId = setOf(2, 3).single { id -> id != firstDeletedId }

        assertEquals(setOf(2, 3), firstPlan.resetSeenKartentextIds)
        assertFalse(1 in firstPlan.resetSeenKartentextIds)

        val secondPlan = planNextCardFromCategory(
            spiel = game(
                deletedMode = GeloeschteKartentexteModus.ALS_LETZTE,
                texts = arrayOf(
                    cardText(1, seen = true),
                    cardText(firstDeletedId, deleted = true, seen = true),
                    cardText(remainingDeletedId, deleted = true),
                ),
            ),
            kategorieId = CATEGORY_ID,
        )

        assertEquals(remainingDeletedId, secondPlan.karte.kartentexte.single().kartentext.id())
        assertTrue(1 in secondPlan.resetSeenKartentextIds)
    }

    @Test
    fun geloeschteBleibenFaelligWennAllePoolsGespieltSind() {
        val plan = planNextCardFromCategory(
            spiel = game(
                deletedMode = GeloeschteKartentexteModus.ALS_LETZTE,
                texts = arrayOf(
                    cardText(1, seen = true, played = true),
                    cardText(2, deleted = true, seen = true, played = true),
                ),
            ),
            kategorieId = CATEGORY_ID,
        )

        assertEquals(2, plan.karte.kartentexte.single().kartentext.id())
        assertEquals(setOf(1, 2), plan.resetSeenUndGespieltKartentextIds)
    }

    @Test
    fun geloeschteWerdenUnbeachtetWieNormaleBehandelt() {
        val card = next(
            game(
                deletedMode = GeloeschteKartentexteModus.UNBEACHTET,
                texts = arrayOf(cardText(1, seen = true), cardText(2, deleted = true)),
            ),
        )

        assertEquals(2, card.kartentexte.single().kartentext.id())
    }

    @Test
    fun ausschliesslichGeloeschteWerdenOhneAuffuellenGezogen() {
        val plan = planNextCardFromCategory(
            spiel = game(
                textsPerCard = 3,
                deletedMode = GeloeschteKartentexteModus.AUSSCHLIESSLICH,
                texts = arrayOf(cardText(1), cardText(2, deleted = true, seen = true)),
            ),
            kategorieId = CATEGORY_ID,
        )

        assertEquals(listOf(2), plan.karte.kartentexte.map { it.kartentext.id() })
        assertEquals(setOf(2), plan.resetSeenKartentextIds)
    }

    @Test
    fun genauEinFavoritWirdUnabhaengigAufgefrischt() {
        val plan = planNextCardFromCategory(
            spiel = game(
                textsPerCard = 3,
                favoritesMode = FavoritenModus.GENAU_EINER_PRO_KARTE,
                texts = arrayOf(
                    cardText(1, favorite = true, seen = true),
                    cardText(2, favorite = true, seen = true),
                    cardText(3),
                    cardText(4),
                ),
            ),
            kategorieId = CATEGORY_ID,
        )

        assertEquals(1, plan.karte.kartentexte.count { it.kartentext.favorit })
        assertEquals(setOf(1, 2), plan.resetSeenKartentextIds.intersect(setOf(1, 2)))
    }

    @Test
    fun gespielteFavoritenWerdenUnabhaengigVonNormalenTextenAufgefrischt() {
        val plan = planNextCardFromCategory(
            spiel = game(
                textsPerCard = 2,
                favoritesMode = FavoritenModus.GENAU_EINER_PRO_KARTE,
                texts = arrayOf(
                    cardText(1, favorite = true, seen = true, played = true),
                    cardText(2, favorite = true, seen = true, played = true),
                    cardText(3),
                    cardText(4),
                ),
            ),
            kategorieId = CATEGORY_ID,
        )

        assertEquals(1, plan.karte.kartentexte.count { it.kartentext.favorit })
        assertEquals(setOf(1, 2), plan.resetSeenUndGespieltKartentextIds)
        assertTrue(plan.resetSeenKartentextIds.intersect(setOf(3, 4)).isEmpty())
    }

    @Test
    fun genauEinFavoritRespektiertDasAusblendenGeloeschterTexte() {
        val card = next(
            game(
                deletedMode = GeloeschteKartentexteModus.AUSBLENDEN,
                favoritesMode = FavoritenModus.GENAU_EINER_PRO_KARTE,
                texts = arrayOf(
                    cardText(1),
                    cardText(2, deleted = true, favorite = true),
                ),
            ),
        )

        assertEquals(listOf(1), card.kartentexte.map { it.kartentext.id() })
        assertFalse(card.kartentexte.single().kartentext.favorit)
    }

    @Test
    fun alsLetzteGeloeschteHabenVorrangVorGenauEinemFavoriten() {
        val normalFirst = next(
            game(
                deletedMode = GeloeschteKartentexteModus.ALS_LETZTE,
                favoritesMode = FavoritenModus.GENAU_EINER_PRO_KARTE,
                texts = arrayOf(
                    cardText(1),
                    cardText(2, deleted = true, favorite = true),
                ),
            ),
        )
        val favoriteLast = next(
            game(
                deletedMode = GeloeschteKartentexteModus.ALS_LETZTE,
                favoritesMode = FavoritenModus.GENAU_EINER_PRO_KARTE,
                texts = arrayOf(
                    cardText(1, seen = true),
                    cardText(2, deleted = true, favorite = true),
                ),
            ),
        )

        assertEquals(1, normalFirst.kartentexte.single().kartentext.id())
        assertEquals(2, favoriteLast.kartentexte.single().kartentext.id())
    }

    @Test
    fun exklusiveFavoritenWerdenNichtMitNormalenAufgefuellt() {
        val card = next(
            game(
                textsPerCard = 4,
                favoritesMode = FavoritenModus.AUSSCHLIESSLICH,
                texts = arrayOf(cardText(1), cardText(2, favorite = true)),
            ),
        )

        assertEquals(listOf(2), card.kartentexte.map { it.kartentext.id() })
    }

    @Test
    fun exklusivBearbeitetNutztNurTexteMitEigenerLokalisierung() {
        val card = next(
            game(
                textsPerCard = 3,
                editedMode = BearbeiteteKartentexteModus.AUSSCHLIESSLICH,
                texts = arrayOf(cardText(1), cardText(2, custom = true)),
            ),
        )

        assertEquals(listOf(2), card.kartentexte.map { it.kartentext.id() })
        assertTrue(card.kartentexte.single().kartentext.lokalisierung.hatEigeneLokalisierung())
    }

    @Test
    fun exklusivmodusOhneMarkierungErzeugtLeereKarte() {
        val card = next(
            game(
                favoritesMode = FavoritenModus.AUSSCHLIESSLICH,
                texts = arrayOf(cardText(1), cardText(2)),
            ),
        )

        assertTrue(card.kartentexte.isEmpty())
    }

    @Test
    fun harteFilterWerdenMiteinanderKombiniert() {
        val card = next(
            game(
                textsPerCard = 3,
                deletedMode = GeloeschteKartentexteModus.AUSSCHLIESSLICH,
                favoritesMode = FavoritenModus.AUSSCHLIESSLICH,
                texts = arrayOf(
                    cardText(1, deleted = true),
                    cardText(2, favorite = true),
                    cardText(3, deleted = true, favorite = true),
                ),
            ),
        )

        assertEquals(listOf(3), card.kartentexte.map { it.kartentext.id() })
        assertTrue(card.kartentexte.single().kartentext.inaktiv)
        assertTrue(card.kartentexte.single().kartentext.favorit)
        assertFalse(card.kartentexte.single().kartentext.gesehen)
    }

    @Test
    fun zufaelligTabBeachtetLetzteGeloeschteImGesamtenSpiel() {
        val normalFirst = planNextRandomCard(
            multiCategoryGame(
                firstCategoryTexts = arrayOf(cardText(1)),
                secondCategoryTexts = arrayOf(cardText(2, deleted = true)),
            ),
        ).karte
        val deletedAfterNormalWasSeen = planNextRandomCard(
            multiCategoryGame(
                firstCategoryTexts = arrayOf(cardText(1, seen = true)),
                secondCategoryTexts = arrayOf(cardText(2, deleted = true)),
            ),
        ).karte

        assertEquals(1, normalFirst.kartentexte.single().kartentext.id())
        assertEquals(2, deletedAfterNormalWasSeen.kartentexte.single().kartentext.id())
    }

    @Test
    fun exklusivBearbeiteteRefreshenNurIhrenEigenenPool() {
        val plan = planNextCardFromCategory(
            spiel = game(
                editedMode = BearbeiteteKartentexteModus.AUSSCHLIESSLICH,
                texts = arrayOf(
                    cardText(1, seen = true, played = true),
                    cardText(2, custom = true, seen = true, played = true),
                ),
            ),
            kategorieId = CATEGORY_ID,
        )

        assertEquals(2, plan.karte.kartentexte.single().kartentext.id())
        assertEquals(setOf(2), plan.resetSeenUndGespieltKartentextIds)
    }

    private fun next(game: Spiel) = GetNextCardFromCategoryUseCase()(game, CATEGORY_ID)

    private fun game(
        textsPerCard: Int = 1,
        deletedMode: GeloeschteKartentexteModus = GeloeschteKartentexteModus.ALS_LETZTE,
        favoritesMode: FavoritenModus = FavoritenModus.UNBEACHTET,
        editedMode: BearbeiteteKartentexteModus = BearbeiteteKartentexteModus.UNBEACHTET,
        texts: Array<out Kartentext>,
    ): Spiel =
        Spiel(
            lokalisierung = localization(GAME_ID),
            originaleKategorien = linkedSetOf(
                Kategorie(
                    lokalisierung = localization(CATEGORY_ID),
                    originaleKartentexte = texts.toCollection(LinkedHashSet()),
                ),
            ),
            texteProKarte = textsPerCard,
            geloeschteKartentexteModus = deletedMode,
            favoritenModus = favoritesMode,
            bearbeiteteKartentexteModus = editedMode,
        )

    private fun multiCategoryGame(
        firstCategoryTexts: Array<out Kartentext>,
        secondCategoryTexts: Array<out Kartentext>,
    ): Spiel =
        Spiel(
            lokalisierung = localization(GAME_ID),
            originaleKategorien = linkedSetOf(
                Kategorie(
                    lokalisierung = localization(CATEGORY_ID),
                    originaleKartentexte = firstCategoryTexts.toCollection(LinkedHashSet()),
                ),
                Kategorie(
                    lokalisierung = localization(SECOND_CATEGORY_ID),
                    originaleKartentexte = secondCategoryTexts.toCollection(LinkedHashSet()),
                ),
            ),
            texteProKarte = 1,
            geloeschteKartentexteModus = GeloeschteKartentexteModus.ALS_LETZTE,
        )

    private fun cardText(
        id: Int,
        deleted: Boolean = false,
        favorite: Boolean = false,
        custom: Boolean = false,
        seen: Boolean = false,
        played: Boolean = false,
    ): Kartentext =
        Kartentext(
            lokalisierung = localization(id, custom),
            inaktiv = deleted,
            favorit = favorite,
            gesehen = seen,
            gespielt = played,
        )

    private fun localization(
        id: Int,
        custom: Boolean = false,
    ): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = buildSet {
                add(Translation(Sprache.OG, "Text $id"))
                if (custom) add(Translation(Sprache.EIGENE_DE, "Eigener Text $id", bearbeitet = true))
            },
            ogSprache = Sprache.DE,
        )

    private companion object {
        const val GAME_ID = 10
        const val CATEGORY_ID = 20
        const val SECOND_CATEGORY_ID = 21
    }
}
