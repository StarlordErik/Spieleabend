@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
internal fun CategoryTabs(
    kategorien: List<GameKategorieUiModel>,
    modifier: Modifier = Modifier,
    onKategorieSelected: (Int) -> Unit = {},
    onRandomSelected: () -> Unit = {},
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val randomTabHeight = (layoutHeight * RANDOM_CATEGORY_TAB_HEIGHT_FRACTION)
            .roundToInt()
            .coerceAtMost(layoutHeight)
        val tabSpacing = CategoryTabSpacing.roundToPx()
        val normalTabHeightPx = min(
            rightCategoryTabHeight(
                tabCount = kategorien.size,
                layoutHeight = layoutHeight,
                spacing = tabSpacing,
            ),
            randomTabHeight,
        )
        val normalTabHeight = normalTabHeightPx.toDp()
        val measuredTabs = measureCategoryTabs(
            kategorien = kategorien,
            constraints = constraints,
            randomTabHeight = randomTabHeight.toDp(),
            normalTabHeight = normalTabHeight,
            onKategorieSelected = onKategorieSelected,
            onRandomSelected = onRandomSelected,
        )

        val previousTabY = (layoutHeight - measuredTabs.previousTab.height - tabSpacing)
            .coerceAtLeast(0)
        val randomTabY = ((layoutHeight - measuredTabs.randomTab.height) / 2)
            .coerceAtLeast(0)
        val normalTabsTotalHeight = measuredTabs.normalTabs.sumOf { tab -> tab.placeable.height } +
            (measuredTabs.normalTabs.size - 1).coerceAtLeast(0) * tabSpacing
        val centeredNormalTabsY = ((layoutHeight - normalTabsTotalHeight) / 2).coerceAtLeast(0)

        layout(width = layoutWidth, height = layoutHeight) {
            var nextNormalTabY = centeredNormalTabsY
            measuredTabs.normalTabs.forEach { tab ->
                tab.placeable.placeRelative(
                    x = when (tab.side) {
                        CategoryTabSide.Left -> 0
                        CategoryTabSide.Right -> layoutWidth - tab.placeable.width
                    },
                    y = nextNormalTabY,
                )
                nextNormalTabY += tab.placeable.height + tabSpacing
            }
            measuredTabs.previousTab.placeRelative(x = 0, y = previousTabY)
            measuredTabs.randomTab.placeRelative(
                x = 0,
                y = randomTabY,
            )
        }
    }
}

private fun rightCategoryTabHeight(
    tabCount: Int,
    layoutHeight: Int,
    spacing: Int,
): Int {
    if (tabCount == 0) {
        return 0
    }

    val totalSpacing = spacing * (tabCount + 1)
    return (layoutHeight - totalSpacing).coerceAtLeast(0) / tabCount
}

internal fun categoryTabColor(index: Int): Color =
    CategoryTabColors[index % CategoryTabColors.size]

@Preview(showBackground = true)
@Composable
private fun CategoryTabsPreview() {
    SpieleabendTheme {
        CategoryTabs(
            kategorien = PreviewUiState.kategorien,
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp),
        )
    }
}

@Composable
private fun CategoryTab(
    tab: GameKategorieUiModel,
    side: CategoryTabSide,
    color: Color,
    modifier: Modifier = Modifier,
    fixedHeight: Dp? = null,
    onClick: (() -> Unit)? = null,
) {
    val labelText = tab.name.uppercase()
    val baseTextStyle = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
    )
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val measuredTextWidthPx = textMeasurer.measure(
        text = AnnotatedString(labelText),
        style = baseTextStyle,
        maxLines = 1,
    ).size.width
    val tabHeight = fixedHeight
        ?: dynamicCategoryTabHeight(
            measuredTextWidthPx = measuredTextWidthPx,
            density = density,
        )
    val fittedLabel = fittedCategoryTabLabel(
        text = labelText,
        baseTextStyle = baseTextStyle,
        textMeasurer = textMeasurer,
        density = density,
        tabHeight = tabHeight,
        measuredTextWidthPx = measuredTextWidthPx,
    )

    Box(
        modifier = (if (onClick == null) modifier else modifier.clickable(onClick = onClick))
            .width(CategoryTabWidth)
            .height(tabHeight)
            .clip(categoryTabShape(side))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = labelText,
            modifier = Modifier
                .requiredWidth(fittedLabel.width)
                .graphicsLayer(rotationZ = side.labelRotation),
            color = CategoryTabContentColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            softWrap = false,
            style = fittedLabel.style,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryTabPreview() {
    SpieleabendTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
        ) {
            CategoryTab(
                tab = PreviewUiState.kategorien.first(),
                side = CategoryTabSide.Left,
                color = CategoryTabColors.first(),
            )
            CategoryTab(
                tab = GameKategorieUiModel(
                    id = -1,
                    name = "Vorherige Karte",
                ),
                side = CategoryTabSide.Right,
                color = CategoryTabColors.last(),
            )
        }
    }
}

private fun dynamicCategoryTabHeight(
    measuredTextWidthPx: Int,
    density: Density,
): Dp =
    with(density) {
        (
            measuredTextWidthPx.toDp() +
                CategoryTabTextExtraWidth +
                CategoryTabLabelVerticalPadding * 2
            ).coerceAtLeast(MinimumCategoryTabHeight)
    }

private fun fittedCategoryTabLabel(
    text: String,
    baseTextStyle: TextStyle,
    textMeasurer: TextMeasurer,
    density: Density,
    tabHeight: Dp,
    measuredTextWidthPx: Int,
): FittedCategoryTabLabel =
    with(density) {
        val tabHeightPx = tabHeight.roundToPx()
        val verticalPaddingPx = CategoryTabLabelVerticalPadding
            .roundToPx()
            .coerceAtMost(tabHeightPx / 2)
        val extraWidthPx = CategoryTabTextExtraWidth.roundToPx()
        val availableTextWidthPx = (tabHeightPx - verticalPaddingPx * 2 - extraWidthPx)
            .coerceAtLeast(1)
        val scale = if (measuredTextWidthPx > availableTextWidthPx) {
            availableTextWidthPx.toFloat() / measuredTextWidthPx
        } else {
            1f
        }
        val fittedTextStyle = baseTextStyle.copy(
            fontSize = (baseTextStyle.fontSize.value * scale).sp,
        )
        val fittedTextWidthPx = textMeasurer.measure(
            text = AnnotatedString(text),
            style = fittedTextStyle,
            maxLines = 1,
        ).size.width
        val maxLabelWidthPx = (tabHeightPx - verticalPaddingPx * 2)
            .coerceAtLeast(extraWidthPx)

        FittedCategoryTabLabel(
            style = fittedTextStyle,
            width = min(fittedTextWidthPx + extraWidthPx, maxLabelWidthPx).toDp(),
        )
    }

private fun SubcomposeMeasureScope.measureCategoryTabs(
    kategorien: List<GameKategorieUiModel>,
    constraints: Constraints,
    randomTabHeight: Dp,
    normalTabHeight: Dp,
    onKategorieSelected: (Int) -> Unit,
    onRandomSelected: () -> Unit,
): MeasuredCategoryTabs {
    val measureConstraints = constraints.copy(minWidth = 0, minHeight = 0)
    val previousTab = subcompose(CategoryTabSlot.PreviousCard) {
        CategoryTab(
            tab = PreviousCardTab,
            side = CategoryTabSide.Left,
            color = PreviousCardTabColor,
        )
    }.single().measure(measureConstraints)
    val randomTab = subcompose(CategoryTabSlot.Random) {
        CategoryTab(
            tab = RandomTab,
            side = CategoryTabSide.Left,
            color = RandomTabColor,
            fixedHeight = randomTabHeight,
            onClick = onRandomSelected,
        )
    }.single().measure(measureConstraints)
    val normalTabs = kategorien.mapIndexed { index, tab ->
        val side = CategoryTabSide.Right
        val color = categoryTabColor(index)
        MeasuredCategoryTab(
            side = side,
            placeable = subcompose(NormalCategoryTabSlot(index = index, id = tab.id)) {
                CategoryTab(
                    tab = tab,
                    side = side,
                    color = color,
                    fixedHeight = normalTabHeight,
                    onClick = { onKategorieSelected(tab.id) },
                )
            }.single().measure(measureConstraints),
        )
    }

    return MeasuredCategoryTabs(
        previousTab = previousTab,
        randomTab = randomTab,
        normalTabs = normalTabs,
    )
}

private fun categoryTabShape(side: CategoryTabSide): Shape =
    when (side) {
        CategoryTabSide.Left -> RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
        CategoryTabSide.Right -> RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
    }

private enum class CategoryTabSide(
    val labelRotation: Float,
) {
    Left(labelRotation = -90f),
    Right(labelRotation = 90f),
}

private enum class CategoryTabSlot {
    PreviousCard,
    Random,
}

private data class NormalCategoryTabSlot(
    val index: Int,
    val id: Int,
)

private data class MeasuredCategoryTabs(
    val previousTab: Placeable,
    val randomTab: Placeable,
    val normalTabs: List<MeasuredCategoryTab>,
)

private data class MeasuredCategoryTab(
    val side: CategoryTabSide,
    val placeable: Placeable,
)

private data class FittedCategoryTabLabel(
    val style: TextStyle,
    val width: Dp,
)

private val CategoryTabContentColor = Color.White
private val CategoryTabWidth = 24.dp
private val MinimumCategoryTabHeight = 64.dp
private val CategoryTabSpacing = 10.dp
private val CategoryTabLabelVerticalPadding = 14.dp
private val CategoryTabTextExtraWidth = 4.dp
private const val RANDOM_CATEGORY_TAB_HEIGHT_FRACTION = 1f / 3f

private val RandomTab = GameKategorieUiModel(
    id = -2,
    name = "Zuf\u00e4llig",
)
private val PreviousCardTab = GameKategorieUiModel(
    id = -1,
    name = "Vorherige Karte",
)
private val RandomTabColor = Color(0xFF1F2937)
private val PreviousCardTabColor = Color(0xFF5F6268)

private val CategoryTabColors = listOf(
    Color(0xFF2A7F62),
    Color(0xFFC84C31),
    Color(0xFF276A8A),
    Color(0xFF8B5A96),
    Color(0xFFB68A1F),
)
