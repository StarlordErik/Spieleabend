@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 28.dp, vertical = 58.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StartScreenPreview() {
    SpieleabendTheme {
        StartScreen(
            games = placeholderBoardGames,
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
    Box(modifier = modifier) {
        games.forEachIndexed { index, game ->
            GameSideImage(
                game = game,
                onClick = onGameClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(
                        x = ((index - 1) * 8).dp,
                        y = -(index * 46).dp,
                    )
                    .fillMaxWidth(GameImageWidthFractions[index])
                    .height(64.dp)
                    .graphicsLayer(
                        rotationZ = GameImageRotations[index],
                        shadowElevation = 10f,
                        shape = RoundedCornerShape(7.dp),
                        clip = true,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StackedGameImagesPreview() {
    SpieleabendTheme {
        StackedGameImages(
            games = placeholderBoardGames,
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
            game = placeholderBoardGames.first(),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(24.dp),
        )
    }
}

private const val GAME_STACK_SIZE = 4

private val GameImageWidthFractions = listOf(0.78f, 0.74f, 0.8f, 0.76f)
private val GameImageRotations = listOf(-3.5f, 1.5f, -1.2f, 2.4f)
