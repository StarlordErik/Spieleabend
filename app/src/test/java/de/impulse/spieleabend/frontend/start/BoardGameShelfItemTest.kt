@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.start

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
                spiel(
                    1,
                    "Erzählt euch mehr",
                    "images/game_box_side_erzaehlt_euch_mehr.png",
                ),
                spiel(
                    75,
                    "Erzählt euch mehr für Paare",
                    "images/game_box_side_erzaehlt_euch_mehr_fuer_paare.png",
                ),
                spiel(
                    149,
                    "Fun Facts",
                    "images/game_box_side_fun_facts.png",
                ),
                spiel(
                    337,
                    "Privacy",
                    "images/game_box_side_privacy.png",
                ),
                spiel(
                    699,
                    "We're Not Really Strangers",
                    "images/game_box_side_were_not_really_strangers.png",
                ),
            )

        val shelfItems = spiele.toBoardGameShelfItems(Sprache.DE)

        assertEquals(listOf(1, 75, 149, 337, 699), shelfItems.map { it.id })
        assertEquals(
            listOf(
                "images/game_box_side_erzaehlt_euch_mehr.png",
                "images/game_box_side_erzaehlt_euch_mehr_fuer_paare.png",
                "images/game_box_side_fun_facts.png",
                "images/game_box_side_privacy.png",
                "images/game_box_side_were_not_really_strangers.png",
            ),
            shelfItems.map { it.imagePath },
        )
        assertEquals(listOf(0.82f, 0.72f, 0.88f, 0.68f, 0.9f), shelfItems.map { it.widthFraction })
        assertEquals(listOf(58, 64, 48, 60, 44), shelfItems.map { it.heightDp })
        assertEquals(
            listOf(
                "Erzählt euch mehr",
                "Erzählt euch mehr für Paare",
                "Fun Facts",
                "Privacy",
                "We're Not Really Strangers",
            ),
            shelfItems.map { it.name },
        )
    }

    @Test
    fun leitetAssetPfadeAusBildpfadenAb() {
        assertEquals(
            "images/game_box_side_fun_facts.png",
            assetImagePathFromMetadataPath("app/src/main/res/drawable-nodpi/game_box_side_fun_facts.png"),
        )
        assertEquals(
            "images/game_box_side_privacy.png",
            assetImagePathFromMetadataPath("game_box_side_privacy.png"),
        )
        assertEquals(
            "images/game_box_side_erzaehlt_euch_mehr.png",
            assetImagePathFromMetadataPath("game_box_side_erzaehlt_euch_mehr.png"),
        )
        assertEquals(
            "images/game_box_side_erzaehlt_euch_mehr.png",
            assetImagePathFromMetadataPath("images/game_box_side_erzaehlt_euch_mehr.png"),
        )
        assertEquals(
            "images/game_box_side_privacy.png",
            assetImagePathFromMetadataPath("app/src/main/assets/images/game_box_side_privacy.png"),
        )
        assertEquals(null, assetImagePathFromMetadataPath(null))
    }

    private fun spiel(
        id: Int,
        text: String,
        bildDateiname: String,
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
            bildDateiname = bildDateiname,
        )
}
