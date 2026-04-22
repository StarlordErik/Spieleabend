package de.impulse.spieleabend.data

import de.impulse.spieleabend.data.seed.InitialGameData
import de.impulse.spieleabend.data.seed.toDatabaseSeed
import org.junit.Assert.assertEquals
import org.junit.Test

class InitialGameDataTest {
    @Test
    fun startDatenHabenVierSpieleMitGeteiltenKategorienUndSechsTexten() {
        val spiele = InitialGameData.spiele

        assertEquals(listOf(1, 2, 3, 4), spiele.map { spiel -> spiel.kartentexteProKarte })
        assertEquals(listOf(2, 3, 4, 5), spiele.map { spiel -> spiel.kategorien.size })

        val kategorien = spiele.flatMap { spiel -> spiel.kategorien }.distinctBy { kategorie ->
            kategorie.id
        }

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

    @Test
    fun startDatenWerdenOhneDuplikateInDatenbankEntitiesGemappt() {
        val seed = InitialGameData.spiele.toDatabaseSeed()

        assertEquals(4, seed.spiele.size)
        assertEquals(5, seed.kategorien.size)
        assertEquals(30, seed.kartentexte.size)
        assertEquals(39, seed.lokalisierungen.size)
        assertEquals(78, seed.translationen.size)
        assertEquals(14, seed.spielXKategorien.size)
        assertEquals(30, seed.kategorieXKartentexte.size)
    }
}
