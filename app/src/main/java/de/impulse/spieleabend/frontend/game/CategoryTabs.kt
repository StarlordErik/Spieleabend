@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.game

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
internal fun CategoryTabs(
    kategorien: List<GameKategorieUiModel>,
    modifier: Modifier = Modifier,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val randomTabHeight = (layoutHeight * RANDOM_CATEGORY_TAB_HEIGHT_FRACTION)
            .roundToInt()
            .coerceAtMost(layoutHeight)
        val measuredTabs = measureCategoryTabs(
            kategorien = kategorien,
            constraints = constraints,
            randomTabHeight = randomTabHeight.toDp(),
        )

        val tabSpacing = CategoryTabSpacing.roundToPx()
        val previousTabY = (layoutHeight - measuredTabs.previousTab.height - tabSpacing)
            .coerceAtLeast(0)
        val randomTabY = ((layoutHeight - measuredTabs.randomTab.height) / 2)
            .coerceAtLeast(0)
        val rightTabPlacements = positionNormalTabs(
            tabs = measuredTabs.normalTabs,
            layoutHeight = layoutHeight,
            spacing = tabSpacing,
            blockedRanges = emptyList(),
        )

        layout(width = layoutWidth, height = layoutHeight) {
            rightTabPlacements.forEach { tabPlacement ->
                tabPlacement.tab.placeable.placeRelative(
                    x = when (tabPlacement.tab.side) {
                        CategoryTabSide.Left -> 0
                        CategoryTabSide.Right -> layoutWidth - tabPlacement.tab.placeable.width
                    },
                    y = tabPlacement.y,
                )
            }
            measuredTabs.previousTab.placeRelative(x = 0, y = previousTabY)
            measuredTabs.randomTab.placeRelative(
                x = 0,
                y = randomTabY,
            )
        }
    }
}

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
) {
    val labelText = tab.name.uppercase()
    val textStyle = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
    )
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val labelWidth = with(density) {
        textMeasurer.measure(
            text = AnnotatedString(labelText),
            style = textStyle,
            maxLines = 1,
        ).size.width.toDp() + CategoryTabTextExtraWidth
    }
    val tabHeight = maxOf(
        labelWidth + CategoryTabLabelVerticalPadding * 2,
        fixedHeight ?: MinimumCategoryTabHeight,
        MinimumCategoryTabHeight,
    )

    Box(
        modifier = modifier
            .width(CategoryTabWidth)
            .height(tabHeight)
            .clip(categoryTabShape(side))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = labelText,
            modifier = Modifier
                .requiredWidth(labelWidth)
                .graphicsLayer(rotationZ = side.labelRotation),
            color = CategoryTabContentColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            softWrap = false,
            style = textStyle,
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
                tab = GameKategorieUiModel(id = "vorherige-karte", name = "Vorherige Karte"),
                side = CategoryTabSide.Right,
                color = CategoryTabColors.last(),
            )
        }
    }
}

private fun SubcomposeMeasureScope.measureCategoryTabs(
    kategorien: List<GameKategorieUiModel>,
    constraints: Constraints,
    randomTabHeight: Dp,
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
        )
    }.single().measure(measureConstraints)
    val normalTabs = kategorien.mapIndexed { index, tab ->
        val side = CategoryTabSide.Right
        MeasuredCategoryTab(
            side = side,
            placeable = subcompose(NormalCategoryTabSlot(index = index, id = tab.id)) {
                CategoryTab(
                    tab = tab,
                    side = side,
                    color = CategoryTabColors[index % CategoryTabColors.size],
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

private fun positionNormalTabs(
    tabs: List<MeasuredCategoryTab>,
    layoutHeight: Int,
    spacing: Int,
    blockedRanges: List<VerticalRange>,
): List<CategoryTabPlacement> {
    val occupiedRanges = blockedRanges.toMutableList()
    return preferredCenteredPlacements(
        tabs = tabs,
        layoutHeight = layoutHeight,
        spacing = spacing,
    )
        .sortedWith(
            compareBy<PreferredCategoryTabPlacement> { placement ->
                abs(placement.center - layoutHeight / 2)
            }.thenBy { placement -> placement.preferredTop },
        )
        .map { preferredPlacement ->
            val y = nearestAvailableTop(
                preferredTop = preferredPlacement.preferredTop,
                tabHeight = preferredPlacement.tab.placeable.height,
                layoutHeight = layoutHeight,
                spacing = spacing,
                occupiedRanges = occupiedRanges,
            )
            occupiedRanges += VerticalRange(
                start = y,
                end = y + preferredPlacement.tab.placeable.height,
            )
            CategoryTabPlacement(
                tab = preferredPlacement.tab,
                y = y,
            )
        }
}

private fun preferredCenteredPlacements(
    tabs: List<MeasuredCategoryTab>,
    layoutHeight: Int,
    spacing: Int,
): List<PreferredCategoryTabPlacement> {
    val totalTabHeight = tabs.sumOf { tab -> tab.placeable.height }
    val totalSpacing = spacing * (tabs.size - 1).coerceAtLeast(0)
    var nextTop = (layoutHeight - totalTabHeight - totalSpacing) / 2

    return tabs.map { tab ->
        val placement = PreferredCategoryTabPlacement(
            tab = tab,
            preferredTop = nextTop,
        )
        nextTop += tab.placeable.height + spacing
        placement
    }
}

private fun nearestAvailableTop(
    preferredTop: Int,
    tabHeight: Int,
    layoutHeight: Int,
    spacing: Int,
    occupiedRanges: List<VerticalRange>,
): Int {
    val lastTop = (layoutHeight - tabHeight).coerceAtLeast(0)
    val boundedPreferredTop = preferredTop.coerceIn(0, lastTop)
    val freeRanges = freeRangesOutside(
        occupiedRanges = occupiedRanges,
        spacing = spacing,
        layoutHeight = layoutHeight,
    )

    return freeRanges
        .asSequence()
        .filter { range -> range.length >= tabHeight }
        .map { range -> boundedPreferredTop.coerceIn(range.start, range.end - tabHeight) }
        .minWithOrNull(
            compareBy<Int> { top -> abs(top - boundedPreferredTop) }
                .thenBy { top -> abs(top + tabHeight / 2 - layoutHeight / 2) }
                .thenBy { top -> top },
        )
        ?: boundedPreferredTop
}

private fun freeRangesOutside(
    occupiedRanges: List<VerticalRange>,
    spacing: Int,
    layoutHeight: Int,
): List<VerticalRange> {
    if (occupiedRanges.isEmpty()) {
        return listOf(VerticalRange(start = 0, end = layoutHeight))
    }

    val expandedOccupiedRanges = occupiedRanges
        .map { range ->
            VerticalRange(
                start = (range.start - spacing).coerceAtLeast(0),
                end = (range.end + spacing).coerceAtMost(layoutHeight),
            )
        }
        .sortedBy { range -> range.start }

    val mergedOccupiedRanges = mutableListOf<VerticalRange>()
    var currentRange = expandedOccupiedRanges.first()
    expandedOccupiedRanges
        .drop(1)
        .forEach { range ->
            if (range.start <= currentRange.end) {
                currentRange = currentRange.copy(
                    end = max(currentRange.end, range.end),
                )
            } else {
                mergedOccupiedRanges += currentRange
                currentRange = range
            }
        }
    mergedOccupiedRanges += currentRange
    val freeRanges = mutableListOf<VerticalRange>()
    var nextStart = 0

    mergedOccupiedRanges.forEach { range ->
        if (nextStart < range.start) {
            freeRanges += VerticalRange(start = nextStart, end = range.start)
        }
        nextStart = max(nextStart, range.end)
    }

    if (nextStart < layoutHeight) {
        freeRanges += VerticalRange(start = nextStart, end = layoutHeight)
    }

    return freeRanges
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
    val id: String,
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

private data class PreferredCategoryTabPlacement(
    val tab: MeasuredCategoryTab,
    val preferredTop: Int,
) {
    val center: Int = preferredTop + tab.placeable.height / 2
}

private data class CategoryTabPlacement(
    val tab: MeasuredCategoryTab,
    val y: Int,
)

private data class VerticalRange(
    val start: Int,
    val end: Int,
) {
    val length: Int = end - start
}

private val CategoryTabContentColor = Color.White
private val CategoryTabWidth = 36.dp
private val MinimumCategoryTabHeight = 64.dp
private val CategoryTabSpacing = 10.dp
private val CategoryTabLabelVerticalPadding = 14.dp
private val CategoryTabTextExtraWidth = 4.dp
private const val RANDOM_CATEGORY_TAB_HEIGHT_FRACTION = 1f / 3f

private val RandomTab = GameKategorieUiModel(id = "random", name = "Zuf\u00e4llig")
private val PreviousCardTab = GameKategorieUiModel(id = "previous-card", name = "Vorherige Karte")
private val RandomTabColor = Color(0xFF2A7F62)
private val PreviousCardTabColor = Color(0xFF5F6268)

private val CategoryTabColors = listOf(
    Color(0xFF2A7F62),
    Color(0xFFC84C31),
    Color(0xFF276A8A),
    Color(0xFF8B5A96),
)
