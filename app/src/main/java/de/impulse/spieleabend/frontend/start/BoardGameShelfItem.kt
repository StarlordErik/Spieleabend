package de.impulse.spieleabend.frontend.start

import androidx.compose.runtime.Immutable
import androidx.annotation.DrawableRes
import de.impulse.spieleabend.R

@Immutable
data class BoardGameShelfItem(
    val id: Int,
    @param:DrawableRes val imageResId: Int,
)

val boardGameShelfItems = listOf(
    BoardGameShelfItem(
        id = 1,
        imageResId = R.drawable.placeholder_game_box_side_1,
    ),
    BoardGameShelfItem(
        id = 2,
        imageResId = R.drawable.placeholder_game_box_side_2,
    ),
    BoardGameShelfItem(
        id = 3,
        imageResId = R.drawable.placeholder_game_box_side_3,
    ),
    BoardGameShelfItem(
        id = 4,
        imageResId = R.drawable.placeholder_game_box_side_4,
    ),
)
