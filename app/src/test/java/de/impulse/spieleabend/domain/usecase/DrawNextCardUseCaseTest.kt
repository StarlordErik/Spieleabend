package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation
import de.impulse.spieleabend.domain.repository.GameRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DrawNextCardUseCaseTest {
    @Test
    fun drawFromCategoryResetetKategorieUndMarkiertGezogeneKartentexteAlsGesehen() = runBlocking {
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

        assertEquals(setOf(1), repository.lastResetCategoryIds)
        assertEquals(setOf(101, 102), repository.lastSeenCardTextIds)
        assertEquals(
            setOf(101, 102),
            result.karte.kartentexte.map { gezogenerKartentext ->
                gezogenerKartentext.kartentext.id()
            }.toSet(),
        )
        assertTrue(
            result.spiel.kategorien
                .single { kategorie -> kategorie.id() == 1 }
                .kartentexte
                .all { kartentext -> kartentext.gesehen },
        )
    }

    @Test
    fun drawRandomResetetAlleAktivenKategorienWennUngeseheneKartentexteNichtReichen() = runBlocking {
        val repository =
            FakeGameRepository(
                spiel(
                    kategorie(
                        id = 1,
                        kartentext(id = 101, gesehen = true),
                    ),
                    kategorie(
                        id = 2,
                        kartentext(id = 201, gesehen = false),
                    ),
                ),
            )

        val result = DrawNextRandomCardUseCase(repository)(gameId = 10)

        assertEquals(setOf(1, 2), repository.lastResetCategoryIds)
        assertEquals(setOf(101, 201), repository.lastSeenCardTextIds)
        assertTrue(
            result.spiel.kategorien.flatMap { kategorie -> kategorie.kartentexte }
                .all { kartentext -> kartentext.gesehen },
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
    ): Kartentext =
        Kartentext(
            lokalisierung = lokalisierung(id = id),
            gesehen = gesehen,
        )

    private fun lokalisierung(id: Int): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = setOf(Translation(sprache = Sprache.OG, text = "lokalisierung-$id")),
            ogSprache = Sprache.DE,
        )

    private inner class FakeGameRepository(
        initialSpiel: Spiel,
    ) : GameRepository {
        private var spiel: Spiel = initialSpiel

        var lastResetCategoryIds: Set<Int> = emptySet()
            private set

        var lastSeenCardTextIds: Set<Int> = emptySet()
            private set

        override suspend fun getGames(): List<Spiel> = listOf(spiel)

        override suspend fun getGame(gameId: Int): Spiel = spiel

        override suspend fun updateSeenStates(
            resetCategoryIds: Set<Int>,
            seenCardTextIds: Set<Int>,
        ) {
            lastResetCategoryIds = resetCategoryIds
            lastSeenCardTextIds = seenCardTextIds
            spiel = spiel.withSeenStates(resetCategoryIds, seenCardTextIds)
        }
    }

    private fun Spiel.withSeenStates(
        resetCategoryIds: Set<Int>,
        seenCardTextIds: Set<Int>,
    ): Spiel {
        val resetCardTextIds =
            (originaleKategorien + hinzugefuegteKategorien + inaktiveKategorien)
                .filter { kategorie -> kategorie.id() in resetCategoryIds }
                .flatMap { kategorie ->
                    (kategorie.originaleKartentexte + kategorie.hinzugefuegteKartentexte).map { kartentext ->
                        kartentext.id()
                    }
                }
                .toSet()

        return copy(
            originaleKategorien = originaleKategorien.mapToLinkedHashSet { kategorie ->
                kategorie.withSeenStates(resetCardTextIds, seenCardTextIds)
            },
            hinzugefuegteKategorien = hinzugefuegteKategorien.mapToLinkedHashSet { kategorie ->
                kategorie.withSeenStates(resetCardTextIds, seenCardTextIds)
            },
            inaktiveKategorien = inaktiveKategorien.mapToLinkedHashSet { kategorie ->
                kategorie.withSeenStates(resetCardTextIds, seenCardTextIds)
            },
        )
    }

    private fun Kategorie.withSeenStates(
        resetCardTextIds: Set<Int>,
        seenCardTextIds: Set<Int>,
    ): Kategorie =
        copy(
            originaleKartentexte = originaleKartentexte.mapToLinkedHashSet { kartentext ->
                kartentext.withSeenState(resetCardTextIds, seenCardTextIds)
            },
            hinzugefuegteKartentexte = hinzugefuegteKartentexte.mapToLinkedHashSet { kartentext ->
                kartentext.withSeenState(resetCardTextIds, seenCardTextIds)
            },
            inaktiveKartentexte = inaktiveKartentexte.mapToLinkedHashSet { kartentext ->
                kartentext.withSeenState(resetCardTextIds, seenCardTextIds)
            },
        )

    private fun Kartentext.withSeenState(
        resetCardTextIds: Set<Int>,
        seenCardTextIds: Set<Int>,
    ): Kartentext =
        copy(
            gesehen =
                when {
                    id() in seenCardTextIds -> true
                    id() in resetCardTextIds -> false
                    else -> gesehen
                },
        )

    private fun <T, R> Iterable<T>.mapToLinkedHashSet(transform: (T) -> R): LinkedHashSet<R> =
        map(transform).toCollection(LinkedHashSet())
}
