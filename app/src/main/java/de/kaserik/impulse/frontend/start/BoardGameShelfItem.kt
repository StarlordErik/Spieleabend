@file:Suppress("MagicNumber")

package de.kaserik.impulse.frontend.start

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import de.kaserik.impulse.R
import de.kaserik.impulse.common.AssetPaths
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.model.Spiel

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
    val fallbackAppearance = fallbackShelfAppearances[index % fallbackShelfAppearances.size]
    val dimensions = gameShelfDimensionsById[gameId] ?: return fallbackAppearance

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

private val gameShelfDimensionsById =
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

private val fallbackShelfAppearances =
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

internal fun assetImagePathFromMetadataPath(imagePath: String?): String? {
    val normalizedPath = imagePath?.trim()?.takeIf(String::isNotEmpty)
    val assetPath =
        when {
            normalizedPath == null -> null
            normalizedPath.startsWith(AssetPaths.Images) -> normalizedPath
            normalizedPath.startsWith(AssetPaths.SourceAssets) -> normalizedPath.removePrefix(AssetPaths.SourceAssets)
            else -> {
                val fileName =
                    normalizedPath
                        .substringAfterLast('/')
                        .substringAfterLast('\\')
                        .takeIf(String::isNotEmpty)

                fileName?.let { name -> AssetPaths.Images + name }
            }
        }

    return assetPath
}

val boardGameShelfItems: List<BoardGameShelfItem>
    @Composable get() =
    listOf(
        previewShelfItem(
            id = 1,
            name = stringResource(R.string.preview_game_conversation),
            imagePath = AssetPaths.Conversation,
            index = 0,
        ),
        previewShelfItem(
            id = 75,
            name = stringResource(R.string.preview_game_couples),
            imagePath = AssetPaths.Couples,
            index = 1,
        ),
        previewShelfItem(
            id = 149,
            name = stringResource(R.string.mode_fun_facts),
            imagePath = AssetPaths.FunFacts,
            index = 2,
        ),
        previewShelfItem(
            id = 337,
            name = stringResource(R.string.preview_game_privacy),
            imagePath = AssetPaths.Privacy,
            index = 3,
        ),
        previewShelfItem(
            id = 699,
            name = stringResource(R.string.preview_game_strangers),
            imagePath = AssetPaths.Strangers,
            index = 4,
        ),
    )
