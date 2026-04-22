package de.impulse.spieleabend.frontend.start

import androidx.compose.runtime.Immutable
import androidx.annotation.DrawableRes
import de.impulse.spieleabend.R

@Immutable
data class BoardGameShelfItem(
    val id: String,
    @param:DrawableRes val imageResId: Int,
)

val boardGameShelfItems = listOf(
    BoardGameShelfItem(
        id = "erzaehlt-euch-mehr",
        imageResId = R.drawable.placeholder_game_box_side_1,
    ),
    BoardGameShelfItem(
        id = "erzaehlt-euch-mehr-fuer-paare",
        imageResId = R.drawable.placeholder_game_box_side_2,
    ),
    BoardGameShelfItem(
        id = "fun-facts",
        imageResId = R.drawable.placeholder_game_box_side_3,
    ),
    BoardGameShelfItem(
        id = "were-not-really-strangers",
        imageResId = R.drawable.placeholder_game_box_side_4,
    ),
)
