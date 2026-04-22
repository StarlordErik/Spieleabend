@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.impulse.spieleabend.R
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@Composable
fun StartScreen(
    games: List<BoardGameShelfItem>,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        ShelfBackground(modifier = Modifier.fillMaxSize())
        StackedGameImages(
            games = games.take(GAME_STACK_SIZE),
            onGameClick = onGameClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxSize()
                .padding(horizontal = 28.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StartScreenPreview() {
    SpieleabendTheme {
        StartScreen(
            games = boardGameShelfItems,
            onGameClick = {},
        )
    }
}

@Composable
private fun ShelfBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.placeholder_shelf_background),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

@Preview(showBackground = true)
@Composable
private fun ShelfBackgroundPreview() {
    SpieleabendTheme {
        ShelfBackground(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun StackedGameImages(
    games: List<BoardGameShelfItem>,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val shelfStacks = listOf(
            ShelfStack(
                shelfTop = maxHeight * MIDDLE_SHELF_TOP_FRACTION,
                upperLimit = maxHeight * TOP_SHELF_TOP_FRACTION + SHELF_BOARD_HEIGHT_DP.dp + SHELF_GAP_DP.dp,
            ),
            ShelfStack(
                shelfTop = maxHeight * TOP_SHELF_TOP_FRACTION,
                upperLimit = TOP_SHELF_MARGIN_DP.dp,
            ),
            ShelfStack(
                shelfTop = maxHeight * BOTTOM_SHELF_TOP_FRACTION,
                upperLimit = maxHeight * MIDDLE_SHELF_TOP_FRACTION + SHELF_BOARD_HEIGHT_DP.dp + SHELF_GAP_DP.dp,
            ),
        )
        var gameIndex = 0

        shelfStacks.forEach { shelfStack ->
            val availableHeight = shelfStack.shelfTop - shelfStack.upperLimit
            val stackCapacity = maxOf(
                1,
                ((availableHeight + GAME_IMAGE_GAP_DP.dp).value / GAME_STACK_STEP_DP).toInt(),
            )

            repeat(stackCapacity) { stackIndex ->
                if (gameIndex < games.size) {
                    val game = games[gameIndex]
                    val gameWidth = maxWidth * GameImageWidthFractions[gameIndex % GameImageWidthFractions.size]
                    val gameTop = shelfStack.shelfTop -
                        GAME_IMAGE_HEIGHT_DP.dp -
                        (GAME_IMAGE_HEIGHT_DP.dp + GAME_IMAGE_GAP_DP.dp) * stackIndex.toFloat()

                    GameSideImage(
                        game = game,
                        onClick = onGameClick,
                        modifier = Modifier
                            .offset(
                                x = (maxWidth - gameWidth) / 2f,
                                y = gameTop,
                            )
                            .width(gameWidth)
                            .height(GAME_IMAGE_HEIGHT_DP.dp)
                            .graphicsLayer(
                                shadowElevation = 10f,
                                shape = RoundedCornerShape(7.dp),
                                clip = true,
                            ),
                    )

                    gameIndex += 1
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StackedGameImagesPreview() {
    SpieleabendTheme {
        StackedGameImages(
            games = boardGameShelfItems,
            onGameClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(28.dp),
        )
    }
}

@Composable
private fun GameSideImage(
    game: BoardGameShelfItem,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(game.imageResId),
        contentDescription = null,
        modifier = modifier
            .clip(RoundedCornerShape(7.dp))
            .clickable { onClick(game.id) },
        contentScale = ContentScale.FillBounds,
    )
}

@Preview(showBackground = true)
@Composable
private fun GameSideImagePreview() {
    SpieleabendTheme {
        GameSideImage(
            game = boardGameShelfItems.first(),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(24.dp),
        )
    }
}

private const val GAME_STACK_SIZE = 4
private const val GAME_IMAGE_HEIGHT_DP = 52
private const val GAME_IMAGE_GAP_DP = 4
private const val GAME_STACK_STEP_DP = GAME_IMAGE_HEIGHT_DP + GAME_IMAGE_GAP_DP
private const val SHELF_BOARD_HEIGHT_DP = 32
private const val SHELF_GAP_DP = 8
private const val TOP_SHELF_MARGIN_DP = 96
private const val TOP_SHELF_TOP_FRACTION = 0.307f
private const val MIDDLE_SHELF_TOP_FRACTION = 0.547f
private const val BOTTOM_SHELF_TOP_FRACTION = 0.786f

private val GameImageWidthFractions = listOf(0.78f, 0.74f, 0.8f, 0.76f)

private data class ShelfStack(
    val shelfTop: Dp,
    val upperLimit: Dp,
)
