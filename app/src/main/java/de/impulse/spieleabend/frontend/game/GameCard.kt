@file:Suppress("MagicNumber", "TooManyFunctions")

package de.impulse.spieleabend.frontend.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.isSpecified
import de.impulse.spieleabend.R
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme
import kotlinx.coroutines.delay

@Composable
internal fun GameCard(
    kartentexte: List<GameKartentextUiModel>,
    cardInstanceId: Long,
    modifier: Modifier = Modifier,
    textPanelColors: List<Color> = emptyList(),
    onKartentextPlayedStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
) {
    val tooltipState = rememberPlayedTooltipState(cardInstanceId)
    val einzelnerKartentext = kartentexte.singleOrNull()

    CardIdlePlayedEffect(
        cardInstanceId = cardInstanceId,
        kartentext = einzelnerKartentext,
        onKartentextPlayed = { kartentextId ->
            tooltipState.show()
            onKartentextPlayedStateChanged(
                kartentextId,
                true,
            )
        },
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = CardBackground,
        shadowElevation = 12.dp,
    ) {
        GameCardContent(
            kartentexte = kartentexte,
            textPanelColors = textPanelColors,
            tooltipVisible = tooltipState.visible,
            onKartentextPlayedStateChanged = onKartentextPlayedStateChanged,
            onKartentextMarkedAsPlayed = tooltipState::show,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameCardPreview() {
    SpieleabendTheme {
        GameCard(
            kartentexte = PreviewUiState.aktuelleKarte.kartentexte,
            cardInstanceId = PreviewUiState.aktuelleKarte.instanceId,
            modifier = Modifier
                .width(320.dp)
                .height(520.dp)
                .padding(24.dp),
        )
    }
}

@Composable
private fun GameCardContent(
    kartentexte: List<GameKartentextUiModel>,
    textPanelColors: List<Color>,
    tooltipVisible: Boolean,
    onKartentextPlayedStateChanged: (Int, Boolean) -> Unit,
    onKartentextMarkedAsPlayed: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(9.dp),
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
                        onKartentextPlayedStateChanged = onKartentextPlayedStateChanged,
                        onKartentextMarkedAsPlayed = onKartentextMarkedAsPlayed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                }
            }
        }

        PlayedTooltip(visible = tooltipVisible)
    }
}

@Preview(showBackground = true)
@Composable
private fun GameCardContentPreview() {
    SpieleabendTheme {
        Surface(
            modifier = Modifier
                .width(320.dp)
                .height(520.dp)
                .padding(24.dp),
        ) {
            GameCardContent(
                kartentexte = PreviewUiState.aktuelleKarte.kartentexte,
                textPanelColors = emptyList(),
                tooltipVisible = true,
                onKartentextPlayedStateChanged = { _, _ -> },
                onKartentextMarkedAsPlayed = {},
            )
        }
    }
}

@Composable
private fun BoxScope.PlayedTooltip(visible: Boolean) {
    val tooltipText = stringResource(R.string.card_text_marked_as_played)

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(y = (-20).dp),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = TooltipBackground,
            shadowElevation = 6.dp,
        ) {
            Text(
                text = tooltipText,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = TooltipTextColor,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun CardIdlePlayedEffect(
    cardInstanceId: Long,
    kartentext: GameKartentextUiModel?,
    onKartentextPlayed: (Int) -> Unit,
) {
    LaunchedEffect(cardInstanceId, kartentext?.id, kartentext?.gespielt) {
        if (kartentext == null || kartentext.gespielt) {
            return@LaunchedEffect
        }

        delay(SINGLE_CARD_IDLE_PLAY_DELAY_MILLIS)
        onKartentextPlayed(kartentext.id)
    }
}

@Preview(showBackground = true)
@Composable
private fun CardIdlePlayedEffectPreview() {
    SpieleabendTheme {
        CardIdlePlayedEffect(
            cardInstanceId = 1,
            kartentext = null,
            onKartentextPlayed = {},
        )
        Box(
            modifier = Modifier
                .width(320.dp)
                .height(124.dp)
                .padding(24.dp),
        )
    }
}

@Composable
private fun rememberPlayedTooltipState(cardInstanceId: Long): PlayedTooltipState {
    val tooltipState = remember(cardInstanceId) { PlayedTooltipState() }

    LaunchedEffect(cardInstanceId, tooltipState.triggerCount) {
        if (tooltipState.triggerCount == 0) {
            return@LaunchedEffect
        }

        tooltipState.visible = true
        delay(TOOLTIP_VISIBLE_DURATION_MILLIS)
        tooltipState.visible = false
    }

    return tooltipState
}

@Preview(showBackground = true)
@Composable
@Suppress("ComposableNaming")
private fun rememberPlayedTooltipStatePreview() {
    SpieleabendTheme {
        val tooltipState = rememberPlayedTooltipState(cardInstanceId = 1)
        Box(
            modifier = Modifier
                .width(320.dp)
                .height(124.dp)
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = tooltipState.visible.toString(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Stable
private class PlayedTooltipState {
    var visible by mutableStateOf(false)
    var triggerCount by mutableIntStateOf(0)

    fun show() {
        triggerCount++
    }
}

@Composable
private fun CardTextPanel(
    kartentext: GameKartentextUiModel,
    index: Int,
    kartentextCount: Int,
    modifier: Modifier = Modifier,
    textPanelColor: Color? = null,
    onKartentextPlayedStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
    onKartentextMarkedAsPlayed: () -> Unit = {},
) {
    val textStyle = when (kartentextCount) {
        1 -> MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold)
        2 -> MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold)
        3 -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
        else -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
    }
    val backgroundColor =
        (textPanelColor ?: CardTextPanelColors[index % CardTextPanelColors.size])
            .darkenedIfPlayed(kartentext.gespielt)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .clickable {
                val nextPlayedState = !kartentext.gespielt
                if (nextPlayedState) {
                    onKartentextMarkedAsPlayed()
                }
                onKartentextPlayedStateChanged(
                    kartentext.id,
                    nextPlayedState,
                )
            }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        AutoShrinkText(
            text = kartentext.text,
            style = textStyle.copy(color = CardTextColor),
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

@Preview(showBackground = true)
@Composable
private fun AutoShrinkTextPreview() {
    SpieleabendTheme {
        Box(
            modifier = Modifier
                .width(320.dp)
                .height(124.dp)
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            AutoShrinkText(
                text = PreviewUiState.aktuelleKarte.kartentexte.first().text,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun AutoShrinkText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        var scaledStyle by remember(text, style, maxWidth, maxHeight) {
            mutableStateOf(style)
        }
        var readyToDraw by remember(text, style, maxWidth, maxHeight) {
            mutableStateOf(false)
        }

        Text(
            text = text,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                    if (readyToDraw) {
                        drawContent()
                    }
                },
            color = scaledStyle.color,
            textAlign = TextAlign.Center,
            softWrap = true,
            overflow = TextOverflow.Clip,
            style =
                scaledStyle.copy(
                    lineBreak = LineBreak.Simple,
                    hyphens = Hyphens.None,
                ),
            onTextLayout = { layoutResult ->
                if (layoutResult.didOverflowHeight || layoutResult.didOverflowWidth) {
                    val nextFontSize =
                        maxOf(
                            scaledStyle.fontSize.value * SHRINK_FACTOR,
                            MIN_CARD_TEXT_FONT_SIZE.value,
                        ).sp
                    if (nextFontSize < scaledStyle.fontSize) {
                        scaledStyle = scaledStyle.scaleTo(nextFontSize)
                        return@Text
                    }
                }
                readyToDraw = true
            },
        )
    }
}

private fun TextStyle.scaleTo(fontSize: androidx.compose.ui.unit.TextUnit): TextStyle =
    copy(
        fontSize = fontSize,
        lineHeight =
            if (lineHeight.isSpecified && this.fontSize.isSpecified && this.fontSize.value != 0f) {
                lineHeight * (fontSize.value / this.fontSize.value)
            } else {
                lineHeight
            },
    )

private val CardBackground = Color(0xFFFFFCF4)
private val CardTextColor = Color(0xFF28231D)
private val TooltipBackground = Color(0xE628231D)
private val TooltipTextColor = Color(0xFFFFFCF4)
private val MIN_CARD_TEXT_FONT_SIZE = 10.sp
private const val SHRINK_FACTOR = 0.92f
private const val PLAYED_DARKEN_FACTOR = 0.5f
private const val SINGLE_CARD_IDLE_PLAY_DELAY_MILLIS = 15_000L
private const val TOOLTIP_VISIBLE_DURATION_MILLIS = 500L

private val CardTextPanelColors = listOf(
    Color(0xFFF3E5C7),
    Color(0xFFD9EEE7),
    Color(0xFFFFD8C7),
    Color(0xFFE8E0FF),
)

private fun Color.darkenedIfPlayed(gespielt: Boolean): Color =
    if (gespielt) {
        copy(
            red = red * PLAYED_DARKEN_FACTOR,
            green = green * PLAYED_DARKEN_FACTOR,
            blue = blue * PLAYED_DARKEN_FACTOR,
        )
    } else {
        this
    }
