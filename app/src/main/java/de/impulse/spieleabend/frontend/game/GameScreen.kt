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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    when (val state = uiState) {
        GameScreenUiState.Loading -> GameLoadingContent(modifier = modifier)
        is GameScreenUiState.Loaded -> {
            GameScreenContent(
                uiState = state.game,
                modifier = modifier,
                onKategorieSelected = viewModel::selectKategorie,
                onRandomSelected = viewModel::selectRandom,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    SpieleabendTheme {
        GameScreenContent(uiState = PreviewUiState)
    }
}

@Preview(showBackground = true)
@Composable
private fun GameLoadingContentPreview() {
    SpieleabendTheme {
        GameLoadingContent()
    }
}

@Composable
private fun GameLoadingContent(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = TableBackground,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = TitleColor)
        }
    }
}

@Composable
private fun GameScreenContent(
    uiState: GameUiState,
    modifier: Modifier = Modifier,
    onKategorieSelected: (Int) -> Unit = {},
    onRandomSelected: () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = TableBackground,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val horizontalPadding = if (maxWidth < CompactWidthBreakpoint) {
                CompactHorizontalPadding
            } else {
                ExpandedHorizontalPadding
            }

            GamePlayArea(
                spielName = uiState.spielName,
                aktuelleKarte = uiState.aktuelleKarte,
                kategorien = uiState.kategorien,
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
                onKategorieSelected = { kategorieId -> onKategorieSelected(kategorieId) },
                onRandomSelected = onRandomSelected,
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
            aktuelleKarte = PreviewUiState.aktuelleKarte,
            kategorien = PreviewUiState.kategorien,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun GamePlayArea(
    spielName: String,
    aktuelleKarte: GameCardUiModel,
    kategorien: List<GameKategorieUiModel>,
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
                kartentexte = aktuelleKarte.kartentexte,
                textPanelColors = aktuelleKarte.textPanelColors(kategorien),
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

private fun GameCardUiModel.textPanelColors(kategorien: List<GameKategorieUiModel>): List<Color> =
    kartentexte.map { kartentext ->
        val kategorieIndex = kategorien.indexOfFirst { kategorie ->
            kategorie.id == kartentext.kategorieId
        }

        if (kategorieIndex >= 0) {
            categoryTabColor(kategorieIndex)
        } else {
            FallbackTextPanelColor
        }
    }

private val FallbackTextPanelColor = Color(0xFFE8E0FF)
