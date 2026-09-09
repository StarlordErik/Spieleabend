@file:Suppress("MagicNumber")

package de.kaserik.impulse.frontend.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
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
import de.kaserik.impulse.R
import de.kaserik.impulse.common.AnimationLabels
import de.kaserik.impulse.frontend.theme.CategoryTabColors
import de.kaserik.impulse.frontend.theme.CategoryTabContentColor
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import de.kaserik.impulse.frontend.theme.PreviousCardTabColor
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
@Suppress("LongMethod")
internal fun CategoryTabs(
    kategorien: List<GameKategorieUiModel>,
    modifier: Modifier = Modifier,
    state: CategoryTabsState = CategoryTabsState(),
    actions: CategoryTabsActions = CategoryTabsActions(),
) {
    val categoryColors = CategoryTabColors
    val randomTabColor = MaterialTheme.colorScheme.onBackground
    val randomTabContentColor = MaterialTheme.colorScheme.background

    SubcomposeLayout(modifier = modifier) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val randomTabHeight = (layoutHeight * RANDOM_CATEGORY_TAB_HEIGHT_FRACTION)
            .roundToInt()
            .coerceAtMost(layoutHeight)
        val tabSpacing = CategoryTabSpacing.roundToPx()
        val normalTabHeightPx = min(
            categoryTabHeight(
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
            state = state,
            categoryColors = categoryColors,
            randomTabColor = randomTabColor,
            randomTabContentColor = randomTabContentColor,
            actions = actions,
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
            measuredTabs.previousTab.placeRelative(
                x = layoutWidth - measuredTabs.previousTab.width,
                y = previousTabY,
            )
            measuredTabs.randomTab.placeRelative(
                x = layoutWidth - measuredTabs.randomTab.width,
                y = randomTabY,
            )
        }
    }
}

private fun categoryTabHeight(
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

internal fun categoryTabColor(index: Int, colors: List<Color>): Color =
    colors[index % colors.size]

@Preview(showBackground = true)
@Composable
private fun CategoryTabsPreview() {
    ImpulseTheme {
        CategoryTabs(
            kategorien = PreviewUiState.kategorien,
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp),
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun CategoryTab(
    tab: GameKategorieUiModel,
    style: CategoryTabStyle,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    enabled: Boolean = true,
    visuallyEnabled: Boolean = enabled,
    target: CardSwipeTarget? = null,
    onBoundsChanged: (CardSwipeTarget, Rect) -> Unit = { _, _ -> },
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
    val tabHeight = style.fixedHeight
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
    val scale by animateFloatAsState(
        targetValue = if (highlighted) HIGHLIGHTED_TAB_SCALE else 1f,
        animationSpec = tween(durationMillis = TAB_HIGHLIGHT_ANIMATION_MILLIS),
        label = AnimationLabels.CATEGORY_TAB_SCALE,
    )

    Box(
        modifier = (if (onClick == null) modifier else modifier.clickable(
            enabled = enabled,
            onClick = onClick
        ))
            .then(
                if (target == null) {
                    Modifier
                } else {
                    Modifier.onGloballyPositioned { coordinates ->
                        onBoundsChanged(target, coordinates.boundsInRoot())
                    }
                },
            )
            .width(CategoryTabWidth)
            .height(tabHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (visuallyEnabled) 1f else DISABLED_TAB_ALPHA
                transformOrigin = TransformOrigin(
                    pivotFractionX = if (style.side == CategoryTabSide.Left) 0f else 1f,
                    pivotFractionY = 0.5f,
                )
            }
            .clip(categoryTabShape(style.side))
            .background(style.color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = labelText,
            modifier = Modifier
                .requiredWidth(fittedLabel.width)
                .graphicsLayer(rotationZ = style.side.labelRotation),
            color = style.contentColor ?: CategoryTabContentColor,
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
    ImpulseTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
        ) {
            CategoryTab(
                tab = PreviewUiState.kategorien.first(),
                style = CategoryTabStyle(
                    side = CategoryTabSide.Left,
                    color = CategoryTabColors.first(),
                ),
            )
            CategoryTab(
                tab = GameKategorieUiModel(
                    id = -1,
                    name = stringResource(R.string.previous_card),
                ),
                style = CategoryTabStyle(
                    side = CategoryTabSide.Right,
                    color = CategoryTabColors.last(),
                ),
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
    state: CategoryTabsState = CategoryTabsState(),
    categoryColors: List<Color>,
    randomTabColor: Color,
    randomTabContentColor: Color,
    actions: CategoryTabsActions = CategoryTabsActions(),
): MeasuredCategoryTabs {
    val measureConstraints = constraints.copy(minWidth = 0, minHeight = 0)
    val previousTab = subcompose(CategoryTabSlot.PreviousCard) {
        CategoryTab(
            tab = PreviousCardTab,
            style = CategoryTabStyle(
                side = CategoryTabSide.Right,
                color = PreviousCardTabColor,
            ),
            highlighted = state.highlightedTarget == CardSwipeTarget.Previous,
            enabled = state.previousEnabled && state.interactionsEnabled,
            visuallyEnabled = state.previousEnabled &&
                    (state.interactionsEnabled || !state.dimWhenInteractionsDisabled),
            target = CardSwipeTarget.Previous,
            onBoundsChanged = actions.onTabBoundsChanged,
            onClick = actions.onPreviousSelected,
        )
    }.single().measure(measureConstraints)
    val randomTab = subcompose(CategoryTabSlot.Random) {
        CategoryTab(
            tab = RandomTab,
            style = CategoryTabStyle(
                side = CategoryTabSide.Right,
                color = randomTabColor,
                contentColor = randomTabContentColor,
                fixedHeight = randomTabHeight,
            ),
            highlighted = state.highlightedTarget == CardSwipeTarget.Random,
            enabled = state.interactionsEnabled,
            visuallyEnabled = state.interactionsEnabled || !state.dimWhenInteractionsDisabled,
            target = CardSwipeTarget.Random,
            onBoundsChanged = actions.onTabBoundsChanged,
            onClick = actions.onRandomSelected,
        )
    }.single().measure(measureConstraints)
    val normalTabs = measureNormalCategoryTabs(
        kategorien, measureConstraints, normalTabHeight, state, actions, categoryColors,
    )

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

private val CategoryTabWidth = 24.dp
private val MinimumCategoryTabHeight = 64.dp
private val CategoryTabSpacing = 10.dp
private val CategoryTabLabelVerticalPadding = 14.dp
private val CategoryTabTextExtraWidth = 4.dp
private const val RANDOM_CATEGORY_TAB_HEIGHT_FRACTION = 1f / 3f
private const val HIGHLIGHTED_TAB_SCALE = 1.1f
private const val DISABLED_TAB_ALPHA = 0.45f
private const val TAB_HIGHLIGHT_ANIMATION_MILLIS = 90

private val RandomTab: GameKategorieUiModel
    @Composable get() = GameKategorieUiModel(
        id = -2,
        name = stringResource(R.string.random_card),
    )
private val PreviousCardTab: GameKategorieUiModel
    @Composable get() = GameKategorieUiModel(
        id = -1,
        name = stringResource(R.string.previous_card),
    )


internal data class CategoryTabsState(
    val highlightedTarget: CardSwipeTarget? = null,
    val previousEnabled: Boolean = false,
    val interactionsEnabled: Boolean = true,
    val dimWhenInteractionsDisabled: Boolean = true,
)

internal data class CategoryTabsActions(
    val onKategorieSelected: (Int) -> Unit = {},
    val onRandomSelected: () -> Unit = {},
    val onPreviousSelected: () -> Unit = {},
    val onTabBoundsChanged: (CardSwipeTarget, Rect) -> Unit = { _, _ -> },
)

private data class CategoryTabStyle(
    val side: CategoryTabSide,
    val color: Color,
    val contentColor: Color? = null,
    val fixedHeight: Dp? = null,
)

private fun SubcomposeMeasureScope.measureNormalCategoryTabs(
    kategorien: List<GameKategorieUiModel>,
    measureConstraints: Constraints,
    normalTabHeight: Dp,
    state: CategoryTabsState,
    actions: CategoryTabsActions,
    categoryColors: List<Color>,
): List<MeasuredCategoryTab> =
    kategorien.mapIndexed { index, tab ->
        val side = CategoryTabSide.Left
        val color = categoryTabColor(index, categoryColors)
        MeasuredCategoryTab(
            side = side,
            placeable = subcompose(NormalCategoryTabSlot(index = index, id = tab.id)) {
                CategoryTab(
                    tab = tab,
                    style = CategoryTabStyle(
                        side = side,
                        color = color,
                        fixedHeight = normalTabHeight,
                    ),
                    highlighted = state.highlightedTarget == CardSwipeTarget.Category(tab.id),
                    enabled = state.interactionsEnabled,
                    visuallyEnabled = state.interactionsEnabled || !state.dimWhenInteractionsDisabled,
                    target = CardSwipeTarget.Category(tab.id),
                    onBoundsChanged = actions.onTabBoundsChanged,
                    onClick = { actions.onKategorieSelected(tab.id) },
                )
            }.single().measure(measureConstraints),
        )
    }
