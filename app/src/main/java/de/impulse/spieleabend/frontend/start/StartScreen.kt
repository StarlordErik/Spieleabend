@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.start

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@Composable
fun StartScreen(
    games: List<BoardGameShelfItem>,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF8F5EF),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Spieleabend",
                    color = Color(0xFF1E1A17),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Gesellschaftsspiele",
                    color = Color(0xFF62574D),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            BoardGameShelf(
                games = games,
                onGameClick = onGameClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
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
private fun BoardGameShelf(
    games: List<BoardGameShelfItem>,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.dp)
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(Color(0xFFE6D6BC)),
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 28.dp, end = 28.dp, bottom = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                games.forEach { game ->
                    BoardGameBox(
                        game = game,
                        onClick = onGameClick,
                    )
                }
                Spacer(modifier = Modifier.width(36.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(Color(0xFF8D5F3D)),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color(0xFF5F3C27)),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BoardGameShelfPreview() {
    SpieleabendTheme {
        BoardGameShelf(
            games = placeholderBoardGames,
            onGameClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .padding(24.dp),
        )
    }
}

@Composable
private fun BoardGameBox(
    game: BoardGameShelfItem,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .width(132.dp)
            .height(214.dp),
        onClick = { onClick(game.id) },
        color = game.accentColor,
        contentColor = Color.White,
        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = game.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFE7F0EA),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x33212A26)),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BoardGameBoxPreview() {
    SpieleabendTheme {
        BoardGameBox(
            game = placeholderBoardGames.first(),
            onClick = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}
