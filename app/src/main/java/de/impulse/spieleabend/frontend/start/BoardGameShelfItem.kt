@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.start

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import de.impulse.spieleabend.R
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.Spiel

@Immutable
data class BoardGameShelfItem(
    val id: Int,
    val name: String,
    val imagePath: String?,
    @param:DrawableRes val fallbackImageResId: Int,
    val widthFraction: Float,
    val heightDp: Int,
)

internal fun List<Spiel>.toBoardGameShelfItems(sprache: Sprache): List<BoardGameShelfItem> =
    mapIndexed { index, spiel ->
        val appearance = shelfAppearance(spiel.id(), index)
        BoardGameShelfItem(
            id = spiel.id(),
            name = spiel.text(sprache),
            imagePath = spiel.bildDateiname,
            fallbackImageResId = appearance.fallbackImageResId,
            widthFraction = appearance.widthFraction,
            heightDp = appearance.heightDp,
        )
    }

private fun previewShelfItem(
    id: Int,
    name: String,
    imagePath: String,
    index: Int,
): BoardGameShelfItem {
    val appearance = shelfAppearance(id, index)
    return BoardGameShelfItem(
        id = id,
        name = name,
        imagePath = imagePath,
        fallbackImageResId = appearance.fallbackImageResId,
        widthFraction = appearance.widthFraction,
        heightDp = appearance.heightDp,
    )
}

private fun shelfAppearance(
    gameId: Int,
    index: Int,
): GameShelfAppearance {
    val fallbackAppearance = FallbackShelfAppearances[index % FallbackShelfAppearances.size]
    val dimensions = GameShelfDimensionsById[gameId] ?: return fallbackAppearance

    return GameShelfAppearance(
        fallbackImageResId = fallbackAppearance.fallbackImageResId,
        widthFraction = dimensions.widthFraction,
        heightDp = dimensions.heightDp,
    )
}

@Immutable
private data class GameShelfAppearance(
    @param:DrawableRes val fallbackImageResId: Int,
    val widthFraction: Float,
    val heightDp: Int,
)

@Immutable
private data class GameShelfDimensions(
    val widthFraction: Float,
    val heightDp: Int,
)

private val GameShelfDimensionsById =
    mapOf(
        1 to GameShelfDimensions(
            widthFraction = 0.82f,
            heightDp = 58,
        ),
        75 to GameShelfDimensions(
            widthFraction = 0.72f,
            heightDp = 64,
        ),
        149 to GameShelfDimensions(
            widthFraction = 0.88f,
            heightDp = 48,
        ),
        337 to GameShelfDimensions(
            widthFraction = 0.68f,
            heightDp = 60,
        ),
        699 to GameShelfDimensions(
            widthFraction = 0.9f,
            heightDp = 44,
        ),
    )

private val FallbackShelfAppearances =
    listOf(
        GameShelfAppearance(
            fallbackImageResId = R.drawable.placeholder_game_box_side_1,
            widthFraction = 0.78f,
            heightDp = 52,
        ),
        GameShelfAppearance(
            fallbackImageResId = R.drawable.placeholder_game_box_side_2,
            widthFraction = 0.74f,
            heightDp = 56,
        ),
        GameShelfAppearance(
            fallbackImageResId = R.drawable.placeholder_game_box_side_3,
            widthFraction = 0.8f,
            heightDp = 48,
        ),
        GameShelfAppearance(
            fallbackImageResId = R.drawable.placeholder_game_box_side_4,
            widthFraction = 0.76f,
            heightDp = 54,
        ),
    )

internal fun drawableResourceNameFromImagePath(imagePath: String?): String? {
    val fileName =
        imagePath
            ?.substringAfterLast('/')
            ?.substringAfterLast('\\')
            ?.trim()
            ?.takeIf(String::isNotEmpty)
            ?: return null

    return fileName.substringBeforeLast('.').takeIf(String::isNotBlank)
}

val boardGameShelfItems =
    listOf(
        previewShelfItem(
            id = 1,
            name = "Erzählt euch mehr",
            imagePath = "app/src/main/res/drawable-nodpi/game_box_side_erzaehlt_euch_mehr.png",
            index = 0,
        ),
        previewShelfItem(
            id = 75,
            name = "Erzählt euch mehr für Paare",
            imagePath = "app/src/main/res/drawable-nodpi/game_box_side_erzaehlt_euch_mehr_fuer_paare.png",
            index = 1,
        ),
        previewShelfItem(
            id = 149,
            name = "Fun Facts",
            imagePath = "app/src/main/res/drawable-nodpi/game_box_side_fun_facts.png",
            index = 2,
        ),
        previewShelfItem(
            id = 337,
            name = "Privacy",
            imagePath = "app/src/main/res/drawable-nodpi/game_box_side_privacy.png",
            index = 3,
        ),
        previewShelfItem(
            id = 699,
            name = "We're Not Really Strangers",
            imagePath = "app/src/main/res/drawable-nodpi/game_box_side_were_not_really_strangers.png",
            index = 4,
        ),
    )
