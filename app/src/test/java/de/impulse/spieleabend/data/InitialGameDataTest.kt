package de.impulse.spieleabend.data

import de.impulse.spieleabend.data.seed.InitialGameData
import de.impulse.spieleabend.data.seed.toDatabaseSeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InitialGameDataTest {
    @Test
    fun startDatenEntsprechenRohdatenOhnePrivacy() {
        val spiele = InitialGameData.spiele

        assertEquals(
            listOf(
                "erzaehlt-euch-mehr",
                "erzaehlt-euch-mehr-fuer-paare",
                "fun-facts",
                "were-not-really-strangers",
            ),
            spiele.map { spiel -> spiel.id },
        )
        assertEquals(List(4) { 1 }, spiele.map { spiel -> spiel.kartentexteProKarte })
        assertEquals(listOf(3, 3, 2, 3), spiele.map { spiel -> spiel.kategorien.size })

        val kategorien = spiele.flatMap { spiel -> spiel.kategorien }.distinctBy { kategorie ->
            kategorie.id
        }

        assertEquals(11, kategorien.size)
        assertEquals(
            listOf(21, 10, 39, 20, 14, 36, 94, 92, 50, 53, 50),
            kategorien.map { kategorie -> kategorie.kartentexte.size },
        )
        assertEquals(
            479,
            kategorien
                .flatMap { kategorie -> kategorie.kartentexte }
                .distinctBy { kartentext -> kartentext.id }
                .size,
        )
        assertTrue(spiele.none { spiel -> spiel.id.contains("privacy") })
    }

    @Test
    fun startDatenWerdenOhneDuplikateInDatenbankEntitiesGemappt() {
        val seed = InitialGameData.spiele.toDatabaseSeed()

        assertEquals(4, seed.spiele.size)
        assertEquals(11, seed.kategorien.size)
        assertEquals(479, seed.kartentexte.size)
        assertEquals(494, seed.lokalisierungen.size)
        assertEquals(514, seed.translationen.size)
        assertEquals(11, seed.spielXKategorien.size)
        assertEquals(479, seed.kategorieXKartentexte.size)
    }
}
