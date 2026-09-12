@file:Suppress("MagicNumber", "TooManyFunctions")

package de.kaserik.impulse.frontend.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.CardBackground
import de.kaserik.impulse.frontend.theme.CardTextColor
import de.kaserik.impulse.frontend.theme.CardTextPanelColors
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

@Composable
internal fun GameCard(
    kartentexte: List<GameKartentextUiModel>,
    cardInstanceId: Long,
    modifier: Modifier = Modifier,
    textPanelColors: List<Color> = emptyList(),
    idleEffectsEnabled: Boolean = true,
    interactionsEnabled: Boolean = true,
    hiddenCardTextIds: Set<Int> = emptySet(),
    developerMode: Boolean = false,
    onKartentextBoundsChanged: (Int, Rect) -> Unit = { _, _ -> },
    cardTextActions: CardTextActions = CardTextActions(),
) {
    val tooltipState = rememberPlayedTooltipState(cardInstanceId)
    val einzelnerKartentext = kartentexte.singleOrNull()

    CardIdlePlayedEffect(
        cardInstanceId = cardInstanceId,
        kartentext = einzelnerKartentext,
        enabled = idleEffectsEnabled && interactionsEnabled && einzelnerKartentext?.id !in hiddenCardTextIds,
        onKartentextPlayed = { kartentextId ->
            tooltipState.show()
            cardTextActions.onKartentextPlayedStateChanged(kartentextId, true)
        },
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = CardBackground,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f),
        ),
        tonalElevation = 4.dp,
        shadowElevation = 18.dp,
    ) {
        GameCardContent(
            kartentexte = kartentexte,
            textPanelColors = textPanelColors,
            tooltipVisible = tooltipState.visible,
            interactionsEnabled = interactionsEnabled,
            hiddenCardTextIds = hiddenCardTextIds,
            developerMode = developerMode,
            onKartentextBoundsChanged = onKartentextBoundsChanged,
            cardTextActions = cardTextActions,
            onKartentextMarkedAsPlayed = tooltipState::show,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameCardPreview() {
    ImpulseTheme {
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
    interactionsEnabled: Boolean = true,
    hiddenCardTextIds: Set<Int> = emptySet(),
    developerMode: Boolean = false,
    onKartentextBoundsChanged: (Int, Rect) -> Unit = { _, _ -> },
    cardTextActions: CardTextActions = CardTextActions(),
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
                    val panelModifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                    if (kartentext.id in hiddenCardTextIds) {
                        Box(
                            modifier = panelModifier.onGloballyPositioned { coordinates ->
                                onKartentextBoundsChanged(
                                    kartentext.id,
                                    coordinates.boundsInRoot(),
                                )
                            },
                        )
                    } else {
                        CardTextPanel(
                            kartentext = kartentext,
                            index = index,
                            kartentextCount = kartentexte.size,
                            textPanelColor = textPanelColors.getOrNull(index),
                            interactionsEnabled = interactionsEnabled,
                            developerMode = developerMode,
                            cardTextActions = cardTextActions,
                            onKartentextMarkedAsPlayed = onKartentextMarkedAsPlayed,
                            modifier = panelModifier.onGloballyPositioned { coordinates ->
                                onKartentextBoundsChanged(kartentext.id, coordinates.boundsInRoot())
                            },
                        )
                    }
                }
            }
        }

        PlayedTooltip(visible = tooltipVisible)
    }
}

@Preview(showBackground = true)
@Composable
private fun GameCardContentPreview() {
    ImpulseTheme {
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
                cardTextActions = CardTextActions(
                    onKartentextPlayedStateChanged = { _, _ -> },
                ),
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
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 6.dp,
        ) {
            Text(
                text = tooltipText,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge,
            )
        }
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
        delay(TOOLTIP_VISIBLE_DURATION_MILLIS.milliseconds)
        tooltipState.visible = false
    }

    return tooltipState
}

@Preview(showBackground = true)
@Composable
private fun RememberPlayedTooltipStatePreview() {
    ImpulseTheme {
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
@Suppress("LongMethod")
internal fun CardTextPanel(
    kartentext: GameKartentextUiModel,
    index: Int,
    kartentextCount: Int,
    modifier: Modifier = Modifier,
    textPanelColor: Color? = null,
    interactionsEnabled: Boolean = true,
    markerInteractionsEnabled: Boolean = interactionsEnabled,
    developerMode: Boolean = false,
    cardTextActions: CardTextActions = CardTextActions(),
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

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .clickable(enabled = interactionsEnabled) {
                val nextPlayedState = !kartentext.gespielt
                if (nextPlayedState) {
                    onKartentextMarkedAsPlayed()
                }
                cardTextActions.onKartentextManuallyPlayedStateChanged(
                    kartentext.id,
                    nextPlayedState,
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        val markerTouchSize = if (developerMode) {
            minOf(CardTextMarkerTouchSize, maxHeight / 2)
        } else {
            minOf(CardTextMarkerTouchSize, maxHeight)
        }
        val markerIconSize =
            minOf(CardTextMarkerIconSize, markerTouchSize * MARKER_ICON_SIZE_FRACTION)
        AutoShrinkText(
            text = kartentext.text,
            modifier = Modifier.padding(
                start = markerTouchSize + CardTextMarkerTextGap,
                top = if (kartentext.uebersetzungFehlt) {
                    markerTouchSize + CardTextMarkerTextGap
                } else {
                    CardTextPanelVerticalPadding
                },
                end = markerTouchSize + CardTextMarkerTextGap,
                bottom = 8.dp,
            ),
            style = textStyle.copy(color = CardTextColor),
        )

        Column(modifier = Modifier.align(Alignment.TopStart)) {
            BrokenHeartToggle(
                checked = kartentext.geloescht,
                enabled = markerInteractionsEnabled,
                backgroundColor = backgroundColor,
                touchSize = markerTouchSize,
                iconSize = markerIconSize,
                onCheckedChange = { checked ->
                    cardTextActions.onKartentextDeletedStateChanged(kartentext.id, checked)
                },
            )
            if (developerMode) {
                PencilToggle(
                    checked = kartentext.eigeneLokalisierung,
                    enabled = markerInteractionsEnabled,
                    touchSize = markerTouchSize,
                    iconSize = markerIconSize,
                    onClick = { cardTextActions.onKartentextEditRequested(kartentext.id) },
                )
            }
        }
        StarToggle(
            checked = kartentext.favorit,
            enabled = markerInteractionsEnabled,
            touchSize = markerTouchSize,
            iconSize = markerIconSize,
            onCheckedChange = { checked ->
                cardTextActions.onKartentextFavoriteStateChanged(kartentext.id, checked)
            },
            modifier = Modifier.align(Alignment.TopEnd),
        )
        if (kartentext.uebersetzungFehlt) {
            MissingTranslationMark(
                markerSize = markerTouchSize,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

@Composable
private fun BrokenHeartToggle(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = colorResource(R.color.transparent),
    touchSize: Dp = CardTextMarkerTouchSize,
    iconSize: Dp = CardTextMarkerIconSize,
) {
    val inkColor = colorResource(R.color.black)
    MarkerToggleButton(
        checked = checked,
        enabled = enabled,
        contentDescription = if (checked) stringResource(R.string.deleted) else stringResource(R.string.not_deleted),
        onCheckedChange = onCheckedChange,
        touchSize = touchSize,
        modifier = modifier,
    ) { marked ->
        CardTextMarkerIcon(
            marker = CardTextMarker.BROKEN_HEART,
            checked = marked,
            modifier = Modifier.size(iconSize),
            color = inkColor,
            backgroundColor = backgroundColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BrokenHeartTogglePreview() {
    ImpulseTheme {
        BrokenHeartToggle(checked = true, enabled = true, onCheckedChange = {})
    }
}

@Composable
private fun StarToggle(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    touchSize: Dp = CardTextMarkerTouchSize,
    iconSize: Dp = CardTextMarkerIconSize,
) {
    val color = MaterialTheme.colorScheme.primary
    MarkerToggleButton(
        checked = checked,
        enabled = enabled,
        contentDescription = stringResource(if (checked) R.string.favorited else R.string.not_favorited),
        onCheckedChange = onCheckedChange,
        touchSize = touchSize,
        modifier = modifier,
    ) { marked ->
        CardTextMarkerIcon(
            marker = CardTextMarker.STAR,
            checked = marked,
            modifier = Modifier.size(iconSize),
            color = color,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StarTogglePreview() {
    ImpulseTheme {
        StarToggle(checked = false, enabled = true, onCheckedChange = {})
    }
}

@Composable
private fun PencilToggle(
    checked: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    touchSize: Dp = CardTextMarkerTouchSize,
    iconSize: Dp = CardTextMarkerIconSize,
) {
    val inkColor = colorResource(R.color.black)
    MarkerToggleButton(
        checked = checked,
        enabled = enabled,
        contentDescription = stringResource(
            if (checked) R.string.own_card_text_available else R.string.edit_card_text,
        ),
        onCheckedChange = { onClick() },
        role = Role.Button,
        touchSize = touchSize,
        modifier = modifier,
    ) { marked ->
        CardTextMarkerIcon(
            marker = CardTextMarker.PENCIL,
            checked = marked,
            modifier = Modifier.size(iconSize),
            color = inkColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PencilTogglePreview() {
    ImpulseTheme {
        PencilToggle(checked = true, enabled = true, onClick = {})
    }
}

@Composable
private fun MarkerToggleButton(
    checked: Boolean,
    enabled: Boolean,
    contentDescription: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    role: Role = Role.Checkbox,
    touchSize: Dp = CardTextMarkerTouchSize,
    content: @Composable (Boolean) -> Unit,
) {
    Box(
        modifier = modifier
            .size(touchSize)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = role,
                onValueChange = onCheckedChange,
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        content(checked)
    }
}

@Preview(showBackground = true)
@Composable
private fun MarkerToggleButtonPreview() {
    ImpulseTheme {
        MarkerToggleButton(
            checked = false,
            enabled = true,
            contentDescription = stringResource(R.string.marker_description),
            onCheckedChange = {},
        ) { }
    }
}

@Composable
private fun MissingTranslationMark(
    modifier: Modifier = Modifier,
    markerSize: Dp = CardTextMarkerTouchSize,
) {
    val textColor = CardTextColor
    val accessibilityLabel = stringResource(R.string.missing_translation)
    Surface(
        modifier = modifier
            .size(markerSize)
            .semantics { contentDescription = accessibilityLabel },
        shape = CircleShape,
        color = colorResource(R.color.transparent),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.symbol_translation),
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = maxOf(
                        markerSize.value * MISSING_TRANSLATION_FONT_SIZE_FRACTION,
                        MIN_CARD_TEXT_FONT_SIZE_VALUE,
                    ).sp,
                ),
            )
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .padding(markerSize * MISSING_TRANSLATION_PADDING_FRACTION),
            ) {
                drawLine(
                    color = textColor,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height),
                    end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    strokeWidth = MarkerStrokeWidth.toPx(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MissingTranslationMarkPreview() {
    ImpulseTheme { MissingTranslationMark() }
}

@Preview(showBackground = true)
@Composable
private fun CardTextPanelPreview() {
    ImpulseTheme {
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
    ImpulseTheme {
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
        val textMeasurer = rememberTextMeasurer()
        val protectedText = remember(text) { protectCardTextLineStarts(text) }
        val fittedStyle = remember(protectedText, style, constraints, textMeasurer) {
            textMeasurer.fitCardTextStyle(
                text = protectedText,
                style = style,
                constraints = Constraints(
                    maxWidth = constraints.maxWidth,
                    maxHeight = constraints.maxHeight,
                ),
            )
        }

        Text(
            text = protectedText,
            modifier = Modifier.fillMaxWidth(),
            color = fittedStyle.color,
            textAlign = TextAlign.Center,
            softWrap = true,
            overflow = TextOverflow.Clip,
            style = fittedStyle,
        )
    }
}

private fun TextMeasurer.fitCardTextStyle(
    text: String,
    style: TextStyle,
    constraints: Constraints,
): TextStyle {
    val layoutStyle = style.copy(
        lineBreak = LineBreak.Simple,
        hyphens = Hyphens.None,
    )
    if (!layoutStyle.fontSize.isSpecified || fits(text, layoutStyle, constraints)) {
        return layoutStyle
    }

    val maximumFontSize = layoutStyle.fontSize.value
    var minimumShrinkSteps = 1
    var maximumShrinkSteps = MAXIMUM_FONT_SIZE_SHRINK_STEPS
    while (minimumShrinkSteps < maximumShrinkSteps) {
        val candidateShrinkSteps = (minimumShrinkSteps + maximumShrinkSteps) / 2
        val candidateFontSize = maximumFontSize.shrunk(candidateShrinkSteps)
        val candidateStyle = layoutStyle.scaleTo(candidateFontSize.sp)
        if (fits(text, candidateStyle, constraints)) {
            maximumShrinkSteps = candidateShrinkSteps
        } else {
            minimumShrinkSteps = candidateShrinkSteps + 1
        }
    }
    return layoutStyle.scaleTo(maximumFontSize.shrunk(minimumShrinkSteps).sp)
}

private fun Float.shrunk(steps: Int): Float =
    maxOf(
        this * SHRINK_FACTOR.pow(steps),
        MIN_CARD_TEXT_FONT_SIZE_VALUE,
    )

private fun TextMeasurer.fits(
    text: String,
    style: TextStyle,
    constraints: Constraints,
): Boolean {
    val result = measure(
        text = AnnotatedString(text),
        style = style,
        overflow = TextOverflow.Clip,
        softWrap = true,
        constraints = constraints,
    )
    if (result.didOverflowHeight || result.didOverflowWidth) {
        return false
    }

    return unbrokenCardTextSegments(text).all { segment ->
        measure(
            text = AnnotatedString(segment),
            style = style,
            overflow = TextOverflow.Visible,
            softWrap = false,
        ).size.width <= constraints.maxWidth
    }
}

internal fun protectCardTextLineStarts(text: String): String {
    val result = StringBuilder(text.length)
    var lineStart = 0
    while (lineStart < text.length) {
        val lineEnd = text.indexOf('\n', startIndex = lineStart).let { index ->
            if (index < 0) text.length else index
        }
        text.appendProtectedLineStart(
            result = result,
            lineStart = lineStart,
            lineEnd = lineEnd,
        )
        if (lineEnd < text.length) result.append('\n')
        lineStart = lineEnd + 1
    }
    return result.toString()
}

private fun String.appendProtectedLineStart(
    result: StringBuilder,
    lineStart: Int,
    lineEnd: Int,
) {
    val protectedEnd = protectedLineStartEnd(lineStart, lineEnd)
    for (index in lineStart until lineEnd) {
        val character = if (index < protectedEnd && this[index].isWhitespace()) {
            NON_BREAKING_SPACE
        } else {
            this[index]
        }
        result.append(character)
        if (index + 1 < protectedEnd && !splitsGraphemeClusterAfter(index)) result.append(
            WORD_JOINER
        )
    }
}

private fun String.protectedLineStartEnd(
    lineStart: Int,
    lineEnd: Int,
): Int {
    if (lineStart >= lineEnd || isLetterAt(lineStart)) return lineStart

    var firstLetter = lineStart
    while (firstLetter < lineEnd && !isLetterAt(firstLetter)) {
        firstLetter += Character.charCount(codePointAt(firstLetter))
    }
    var protectedEnd = firstLetter
    while (protectedEnd < lineEnd && !Character.isWhitespace(codePointAt(protectedEnd))) {
        protectedEnd += Character.charCount(codePointAt(protectedEnd))
    }
    return if (firstLetter < lineEnd) protectedEnd else lineStart
}

private fun String.isLetterAt(index: Int): Boolean = Character.isLetter(codePointAt(index))

private fun String.splitsGraphemeClusterAfter(index: Int): Boolean {
    val nextIndex = index + 1
    return when {
        nextIndex >= length -> false
        this[index].isHighSurrogate() && this[nextIndex].isLowSurrogate() -> true
        else -> {
            val previousCodePoint = codePointBefore(nextIndex)
            val nextCodePoint = codePointAt(nextIndex)
            nextCodePoint.isGraphemeContinuation() ||
                    previousCodePoint == ZERO_WIDTH_JOINER_CODE_POINT ||
                    previousCodePoint.isRegionalIndicator() && nextCodePoint.isRegionalIndicator()
        }
    }
}

private fun Int.isGraphemeContinuation(): Boolean =
    when (Character.getType(this)) {
        Character.NON_SPACING_MARK.toInt(),
        Character.COMBINING_SPACING_MARK.toInt(),
        Character.ENCLOSING_MARK.toInt(),
            -> true

        else -> this == ZERO_WIDTH_JOINER_CODE_POINT ||
                this in VARIATION_SELECTOR_RANGE ||
                this in SUPPLEMENTARY_VARIATION_SELECTOR_RANGE ||
                this in EMOJI_MODIFIER_RANGE ||
                this in EMOJI_TAG_RANGE
    }

private fun Int.isRegionalIndicator(): Boolean = this in REGIONAL_INDICATOR_RANGE

internal fun unbrokenCardTextSegments(text: String): List<String> {
    val segments = mutableListOf<String>()
    var segmentStart = 0
    text.forEachIndexed { index, character ->
        if (character.isWhitespace() && character != NON_BREAKING_SPACE) {
            if (segmentStart < index) segments += text.substring(segmentStart, index)
            segmentStart = index + 1
        }
    }
    if (segmentStart < text.length) segments += text.substring(segmentStart)
    return segments
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

private val CardTextMarkerTouchSize = 30.dp
private val CardTextMarkerIconSize = 17.dp
private val CardTextMarkerTextGap = 4.dp
private val CardTextPanelVerticalPadding = 8.dp
private const val MARKER_ICON_SIZE_FRACTION = 0.57f
private const val MISSING_TRANSLATION_FONT_SIZE_FRACTION = 0.32f
private const val MISSING_TRANSLATION_PADDING_FRACTION = 0.23f
private const val NON_BREAKING_SPACE = '\u00A0'
private const val WORD_JOINER = '\u2060'
private const val ZERO_WIDTH_JOINER_CODE_POINT = 0x200D
private val VARIATION_SELECTOR_RANGE = 0xFE00..0xFE0F
private val SUPPLEMENTARY_VARIATION_SELECTOR_RANGE = 0xE0100..0xE01EF
private val EMOJI_MODIFIER_RANGE = 0x1F3FB..0x1F3FF
private val EMOJI_TAG_RANGE = 0xE0020..0xE007F
private val REGIONAL_INDICATOR_RANGE = 0x1F1E6..0x1F1FF
private const val MIN_CARD_TEXT_FONT_SIZE_VALUE = 0.01f
private const val SHRINK_FACTOR = 0.92f
private const val MAXIMUM_FONT_SIZE_SHRINK_STEPS = 128
private const val PLAYED_DARKEN_FACTOR = 0.62f
private const val TOOLTIP_VISIBLE_DURATION_MILLIS = 500L


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
