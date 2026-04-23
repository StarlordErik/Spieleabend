package de.impulse.spieleabend.frontend.start

import de.impulse.spieleabend.R
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation
import org.junit.Assert.assertEquals
import org.junit.Test

class BoardGameShelfItemTest {
    @Test
    fun verwendetEchteSpielIdsUndErzeugtAuchDasFuenfteRegalspiel() {
        val spiele =
            listOf(
                spiel(1, "Erzaehlt euch mehr"),
                spiel(75, "Erzaehlt euch mehr fuer Paare"),
                spiel(149, "Fun Facts"),
                spiel(337, "Privacy"),
                spiel(699, "We're Not Really Strangers"),
            )

        val shelfItems = spiele.toBoardGameShelfItems(Sprache.DE)

        assertEquals(listOf(1, 75, 149, 337, 699), shelfItems.map { it.id })
        assertEquals(
            listOf(
                R.drawable.placeholder_game_box_side_1,
                R.drawable.placeholder_game_box_side_2,
                R.drawable.placeholder_game_box_side_3,
                R.drawable.placeholder_game_box_side_4,
                R.drawable.placeholder_game_box_side_1,
            ),
            shelfItems.map { it.imageResId },
        )
        assertEquals(
            listOf(
                "Erzaehlt euch mehr",
                "Erzaehlt euch mehr fuer Paare",
                "Fun Facts",
                "Privacy",
                "We're Not Really Strangers",
            ),
            shelfItems.map { it.name },
        )
    }

    private fun spiel(
        id: Int,
        text: String,
    ): Spiel =
        Spiel(
            lokalisierung =
                Lokalisierung(
                    id = id,
                    translationen = linkedSetOf(
                        Translation(sprache = Sprache.OG, text = text),
                        Translation(sprache = Sprache.DE, text = text),
                    ),
                    ogSprache = Sprache.DE,
                ),
        )
}
