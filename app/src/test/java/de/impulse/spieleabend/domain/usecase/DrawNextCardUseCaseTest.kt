package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.CardHistoryState
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation
import de.impulse.spieleabend.domain.repository.GameRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrawNextCardUseCaseTest {
    @Test
    fun previousPopsCurrentCardsAndANewDrawCreatesANewBranch() = runBlocking {
        val repository = FakeGameRepository(spiel(kategorie(id = 1, kartentext(id = 101))))
        listOf(1, 2, 3, 4, 5).forEach { id -> repository.commit(card(id)) }

        assertEquals(4, repository.popCurrentCard(10)?.card?.singleId())
        assertEquals(3, repository.popCurrentCard(10)?.card?.singleId())

        repository.commit(card(6))
        repository.commit(card(7))

        assertEquals(7, repository.getCurrentCard(10)?.card?.singleId())
        assertEquals(
            listOf(6, 3, 2, 1),
            buildList {
                while (true) {
                    val previous = repository.popCurrentCard(10) ?: break
                    add(previous.card.singleId())
                }
            },
        )
    }

    @Test
    fun historyKeepsCurrentAndTenPreviousCards() = runBlocking {
        val repository = FakeGameRepository(spiel(kategorie(id = 1, kartentext(id = 101))))
        (1..12).forEach { id -> repository.commit(card(id)) }

        assertEquals(12, repository.getCurrentCard(10)?.card?.singleId())
        assertEquals(
            (11 downTo 2).toList(),
            buildList {
                while (true) {
                    val previous = repository.popCurrentCard(10) ?: break
                    add(previous.card.singleId())
                }
            },
        )
        assertFalse(repository.getCurrentCard(10)?.hasPrevious ?: true)
    }

    @Test
    fun drawFromCategoryZiehtNurUngeseheneUndUngespielteKartentexte() = runBlocking {
        val repository =
            FakeGameRepository(
                spiel(
                    kategorie(
                        id = 1,
                        kartentext(id = 101, gesehen = false),
                        kartentext(id = 102, gesehen = true),
                        kartentext(id = 103, gesehen = false, gespielt = true),
                        kartentext(id = 104, gesehen = false),
                    ),
                ),
            )

        val result =
            DrawNextCardFromCategoryUseCase(repository)(
                gameId = 10,
                kategorieId = 1,
            )

        assertEquals(emptySet<Int>(), repository.lastResetSeenCategoryIds)
        assertEquals(emptySet<Int>(), repository.lastResetSeenAndPlayedCategoryIds)
        assertEquals(setOf(101, 104), repository.lastSeenCardTextIds)
        assertEquals(
            setOf(101, 104),
            result.karte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
        val kartentexte =
            result.spiel.kategorien
                .single { kategorie -> kategorie.id() == 1 }
                .kartentexte
                .associateBy { kartentext -> kartentext.id() }
        assertTrue(kartentexte.getValue(101).gesehen)
        assertTrue(kartentexte.getValue(102).gesehen)
        assertFalse(kartentexte.getValue(103).gesehen)
        assertTrue(kartentexte.getValue(103).gespielt)
        assertTrue(kartentexte.getValue(104).gesehen)
    }

    @Test
    fun drawFromCategoryZiehtMitWenigerAlsTexteProKarteWennNochUngeseheneUngespielteVorhandenSind() = runBlocking {
        val repository =
            FakeGameRepository(
                spiel(
                    kategorie(
                        id = 1,
                        kartentext(id = 101, gesehen = false),
                        kartentext(id = 102, gesehen = true),
                    ),
                ),
            )

        val result =
            DrawNextCardFromCategoryUseCase(repository)(
                gameId = 10,
                kategorieId = 1,
            )

        assertEquals(emptySet<Int>(), repository.lastResetSeenCategoryIds)
        assertEquals(emptySet<Int>(), repository.lastResetSeenAndPlayedCategoryIds)
        assertEquals(setOf(101), repository.lastSeenCardTextIds)
        assertEquals(
            setOf(101),
            result.karte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
    }

    @Test
    fun drawFromCategoryResetetNurGesehenWennKeineUngesehenenUngespieltenKartentexteMehrVorhandenSind() = runBlocking {
        val repository =
            FakeGameRepository(
                spiel(
                    kategorie(
                        id = 1,
                        kartentext(id = 101, gesehen = true, gespielt = false),
                        kartentext(id = 102, gesehen = false, gespielt = true),
                    ),
                ),
            )

        val result =
            DrawNextCardFromCategoryUseCase(repository)(
                gameId = 10,
                kategorieId = 1,
            )

        assertEquals(setOf(1), repository.lastResetSeenCategoryIds)
        assertEquals(emptySet<Int>(), repository.lastResetSeenAndPlayedCategoryIds)
        assertEquals(setOf(101), repository.lastSeenCardTextIds)
        val kartentexte =
            result.spiel.kategorien
                .single { kategorie -> kategorie.id() == 1 }
                .kartentexte
                .associateBy { kartentext -> kartentext.id() }
        assertTrue(kartentexte.getValue(101).gesehen)
        assertFalse(kartentexte.getValue(101).gespielt)
        assertFalse(kartentexte.getValue(102).gesehen)
        assertTrue(kartentexte.getValue(102).gespielt)
    }

    @Test
    fun drawFromCategoryResetetGespieltUndGesehenWennAlleKartentexteGespieltSind() = runBlocking {
        val repository =
            FakeGameRepository(
                spiel(
                    kategorie(
                        id = 1,
                        kartentext(id = 101, gesehen = true, gespielt = true),
                        kartentext(id = 102, gesehen = false, gespielt = true),
                    ),
                ),
            )

        val result =
            DrawNextCardFromCategoryUseCase(repository)(
                gameId = 10,
                kategorieId = 1,
            )

        assertEquals(emptySet<Int>(), repository.lastResetSeenCategoryIds)
        assertEquals(setOf(1), repository.lastResetSeenAndPlayedCategoryIds)
        assertTrue(
            result.karte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gespielt
            },
        )
        assertTrue(
            result.karte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gesehen
            },
        )
        assertTrue(
            result.spiel.kategorien
                .single { kategorie -> kategorie.id() == 1 }
                .kartentexte
                .all { kartentext -> kartentext.gesehen && !kartentext.gespielt },
        )
    }

    @Test
    fun drawRandomZiehtMitWenigerAlsTexteProKarteWennNochUngeseheneUngespielteVorhandenSind() = runBlocking {
        val repository =
            FakeGameRepository(
                spiel(
                    kategorie(
                        id = 1,
                        kartentext(id = 101, gesehen = true, gespielt = false),
                    ),
                    kategorie(
                        id = 2,
                        kartentext(id = 201, gesehen = false, gespielt = false),
                    ),
                ),
            )

        val result = DrawNextRandomCardUseCase(repository)(gameId = 10)

        assertEquals(emptySet<Int>(), repository.lastResetSeenCategoryIds)
        assertEquals(emptySet<Int>(), repository.lastResetSeenAndPlayedCategoryIds)
        assertEquals(setOf(201), repository.lastSeenCardTextIds)
        assertEquals(
            setOf(201),
            result.karte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
    }

    @Test
    fun drawRandomResetetNurGesehenWennKeineUngesehenenUngespieltenKartentexteMehrVorhandenSind() = runBlocking {
        val repository =
            FakeGameRepository(
                spiel(
                    kategorie(
                        id = 1,
                        kartentext(id = 101, gesehen = true, gespielt = false),
                    ),
                    kategorie(
                        id = 2,
                        kartentext(id = 201, gesehen = false, gespielt = true),
                    ),
                ),
            )

        val result = DrawNextRandomCardUseCase(repository)(gameId = 10)

        assertEquals(setOf(1, 2), repository.lastResetSeenCategoryIds)
        assertEquals(emptySet<Int>(), repository.lastResetSeenAndPlayedCategoryIds)
        assertEquals(setOf(101), repository.lastSeenCardTextIds)
        val kartentexte =
            result.spiel.kategorien
                .flatMap { kategorie -> kategorie.kartentexte }
                .associateBy { kartentext -> kartentext.id() }
        assertTrue(kartentexte.getValue(101).gesehen)
        assertFalse(kartentexte.getValue(101).gespielt)
        assertFalse(kartentexte.getValue(201).gesehen)
        assertTrue(kartentexte.getValue(201).gespielt)
    }

    @Test
    fun drawRandomResetetGespieltUndGesehenWennAlleKartentexteGespieltSind() = runBlocking {
        val repository =
            FakeGameRepository(
                spiel(
                    kategorie(
                        id = 1,
                        kartentext(id = 101, gesehen = true, gespielt = true),
                    ),
                    kategorie(
                        id = 2,
                        kartentext(id = 201, gesehen = false, gespielt = true),
                    ),
                ),
            )

        val result = DrawNextRandomCardUseCase(repository)(gameId = 10)

        assertEquals(emptySet<Int>(), repository.lastResetSeenCategoryIds)
        assertEquals(setOf(1, 2), repository.lastResetSeenAndPlayedCategoryIds)
        assertTrue(
            result.karte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gespielt
            },
        )
        assertTrue(
            result.karte.kartentexte.all { gezogenerKartentext ->
                !gezogenerKartentext.kartentext.gesehen
            },
        )
        assertTrue(
            result.spiel.kategorien.flatMap { kategorie -> kategorie.kartentexte }
                .all { kartentext -> kartentext.gesehen && !kartentext.gespielt },
        )
    }

    @Test
    fun setCardTextPlayedStateSetztGespieltAufTrueUndFalse() = runBlocking {
        val repository =
            FakeGameRepository(
                spiel(
                    kategorie(
                        id = 1,
                        kartentext(id = 101, gesehen = true, gespielt = false),
                        kartentext(id = 102, gesehen = true, gespielt = false),
                    ),
                ),
            )

        SetCardTextPlayedStateUseCase(repository)(
            cardTextId = 101,
            gespielt = true,
        )

        assertEquals(setOf(101), repository.lastPlayedCardTextIds)
        val kartentexte =
            repository.getGame(10)
                .kategorien
                .single()
                .kartentexte
                .associateBy { kartentext -> kartentext.id() }
        assertTrue(kartentexte.getValue(101).gespielt)
        assertFalse(kartentexte.getValue(102).gespielt)

        SetCardTextPlayedStateUseCase(repository)(
            cardTextId = 101,
            gespielt = false,
        )

        assertEquals(setOf(101), repository.lastPlayedCardTextIds)
        assertFalse(
            repository.getGame(10)
                .kategorien
                .single()
                .kartentexte
                .single { kartentext -> kartentext.id() == 101 }
                .gespielt,
        )
    }

    private fun spiel(vararg kategorien: Kategorie): Spiel =
        Spiel(
            lokalisierung = lokalisierung(id = 10),
            originaleKategorien = kategorien.toCollection(LinkedHashSet()),
            texteProKarte = 2,
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

    private fun card(id: Int): GezogeneKarte =
        GezogeneKarte(
            kartentexte =
                listOf(
                    GezogenerKartentext(
                        kartentext = kartentext(id),
                        kategorieId = 1,
                    ),
                ),
        )

    private fun GezogeneKarte.singleId(): Int = kartentexte.single().kartentext.id()

    private inner class FakeGameRepository(
        initialSpiel: Spiel,
    ) : GameRepository {
        private var spiel: Spiel = initialSpiel
        private val history = mutableListOf<CardHistoryState>()
        private var nextInstanceId = 1L

        var lastResetSeenCategoryIds: Set<Int> = emptySet()
            private set

        var lastResetSeenAndPlayedCategoryIds: Set<Int> = emptySet()
            private set

        var lastSeenCardTextIds: Set<Int> = emptySet()
            private set

        var lastPlayedCardTextIds: Set<Int> = emptySet()
            private set

        override suspend fun getGames(): List<Spiel> = listOf(spiel)

        override suspend fun getGame(gameId: Int): Spiel = spiel

        override suspend fun commitCardDraw(
            gameId: Int,
            resetSeenCategoryIds: Set<Int>,
            resetSeenAndPlayedCategoryIds: Set<Int>,
            card: GezogeneKarte,
        ): CardHistoryState {
            val seenCardTextIds = card.kartentexte.map { it.kartentext.id() }.toSet()
            lastResetSeenCategoryIds = resetSeenCategoryIds
            lastResetSeenAndPlayedCategoryIds = resetSeenAndPlayedCategoryIds
            lastSeenCardTextIds = seenCardTextIds
            spiel =
                spiel.withCardTextStates(
                    resetSeenCategoryIds = resetSeenCategoryIds,
                    resetSeenAndPlayedCategoryIds = resetSeenAndPlayedCategoryIds,
                    seenCardTextIds = seenCardTextIds,
                )
            val state = CardHistoryState(
                card = card,
                instanceId = nextInstanceId++,
                hasPrevious = history.isNotEmpty(),
            )
            history.add(0, state)
            while (history.size > 11) history.removeLast()
            return state
        }

        suspend fun commit(card: GezogeneKarte): CardHistoryState =
            commitCardDraw(
                gameId = 10,
                resetSeenCategoryIds = emptySet(),
                resetSeenAndPlayedCategoryIds = emptySet(),
                card = card,
            )

        override suspend fun getCurrentCard(gameId: Int): CardHistoryState? =
            history.firstOrNull()?.copy(hasPrevious = history.size > 1)

        override suspend fun popCurrentCard(gameId: Int): CardHistoryState? {
            if (history.size < 2) return null
            history.removeAt(0)
            return history.first().copy(hasPrevious = history.size > 1)
        }

        override suspend fun setCardTextsPlayedState(
            cardTextIds: Set<Int>,
            gespielt: Boolean,
        ) {
            lastPlayedCardTextIds = cardTextIds
            spiel = spiel.withPlayedCardTextIds(
                cardTextIds = cardTextIds,
                gespielt = gespielt,
            )
        }

        override suspend fun resetSeenCards(gameId: Int) = Unit

        override suspend fun resetAllCards(gameId: Int) = Unit

        override suspend fun resetAllCardsForAllGames() = Unit

        override suspend fun setTextsPerCardOverride(gameId: Int, value: Int?) {
            spiel = spiel.copy(texteProKarte = value ?: spiel.standardTexteProKarte)
        }
    }

    private fun Spiel.withCardTextStates(
        resetSeenCategoryIds: Set<Int>,
        resetSeenAndPlayedCategoryIds: Set<Int>,
        seenCardTextIds: Set<Int>,
    ): Spiel {
        val resetSeenCardTextIds = categoryCardTextIds(resetSeenCategoryIds, onlyUnplayed = true)
        val resetSeenAndPlayedCardTextIds = categoryCardTextIds(resetSeenAndPlayedCategoryIds)

        return copy(
            originaleKategorien = originaleKategorien.mapToLinkedHashSet { kategorie ->
                kategorie.withCardTextStates(
                    resetSeenCardTextIds = resetSeenCardTextIds,
                    resetSeenAndPlayedCardTextIds = resetSeenAndPlayedCardTextIds,
                    seenCardTextIds = seenCardTextIds,
                )
            },
            hinzugefuegteKategorien = hinzugefuegteKategorien.mapToLinkedHashSet { kategorie ->
                kategorie.withCardTextStates(
                    resetSeenCardTextIds = resetSeenCardTextIds,
                    resetSeenAndPlayedCardTextIds = resetSeenAndPlayedCardTextIds,
                    seenCardTextIds = seenCardTextIds,
                )
            },
            inaktiveKategorien = inaktiveKategorien.mapToLinkedHashSet { kategorie ->
                kategorie.withCardTextStates(
                    resetSeenCardTextIds = resetSeenCardTextIds,
                    resetSeenAndPlayedCardTextIds = resetSeenAndPlayedCardTextIds,
                    seenCardTextIds = seenCardTextIds,
                )
            },
        )
    }

    private fun Spiel.withPlayedCardTextIds(
        cardTextIds: Set<Int>,
        gespielt: Boolean,
    ): Spiel =
        copy(
            originaleKategorien = originaleKategorien.mapToLinkedHashSet { kategorie ->
                kategorie.withPlayedCardTextIds(
                    cardTextIds = cardTextIds,
                    gespielt = gespielt,
                )
            },
            hinzugefuegteKategorien = hinzugefuegteKategorien.mapToLinkedHashSet { kategorie ->
                kategorie.withPlayedCardTextIds(
                    cardTextIds = cardTextIds,
                    gespielt = gespielt,
                )
            },
            inaktiveKategorien = inaktiveKategorien.mapToLinkedHashSet { kategorie ->
                kategorie.withPlayedCardTextIds(
                    cardTextIds = cardTextIds,
                    gespielt = gespielt,
                )
            },
        )

    private fun Spiel.categoryCardTextIds(
        categoryIds: Set<Int>,
        onlyUnplayed: Boolean = false,
    ): Set<Int> =
        (originaleKategorien + hinzugefuegteKategorien + inaktiveKategorien)
            .filter { kategorie -> kategorie.id() in categoryIds }
            .flatMap { kategorie ->
                (kategorie.originaleKartentexte + kategorie.hinzugefuegteKartentexte)
                    .filter { kartentext -> !onlyUnplayed || !kartentext.gespielt }
                    .map { kartentext -> kartentext.id() }
            }
            .toSet()

    private fun Kategorie.withCardTextStates(
        resetSeenCardTextIds: Set<Int>,
        resetSeenAndPlayedCardTextIds: Set<Int>,
        seenCardTextIds: Set<Int>,
    ): Kategorie =
        copy(
            originaleKartentexte = originaleKartentexte.mapToLinkedHashSet { kartentext ->
                kartentext.withCardTextStates(
                    resetSeenCardTextIds = resetSeenCardTextIds,
                    resetSeenAndPlayedCardTextIds = resetSeenAndPlayedCardTextIds,
                    seenCardTextIds = seenCardTextIds,
                )
            },
            hinzugefuegteKartentexte = hinzugefuegteKartentexte.mapToLinkedHashSet { kartentext ->
                kartentext.withCardTextStates(
                    resetSeenCardTextIds = resetSeenCardTextIds,
                    resetSeenAndPlayedCardTextIds = resetSeenAndPlayedCardTextIds,
                    seenCardTextIds = seenCardTextIds,
                )
            },
            inaktiveKartentexte = inaktiveKartentexte.mapToLinkedHashSet { kartentext ->
                kartentext.withCardTextStates(
                    resetSeenCardTextIds = resetSeenCardTextIds,
                    resetSeenAndPlayedCardTextIds = resetSeenAndPlayedCardTextIds,
                    seenCardTextIds = seenCardTextIds,
                )
            },
        )

    private fun Kategorie.withPlayedCardTextIds(
        cardTextIds: Set<Int>,
        gespielt: Boolean,
    ): Kategorie =
        copy(
            originaleKartentexte = originaleKartentexte.mapToLinkedHashSet { kartentext ->
                if (kartentext.id() in cardTextIds) {
                    kartentext.copy(gespielt = gespielt)
                } else {
                    kartentext
                }
            },
            hinzugefuegteKartentexte = hinzugefuegteKartentexte.mapToLinkedHashSet { kartentext ->
                if (kartentext.id() in cardTextIds) {
                    kartentext.copy(gespielt = gespielt)
                } else {
                    kartentext
                }
            },
            inaktiveKartentexte = inaktiveKartentexte.mapToLinkedHashSet { kartentext ->
                if (kartentext.id() in cardTextIds) {
                    kartentext.copy(gespielt = gespielt)
                } else {
                    kartentext
                }
            },
        )

    private fun Kartentext.withCardTextStates(
        resetSeenCardTextIds: Set<Int>,
        resetSeenAndPlayedCardTextIds: Set<Int>,
        seenCardTextIds: Set<Int>,
    ): Kartentext =
        copy(
            gesehen =
                when {
                    id() in seenCardTextIds -> true
                    id() in resetSeenAndPlayedCardTextIds -> false
                    id() in resetSeenCardTextIds -> false
                    else -> gesehen
                },
            gespielt =
                when {
                    id() in resetSeenAndPlayedCardTextIds -> false
                    else -> gespielt
                },
        )

    private fun <T, R> Iterable<T>.mapToLinkedHashSet(transform: (T) -> R): LinkedHashSet<R> =
        map(transform).toCollection(LinkedHashSet())
}
