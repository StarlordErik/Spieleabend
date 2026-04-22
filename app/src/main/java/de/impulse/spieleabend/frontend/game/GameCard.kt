@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
internal fun GameCard(
    kartentexte: List<GameKartentextUiModel>,
    textPanelColors: List<Color> = emptyList(),
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = CardBackground,
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (kartentexte.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                kartentexte.forEachIndexed { index, kartentext ->
                    CardTextPanel(
                        kartentext = kartentext,
                        index = index,
                        kartentextCount = kartentexte.size,
                        textPanelColor = textPanelColors.getOrNull(index),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameCardPreview() {
    SpieleabendTheme {
        GameCard(
            kartentexte = PreviewUiState.aktuelleKarte.kartentexte,
            modifier = Modifier
                .width(320.dp)
                .height(520.dp)
                .padding(24.dp),
        )
    }
}

@Composable
private fun CardTextPanel(
    kartentext: GameKartentextUiModel,
    index: Int,
    kartentextCount: Int,
    textPanelColor: Color? = null,
    modifier: Modifier = Modifier,
) {
    val textStyle = when (kartentextCount) {
        1 -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
        2 -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
        3 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        else -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(textPanelColor ?: CardTextPanelColors[index % CardTextPanelColors.size])
            .padding(horizontal = 18.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = kartentext.text,
            color = CardTextColor,
            textAlign = TextAlign.Center,
            maxLines = maxLinesFor(kartentextCount),
            overflow = TextOverflow.Ellipsis,
            style = textStyle,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardTextPanelPreview() {
    SpieleabendTheme {
        CardTextPanel(
            kartentext = PreviewUiState.aktuelleKarte.kartentexte.first(),
            index = 0,
            kartentextCount = PreviewUiState.aktuelleKarte.kartentexte.size,
            modifier = Modifier
                .width(320.dp)
                .height(124.dp)
                .padding(24.dp),
        )
    }
}

private fun maxLinesFor(kartentextCount: Int): Int =
    when (kartentextCount) {
        1 -> 8
        2 -> 5
        3 -> 4
        else -> 3
    }

private val CardBackground = Color(0xFFFFFCF4)
private val CardTextColor = Color(0xFF28231D)

private val CardTextPanelColors = listOf(
    Color(0xFFF3E5C7),
    Color(0xFFD9EEE7),
    Color(0xFFFFD8C7),
    Color(0xFFE8E0FF),
)
