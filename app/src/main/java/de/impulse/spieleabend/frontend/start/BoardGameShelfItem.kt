package de.impulse.spieleabend.frontend.start

import androidx.compose.runtime.Immutable
import androidx.annotation.DrawableRes
import de.impulse.spieleabend.R
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.Spiel

@Immutable
data class BoardGameShelfItem(
    val id: Int,
    val name: String,
    @param:DrawableRes val imageResId: Int,
)

internal fun List<Spiel>.toBoardGameShelfItems(sprache: Sprache): List<BoardGameShelfItem> =
    mapIndexed { index, spiel ->
        BoardGameShelfItem(
            id = spiel.id(),
            name = spiel.text(sprache),
            imageResId = placeholderImage(index),
        )
    }

val boardGameShelfItems =
    listOf(
        BoardGameShelfItem(
            id = 1,
            name = "Erzaehlt euch mehr",
            imageResId = R.drawable.placeholder_game_box_side_1,
        ),
        BoardGameShelfItem(
            id = 75,
            name = "Erzaehlt euch mehr fuer Paare",
            imageResId = R.drawable.placeholder_game_box_side_2,
        ),
        BoardGameShelfItem(
            id = 149,
            name = "Fun Facts",
            imageResId = R.drawable.placeholder_game_box_side_3,
        ),
        BoardGameShelfItem(
            id = 337,
            name = "Privacy",
            imageResId = R.drawable.placeholder_game_box_side_4,
        ),
        BoardGameShelfItem(
            id = 699,
            name = "We're Not Really Strangers",
            imageResId = R.drawable.placeholder_game_box_side_1,
        ),
    )

@DrawableRes
private fun placeholderImage(index: Int): Int =
    PlaceholderImages[index % PlaceholderImages.size]

private val PlaceholderImages =
    listOf(
        R.drawable.placeholder_game_box_side_1,
        R.drawable.placeholder_game_box_side_2,
        R.drawable.placeholder_game_box_side_3,
        R.drawable.placeholder_game_box_side_4,
    )
