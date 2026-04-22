package de.impulse.spieleabend.data

import org.junit.Assert.assertEquals
import org.junit.Test

class GameRepositoryImplTest {
    @Test
    fun fakeDatenHabenVierSpieleMitGeteiltenKategorienUndSechsTexten() {
        val repository = GameRepositoryImpl()
        val spiele = (1..4).map { gameNumber ->
            repository.getGame("placeholder-$gameNumber")
        }

        assertEquals(listOf(1, 2, 3, 4), spiele.map { spiel -> spiel.kartentexteProKarte })
        assertEquals(listOf(2, 3, 4, 5), spiele.map { spiel -> spiel.kategorien.size })

        val kategorien = spiele.flatMap { spiel -> spiel.kategorien }.distinctBy { kategorie -> kategorie.id }
        assertEquals(5, kategorien.size)
        assertEquals(List(5) { 6 }, kategorien.map { kategorie -> kategorie.kartentexte.size })
        assertEquals(
            30,
            kategorien
                .flatMap { kategorie -> kategorie.kartentexte }
                .distinctBy { kartentext -> kartentext.id }
                .size,
        )
    }
}
