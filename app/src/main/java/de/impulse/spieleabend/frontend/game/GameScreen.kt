@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenContent(
        uiState = uiState,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    SpieleabendTheme {
        GameScreenContent(uiState = PreviewUiState)
    }
}

@Composable
private fun GameScreenContent(
    uiState: GameUiState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = TableBackground,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            var selectedCard by remember(uiState) {
                mutableStateOf(uiState.randomCardSelection())
            }
            val horizontalPadding = if (maxWidth < CompactWidthBreakpoint) {
                CompactHorizontalPadding
            } else {
                ExpandedHorizontalPadding
            }

            GamePlayArea(
                spielName = uiState.spielName,
                selectedCard = selectedCard,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = horizontalPadding,
                        top = 24.dp,
                        end = horizontalPadding,
                        bottom = 24.dp,
                    ),
            )

            CategoryTabs(
                kategorien = uiState.kategorien,
                modifier = Modifier.fillMaxSize(),
                onKategorieSelected = { kategorie, color ->
                    selectedCard = kategorie.cardSelection(color = color)
                },
                onRandomSelected = {
                    selectedCard = uiState.randomCardSelection()
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GamePlayAreaPreview() {
    SpieleabendTheme {
        GamePlayArea(
            spielName = PreviewUiState.spielName,
            selectedCard = GameCardSelection(
                kartentexte = PreviewUiState.kartentexte,
                textPanelColors = emptyList(),
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun GamePlayArea(
    spielName: String,
    selectedCard: GameCardSelection,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = spielName,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            color = TitleColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
            ),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            GameCard(
                kartentexte = selectedCard.kartentexte,
                textPanelColors = selectedCard.textPanelColors,
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .heightIn(max = 720.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = 12.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenContentPreview() {
    SpieleabendTheme {
        GameScreenContent(uiState = PreviewUiState)
    }
}

private val TableBackground = Color(0xFFE5EFE9)
private val TitleColor = Color(0xFF22201D)
private val CompactWidthBreakpoint = 420.dp
private val CompactHorizontalPadding = 52.dp
private val ExpandedHorizontalPadding = 76.dp

private fun GameKategorieUiModel.cardSelection(color: Color): GameCardSelection {
    val shuffledKartentexte = kartentexte.shuffled()
    return GameCardSelection(
        kartentexte = shuffledKartentexte,
        textPanelColors = List(shuffledKartentexte.size) { color },
    )
}

private fun GameUiState.randomCardSelection(): GameCardSelection =
    kategorien
        .flatMapIndexed { index, kategorie ->
            kategorie.kartentexte.map { kartentext ->
                ColoredKartentext(
                    kartentext = kartentext,
                    color = categoryTabColor(index),
                )
            }
        }
        .shuffled()
        .distinctBy { coloredKartentext -> coloredKartentext.kartentext.id }
        .toGameCardSelection()

private fun List<ColoredKartentext>.toGameCardSelection(): GameCardSelection =
    GameCardSelection(
        kartentexte = map { coloredKartentext -> coloredKartentext.kartentext },
        textPanelColors = map { coloredKartentext -> coloredKartentext.color },
    )

private data class GameCardSelection(
    val kartentexte: List<GameKartentextUiModel>,
    val textPanelColors: List<Color>,
)

private data class ColoredKartentext(
    val kartentext: GameKartentextUiModel,
    val color: Color,
)
