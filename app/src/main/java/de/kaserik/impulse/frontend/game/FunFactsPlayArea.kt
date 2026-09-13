@file:Suppress("CyclomaticComplexMethod", "LongMethod", "MagicNumber", "TooManyFunctions")

package de.kaserik.impulse.frontend.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Stable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.common.AnimationLabels
import de.kaserik.impulse.frontend.theme.AssignedColorTabColor
import de.kaserik.impulse.frontend.theme.CategoryTabColors
import de.kaserik.impulse.frontend.theme.FallbackTextPanelColor
import de.kaserik.impulse.frontend.theme.GameTableBrush
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import de.kaserik.impulse.frontend.theme.SignColors
import de.kaserik.impulse.frontend.theme.TableBackground
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun FunFactsPlayArea(
    uiState: GameUiState,
    session: FunFactsSession,
    modifier: Modifier = Modifier,
    swipeControls: CardSwipeControls = CardSwipeControls(),
    cardTextActions: CardTextActions = CardTextActions(),
    developerMode: Boolean = false,
    transitionActions: FunFactsTransitionActions = FunFactsTransitionActions(),
    gameContentHorizontalPadding: Dp = 0.dp,
) {
    val scope = rememberCoroutineScope()
    val playState = remember { FunFactsPlayAreaState() }
    val cardTextBounds = remember(uiState.aktuelleKarte.instanceId) {
        mutableStateMapOf<Int, Rect>()
    }
    val measuredModifier = modifier
        .graphicsLayer { alpha = playState.nextCardAlpha.value }
        .onGloballyPositioned { coordinates ->
            playState.playAreaBounds = coordinates.boundsInRoot()
        }

    LaunchedEffect(uiState.aktuelleKarte.instanceId, playState.awaitingNextCardId) {
        playState.revealNextCard(uiState.aktuelleKarte.instanceId)
    }

    if (session.selectingQuestion) {
        GamePlayArea(
            spielName = uiState.spielName,
            aktuelleKarte = uiState.aktuelleKarte,
            kategorien = uiState.kategorien,
            swipeControls = swipeControls,
            developerMode = developerMode,
            cardTextActions = cardTextActions.copy(
                onKartentextManuallyPlayedStateChanged = { cardTextId, _ ->
                    transitionActions.onQuestionTransitionStateChanged(true)
                    transitionActions.onCategoryTabsVisibilityChanged(false)
                    playState.newlySelectedQuestionId = cardTextId
                    session.selectQuestion(
                        questionId = cardTextId,
                        origin = cardTextBounds[cardTextId]
                            ?.relativeTo(playState.playAreaBounds),
                    )
                },
            ),
            onKartentextBoundsChanged = { cardTextId, bounds ->
                cardTextBounds[cardTextId] = bounds
            },
            modifier = measuredModifier.padding(horizontal = gameContentHorizontalPadding),
        )
        return
    }

    FunFactsQuestionStage(
        uiState = uiState,
        session = session,
        playState = playState,
        cardTextBounds = cardTextBounds,
        onNextRound = {
            scope.launch {
                playState.startNextRound(
                    uiState.aktuelleKarte.instanceId,
                    session,
                    transitionActions
                )
            }
        },
        modifier = measuredModifier,
        developerMode = developerMode,
        cardTextActions = cardTextActions,
        transitionActions = transitionActions,
        gameContentHorizontalPadding = gameContentHorizontalPadding,
    )
}

@Preview(showBackground = true)
@Composable
private fun FunFactsPlayAreaPreview() {
    ImpulseTheme {
        val session = remember {
            FunFactsSession().apply {
                selectQuestion(
                    questionId = 101,
                    origin = FunFactsQuestionOrigin(
                        leftFraction = 0.15f,
                        topFraction = 0.35f,
                        widthFraction = 0.7f,
                        heightFraction = 0.2f,
                    ),
                )
            }
        }
        FunFactsPlayArea(uiState = PreviewUiState, session = session)
    }
}

@Composable
private fun AnswerEntry(
    session: FunFactsSession,
    categoryName: String,
    modifier: Modifier = Modifier,
    colorTabsAlpha: Float = 1f,
    colorTabsInteractionsEnabled: Boolean = true,
) {
    BoxWithConstraints(modifier = modifier) {
        val maximumDrawingWidth = (
                maxWidth - (ColorTabWidth + ColorTabSpacing) * 2
                ).coerceAtLeast(0.dp)
        val preferredDrawingHeight = maximumDrawingWidth / SIGN_ASPECT_RATIO
        val heightWithoutDrawingPadSpacing = preferredDrawingHeight * 2 +
                DrawingControlsTopAllowance * 2 + AnswerEntrySpacing + FinishButtonHeight
        val drawingPadSpacing = (maxHeight - heightWithoutDrawingPadSpacing)
            .coerceAtMost(PreferredDrawingPadSpacing)
            .coerceAtLeast(MinimumDrawingPadSpacing)
        val availableDrawingHeight = (
                maxHeight - DrawingControlsTopAllowance * 2 - drawingPadSpacing -
                        AnswerEntrySpacing - FinishButtonHeight
                ).coerceAtLeast(0.dp) / 2f
        val drawingWidth = minOf(
            maximumDrawingWidth,
            availableDrawingHeight * SIGN_ASPECT_RATIO,
        )
        val drawingHeight = drawingWidth / SIGN_ASPECT_RATIO
        val currentStrokeWidth = drawingHeight *
                DEFAULT_DRAWING_STROKE_WIDTH_FRACTION
        val strokeWidthSelectorOffset = DrawingControlsTopAllowance / 2f -
                drawingHeight * (1f - CONCAVE_TIP_HEIGHT_FRACTION) / 2f
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(drawingWidth),
            verticalArrangement = Arrangement.spacedBy(
                AnswerEntrySpacing,
                Alignment.CenterVertically,
            ),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(drawingPadSpacing),
                ) {
                    DrawingPad(
                        label = stringResource(R.string.player_name_label),
                        drawing = session.draftName.snapshot(),
                        onStrokeStarted = { point ->
                            session.draftName.startStroke(
                                point,
                                StrokeWidthFractions[session.selectedStrokeWidthIndex]
                            )
                        },
                        onStrokeContinued = session.draftName::continueStroke,
                        onClear = session.draftName::clear,
                        signHeight = drawingHeight,
                        signColor = SignColors[session.selectedColorIndex % SignColors.size],
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DrawingPad(
                        label = stringResource(R.string.answer_label),
                        drawing = session.draftAnswer.snapshot(),
                        onStrokeStarted = { point ->
                            session.draftAnswer.startStroke(
                                point,
                                StrokeWidthFractions[session.selectedStrokeWidthIndex]
                            )
                        },
                        onStrokeContinued = session.draftAnswer::continueStroke,
                        onClear = session.draftAnswer::clear,
                        categoryName = categoryName,
                        signHeight = drawingHeight,
                        signColor = SignColors[session.selectedColorIndex % SignColors.size],
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                StrokeWidthSelector(
                    selectedIndex = session.selectedStrokeWidthIndex,
                    onSelected = session::selectStrokeWidth,
                    currentStrokeWidth = currentStrokeWidth,
                    selectedColor = SignColors[session.selectedColorIndex % SignColors.size],
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = strokeWidthSelectorOffset),
                )
            }
            FinishAnswerButton(session = session, modifier = Modifier.fillMaxWidth())
        }
        if (colorTabsAlpha > 0f) {
            SideColorTabs(
                selectedColorIndex = session.selectedColorIndex,
                availableColorIndices = session.availableColorIndices,
                onColorSelected = session::selectColor,
                interactionsEnabled = colorTabsInteractionsEnabled,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = colorTabsAlpha },
            )
        }
    }
}

@Composable
internal fun FunFactsLandscapeDrawing(
    uiState: GameUiState,
    session: FunFactsSession,
    target: FunFactsLandscapeTarget,
    modifier: Modifier = Modifier,
) {
    val question = uiState.aktuelleKarte.kartentexte.firstOrNull {
        it.id == session.selectedQuestionId
    }
    val categoryName = uiState.kategorien.firstOrNull { it.id == question?.kategorieId }
        ?.name.orEmpty()
    Surface(modifier = modifier.fillMaxSize(), color = TableBackground) {
        LandscapeDrawingEntry(
            session = session,
            target = target,
            categoryName = categoryName,
            modifier = Modifier
                .fillMaxSize()
                .background(GameTableBrush)
                .safeDrawingPadding()
                .padding(horizontal = ColorTabWidth + ColorTabSpacing, vertical = 24.dp),
        )
    }
}

@Composable
private fun LandscapeDrawingEntry(
    session: FunFactsSession,
    target: FunFactsLandscapeTarget,
    categoryName: String,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var deleteSize by remember { mutableStateOf(IntSize.Zero) }
    val answering = target == FunFactsLandscapeTarget.Answer
    val draft = if (answering) session.draftAnswer else session.draftName
    BoxWithConstraints(modifier = modifier) {
        val layout = landscapeDrawingPadLayout(
            availableSize = DpSize(maxWidth, maxHeight),
            deleteButtonWidth = with(density) { deleteSize.width.toDp() },
            deleteSignGap = with(density) { DELETE_SIGN_GAP_PX.toDp() },
        )
        DrawingPad(
            label = stringResource(if (answering) R.string.answer_label else R.string.player_name_label),
            drawing = draft.snapshot(),
            onStrokeStarted = { point ->
                draft.startStroke(point, StrokeWidthFractions[session.selectedStrokeWidthIndex])
            },
            onStrokeContinued = draft::continueStroke,
            onClear = draft::clear,
            signHeight = layout.signSize.height,
            signColor = SignColors[session.selectedColorIndex % SignColors.size],
            categoryName = categoryName.takeIf { answering },
            modifier = Modifier
                .offset(x = layout.drawingPadOffset.x, y = layout.drawingPadOffset.y)
                .width(layout.signSize.width),
            controls = DrawingPadControls(
                topAllowance = layout.controlsTopAllowance,
                finishSession = session.takeIf { answering },
                onDeleteSizeChanged = { deleteSize = it },
            ),
        )
        StrokeWidthSelector(
            selectedIndex = session.selectedStrokeWidthIndex,
            onSelected = session::selectStrokeWidth,
            selectedColor = SignColors[session.selectedColorIndex % SignColors.size],
            currentStrokeWidth = layout.signSize.height * DEFAULT_DRAWING_STROKE_WIDTH_FRACTION,
            vertical = true,
            modifier = Modifier.offset(
                x = layout.strokeWidthSelectorOffset.x,
                y = layout.strokeWidthSelectorOffset.y,
            ),
        )
    }
}

internal data class LandscapeDrawingPadLayout(
    val signSize: DpSize,
    val controlsTopAllowance: Dp,
    val drawingPadOffset: DpOffset,
    val strokeWidthSelectorOffset: DpOffset,
)

internal fun landscapeDrawingPadLayout(
    availableSize: DpSize,
    deleteButtonWidth: Dp,
    deleteSignGap: Dp,
): LandscapeDrawingPadLayout {
    val sideControlsWidth = StrokeWidthButtonSize + StrokeWidthButtonSpacing
    val controlsHeight = DrawingControlHeight + deleteSignGap
    val slopeAtDeleteStart = deleteButtonWidth * (2f * TOP_CORNER_HEIGHT_FRACTION / SIGN_ASPECT_RATIO)
    // The labels fit above the sloping edge; reserve extra height only when they need it.
    val heightWithControls = maxOf(
        availableSize.height - controlsHeight,
        (availableSize.height - controlsHeight - slopeAtDeleteStart) / (1f - TOP_CORNER_HEIGHT_FRACTION),
    )
    val signHeight = minOf(
        (availableSize.width - sideControlsWidth) / SIGN_ASPECT_RATIO,
        availableSize.height,
        heightWithControls,
    ).coerceAtLeast(0.dp)
    val signWidth = signHeight * SIGN_ASPECT_RATIO
    val boundaryAtDeleteStart = (signHeight * TOP_CORNER_HEIGHT_FRACTION - slopeAtDeleteStart)
        .coerceAtLeast(0.dp)
    val controlsTopAllowance = (controlsHeight - boundaryAtDeleteStart).coerceAtLeast(0.dp)
    // Keep the sign centered whenever the buttons fit in the existing right margin.
    val drawingPadX = minOf(
        (availableSize.width - signWidth) / 2f,
        availableSize.width - signWidth - sideControlsWidth,
    ).coerceAtLeast(0.dp)
    val drawingPadY = ((availableSize.height - signHeight - controlsTopAllowance) / 2f)
        .coerceAtLeast(0.dp)
    val selectorHeight = StrokeWidthButtonSize * StrokeWidthFractions.size +
            StrokeWidthButtonSpacing * (StrokeWidthFractions.size - 1)
    val sideCenterY = drawingPadY + controlsTopAllowance +
            signHeight * ((TOP_CORNER_HEIGHT_FRACTION + 1f) / 2f)
    val selectorY = (sideCenterY - selectorHeight / 2f)
        .coerceIn(0.dp, (availableSize.height - selectorHeight).coerceAtLeast(0.dp))
    return LandscapeDrawingPadLayout(
        signSize = DpSize(signWidth, signHeight),
        controlsTopAllowance = controlsTopAllowance,
        drawingPadOffset = DpOffset(drawingPadX, drawingPadY),
        strokeWidthSelectorOffset = DpOffset(drawingPadX + signWidth + StrokeWidthButtonSpacing, selectorY),
    )
}

private data class DrawingPadControls(
    val topAllowance: Dp = DrawingControlsTopAllowance,
    val finishSession: FunFactsSession? = null,
    val onDeleteSizeChanged: (IntSize) -> Unit = {},
)

@Composable
private fun FinishAnswerButton(session: FunFactsSession, modifier: Modifier = Modifier) {
    Button(
        onClick = session::finishAnswer,
        enabled = session.draftName.strokes.isNotEmpty() &&
                session.draftAnswer.strokes.isNotEmpty() &&
                session.selectedColorIndex in session.availableColorIndices,
        modifier = modifier.height(FinishButtonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) { Text(stringResource(R.string.done)) }
}

@Preview(showBackground = true)
@Composable
private fun FunFactsLandscapeDrawingPreview() {
    ImpulseTheme {
        FunFactsLandscapeDrawing(
            uiState = PreviewUiState,
            session = remember { FunFactsSession() },
            target = FunFactsLandscapeTarget.Name,
            modifier = Modifier.size(width = 800.dp, height = 360.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LandscapeDrawingEntryPreview() {
    ImpulseTheme {
        LandscapeDrawingEntry(
            session = remember { FunFactsSession().apply { draftAnswer.restore(PreviewDrawing) } },
            target = FunFactsLandscapeTarget.Answer,
            categoryName = stringResource(R.string.preview_category_knowledge),
            modifier = Modifier.size(width = 800.dp, height = 360.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FinishAnswerButtonPreview() {
    ImpulseTheme {
        FinishAnswerButton(
            session = remember {
                FunFactsSession().apply {
                    draftName.restore(PreviewDrawing)
                    draftAnswer.restore(PreviewDrawing)
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnswerEntryPreview() {
    ImpulseTheme {
        AnswerEntry(
            session = remember {
                FunFactsSession().apply {
                    draftName.restore(PreviewDrawing); draftAnswer.restore(
                    PreviewDrawing
                )
                }
            },
            categoryName = stringResource(R.string.preview_category_knowledge),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 520.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 780, heightDp = 360)
@Composable
private fun LandscapeNameDrawingPreview() {
    ImpulseTheme {
        FunFactsLandscapeDrawing(
            uiState = PreviewUiState,
            session = remember { previewSelectedQuestionSession() },
            target = FunFactsLandscapeTarget.Name,
        )
    }
}

@Preview(showBackground = true, widthDp = 780, heightDp = 360)
@Composable
private fun LandscapeAnswerDrawingPreview() {
    ImpulseTheme {
        FunFactsLandscapeDrawing(
            uiState = PreviewUiState,
            session = remember {
                previewSelectedQuestionSession().apply {
                    draftName.restore(PreviewDrawing)
                    draftAnswer.restore(PreviewDrawing)
                }
            },
            target = FunFactsLandscapeTarget.Answer,
        )
    }
}

@Composable
private fun SideColorTabs(
    selectedColorIndex: Int,
    availableColorIndices: List<Int>,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    interactionsEnabled: Boolean = true,
) {
    BoxWithConstraints(modifier = modifier) {
        val tabHeight = ((maxHeight - ColorTabSpacing * 6) / 5).coerceAtLeast(0.dp)
        ColorTabColumn(
            indices = 0 until 5,
            selectedColorIndex = selectedColorIndex,
            availableColorIndices = availableColorIndices,
            interactionsEnabled = interactionsEnabled,
            onColorSelected = onColorSelected,
            leftSide = true,
            tabHeight = tabHeight,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        ColorTabColumn(
            indices = 5 until 10,
            selectedColorIndex = selectedColorIndex,
            availableColorIndices = availableColorIndices,
            interactionsEnabled = interactionsEnabled,
            onColorSelected = onColorSelected,
            leftSide = false,
            tabHeight = tabHeight,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SideColorTabsPreview() {
    ImpulseTheme {
        SideColorTabs(
            selectedColorIndex = 2,
            availableColorIndices = listOf(0, 2, 4, 6, 8),
            onColorSelected = {},
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 360.dp),
        )
    }
}

@Composable
private fun ColorTabColumn(
    indices: IntRange,
    selectedColorIndex: Int,
    availableColorIndices: List<Int>,
    interactionsEnabled: Boolean,
    onColorSelected: (Int) -> Unit,
    leftSide: Boolean,
    tabHeight: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(ColorTabWidth)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(ColorTabSpacing, Alignment.CenterVertically),
        horizontalAlignment = if (leftSide) Alignment.Start else Alignment.End,
    ) {
        indices.forEach { index ->
            val available = index in availableColorIndices
            val selected = index == selectedColorIndex && available
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.1f else 1f,
                animationSpec = tween(durationMillis = 90),
                label = AnimationLabels.COLOR_TAB_SCALE,
            )
            Box(
                modifier = Modifier
                    .width(ColorTabWidth)
                    .height(tabHeight)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = if (selected) 1f else UNSELECTED_COLOR_TAB_ALPHA
                        transformOrigin = TransformOrigin(
                            pivotFractionX = if (leftSide) 0f else 1f,
                            pivotFractionY = 0.5f,
                        )
                    }
                    .clip(
                        colorTabShape(leftSide),
                    )
                    .background(if (available) SignColors[index] else AssignedColorTabColor)
                    .clickable(enabled = available && interactionsEnabled) {
                        onColorSelected(index)
                    },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorTabColumnPreview() {
    ImpulseTheme {
        ColorTabColumn(
            indices = 0 until 5,
            selectedColorIndex = 2,
            availableColorIndices = listOf(0, 2, 4),
            interactionsEnabled = true,
            onColorSelected = {},
            leftSide = true,
            tabHeight = 72.dp,
        )
    }
}

@Composable
private fun DrawingPad(
    label: String,
    drawing: FunFactsDrawing,
    onStrokeStarted: (Offset) -> Unit,
    onStrokeContinued: (Offset) -> Unit,
    onClear: () -> Unit,
    signHeight: Dp,
    signColor: Color,
    modifier: Modifier = Modifier,
    categoryName: String? = null,
    controls: DrawingPadControls = DrawingPadControls(),
) {
    BoxWithConstraints(modifier = modifier.height(signHeight + controls.topAllowance)) {
        var labelSize by remember { mutableStateOf(IntSize.Zero) }
        var deleteSize by remember { mutableStateOf(IntSize.Zero) }
        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val signTopPx = with(density) { controls.topAllowance.toPx() }
        val cornerHeightPx = with(density) { signHeight.toPx() } * TOP_CORNER_HEIGHT_FRACTION
        val deleteTop = if (deleteSize == IntSize.Zero) {
            0
        } else {
            val deleteStart = widthPx - deleteSize.width
            val boundaryAtDeleteStart = cornerHeightPx *
                    (2f * deleteStart / widthPx - 1f).coerceAtLeast(0f)
            (
                    signTopPx + boundaryAtDeleteStart - DELETE_SIGN_GAP_PX - deleteSize.height
                    ).roundToInt().coerceAtLeast(0)
        }
        val labelTop = if (labelSize == IntSize.Zero || deleteSize == IntSize.Zero) {
            deleteTop
        } else {
            deleteTop + (deleteSize.height - labelSize.height) / 2
        }
        val signBorderWidth = signHeight * MIDDLE_STROKE_WIDTH_FRACTION
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(signHeight),
            shape = WideCaretShape,
            color = signColor,
            border = BorderStroke(signBorderWidth, signColor.darkened()),
        ) {
            DrawingCanvas(
                drawing = drawing,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(onStrokeStarted, onStrokeContinued) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            down.consume()
                            onStrokeStarted(down.position.normalized(size.width, size.height))
                            var change = awaitPointerEvent().changes
                                .firstOrNull { it.id == down.id }
                            while (change?.pressed == true) {
                                change.consume()
                                onStrokeContinued(
                                    change.position.normalized(size.width, size.height),
                                )
                                change = awaitPointerEvent().changes
                                    .firstOrNull { it.id == down.id }
                            }
                        }
                    },
            )
        }
        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset { IntOffset(x = 0, y = labelTop) }
                .onSizeChanged { size -> labelSize = size },
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelLarge,
        )
        Button(
            onClick = onClear,
            enabled = drawing.strokes.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .height(DrawingControlHeight)
                .offset { IntOffset(x = 0, y = deleteTop) }
                .onSizeChanged { size ->
                    deleteSize = size
                    controls.onDeleteSizeChanged(size)
                },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        ) {
            Text(stringResource(R.string.delete))
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(CATEGORY_LABEL_WIDTH_FRACTION)
                .padding(bottom = 1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            categoryName?.takeIf(String::isNotBlank)?.let { name ->
                Text(
                    text = name,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (controls.finishSession != null) {
                FinishAnswerButton(session = controls.finishSession, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrawingPadPreview() {
    ImpulseTheme {
        DrawingPad(
            label = stringResource(R.string.player_name_label),
            drawing = FunFactsDrawing(
                strokes = listOf(
                    listOf(
                        Offset(0.1f, 0.2f),
                        Offset(0.9f, 0.8f),
                    ),
                ),
            ),
            onStrokeStarted = {},
            onStrokeContinued = {},
            onClear = {},
            signHeight = 240.dp,
            signColor = SignColors.first(),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 240.dp)
                .padding(12.dp),
        )
    }
}

@Composable
private fun StrokeWidthSelector(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    currentStrokeWidth: Dp = StrokeWidthPreviewDotSize,
    vertical: Boolean = false,
) {
    val buttons: @Composable () -> Unit = {
        StrokeWidthFractions.forEachIndexed { index, strokeWidth ->
            val strokeDescription = stringResource(R.string.stroke_width_description, index + 1)
            val selected = index == selectedIndex
            Surface(
                onClick = { onSelected(index) },
                modifier = Modifier
                    .size(StrokeWidthButtonSize)
                    .semantics {
                        contentDescription = strokeDescription
                    },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                border = BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) selectedColor else MaterialTheme.colorScheme.outlineVariant,
                ),
                shadowElevation = if (selected) 4.dp else 1.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(
                        modifier = Modifier.size(
                            currentStrokeWidth *
                                    (strokeWidth / DEFAULT_DRAWING_STROKE_WIDTH_FRACTION) *
                                    DRAWING_STROKE_WIDTH_SCALE,
                        ),
                    ) {
                        drawCircle(selectedColor)
                    }
                }
            }
        }
    }
    if (vertical) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(StrokeWidthButtonSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) { buttons() }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(StrokeWidthButtonSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) { buttons() }
    }
}

@Preview(showBackground = true)
@Composable
private fun StrokeWidthSelectorPreview() {
    ImpulseTheme {
        StrokeWidthSelector(
            selectedIndex = DEFAULT_STROKE_WIDTH_INDEX,
            onSelected = {},
            selectedColor = SignColors.first(),
        )
    }
}

@Composable
private fun PositioningActions(
    revealEnabled: Boolean,
    nextPlayerEnabled: Boolean,
    onNextPlayer: () -> Unit,
    onReveal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            stringResource(R.string.sign_placement_hint),
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = onNextPlayer,
                enabled = nextPlayerEnabled,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.next_player))
            }
            Button(onClick = onReveal, enabled = revealEnabled, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.reveal))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PositioningActionsPreview() {
    ImpulseTheme {
        PositioningActions(
            revealEnabled = true,
            nextPlayerEnabled = true,
            onNextPlayer = {},
            onReveal = {},
        )
    }
}

@Composable
private fun RevealButtonWithHint(
    showHint: Boolean,
    onDismissHint: () -> Unit,
    onReveal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(showHint) {
        if (showHint) {
            delay(FIRST_PLAYER_HINT_DURATION_MILLIS.milliseconds)
            onDismissHint()
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ActionButtonHeight),
    ) {
        Button(
            onClick = onReveal,
            modifier = Modifier.fillMaxSize()
        ) { Text(stringResource(R.string.reveal)) }
        AnimatedVisibility(
            visible = showHint,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(180)),
            modifier = Modifier.fillMaxSize(),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onDismissHint),
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.96f),
                shadowElevation = 2.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.first_player_hint),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RevealButtonWithHintPreview() {
    ImpulseTheme {
        RevealButtonWithHint(showHint = true, onDismissHint = {}, onReveal = {})
    }
}

@Composable
private fun PlayerSignStack(
    players: List<FunFactsPlayer>,
    activeSignId: Int?,
    onMoveActiveSign: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onToggleRevealedSide: (Int) -> Unit = {},
    revealedSignsCanBeFlipped: Boolean = false,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val tipGap = with(LocalDensity.current) { STACK_TIP_GAP_PX.toDp() }
        val layoutSignCount = players.size.coerceAtLeast(MIN_STACK_LAYOUT_SIGN_COUNT)
        val spacesInFullStack = layoutSignCount - 1
        val stackHeightFactor = 1f +
                CONCAVE_TIP_HEIGHT_FRACTION * spacesInFullStack
        val signHeightByStack = (
                maxHeight - tipGap * spacesInFullStack
                ).coerceAtLeast(0.dp) / stackHeightFactor
        val signHeight = minOf(signHeightByStack, maxWidth / SIGN_ASPECT_RATIO)
        val signWidth = signHeight * SIGN_ASPECT_RATIO
        val spacing = tipGap - signHeight * (1f - CONCAVE_TIP_HEIGHT_FRACTION)

        Column(
            modifier = Modifier.width(signWidth),
            verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            players.forEach { player ->
                key(player.id) {
                    PlayerSign(
                        player = player,
                        active = player.id == activeSignId,
                        onMove = onMoveActiveSign,
                        onToggleRevealedSide = onToggleRevealedSide,
                        flippingEnabled = revealedSignsCanBeFlipped,
                        borderWidth = signHeight * MIDDLE_STROKE_WIDTH_FRACTION,
                        modifier = Modifier
                            .width(signWidth)
                            .height(signHeight),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerSignStackPreview() {
    ImpulseTheme {
        PlayerSignStack(
            players = listOf(
                FunFactsPlayer(0, PreviewDrawing, PreviewDrawing, 0),
                FunFactsPlayer(1, PreviewDrawing, PreviewDrawing, 1),
            ),
            activeSignId = 1,
            onMoveActiveSign = {},
        )
    }
}

@Composable
private fun PlayerSign(
    player: FunFactsPlayer,
    active: Boolean,
    onMove: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onToggleRevealedSide: (Int) -> Unit = {},
    flippingEnabled: Boolean = false,
    borderWidth: Dp = 2.dp,
) {
    val signDescription = stringResource(playerSignDescription(player, active, flippingEnabled))
    val signColor = SignColors[player.colorIndex % SignColors.size]
    val rotation by animateFloatAsState(
        targetValue = if (player.answerVisible) 180f else 0f,
        label = AnimationLabels.REVEAL_SIGN,
    )
    val dragThreshold = with(LocalDensity.current) { 32.dp.toPx() }
    val flipping = rotation > 0.5f && rotation < 179.5f
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val shadowElevation = when {
        flipping -> 1.dp
        pressed -> 10.dp
        active -> 8.dp
        else -> 4.dp
    }
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = shadowElevation,
                    shape = WideCaretShape,
                    clip = false,
                )
                .graphicsLayer { rotationY = rotation }
                .reorderPlayerSign(player.id, active, dragThreshold, onMove)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = flippingEnabled && player.revealed,
                ) { onToggleRevealedSide(player.id) }
                .semantics {
                    contentDescription = signDescription
                },
            shape = WideCaretShape,
            color = colorResource(R.color.transparent),
            shadowElevation = 0.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(WideCaretShape)
                    .background(signColor)
                    .border(
                        width = borderWidth,
                        color = if (active) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            signColor.darkened()
                        },
                        shape = WideCaretShape,
                    )
                    .graphicsLayer { rotationY = if (rotation > 90f) 180f else 0f },
                contentAlignment = Alignment.Center,
            ) {
                if (rotation > 90f) {
                    DrawingCanvas(
                        drawing = player.answer,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    DrawingCanvas(
                        drawing = player.name,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerSignPreview() {
    ImpulseTheme {
        PlayerSign(
            player = FunFactsPlayer(0, PreviewDrawing, PreviewDrawing, 0),
            active = true,
            onMove = {},
        )
    }
}

private val WideCaretShape = GenericShape { size, _ ->
    val vertices = listOf(
        Offset(0f, size.height * TOP_CORNER_HEIGHT_FRACTION),
        Offset(size.width * 0.5f, 0f),
        Offset(size.width, size.height * TOP_CORNER_HEIGHT_FRACTION),
        Offset(size.width, size.height),
        Offset(size.width * 0.5f, size.height * 0.72f),
        Offset(0f, size.height),
    )
    val cornerRadius = size.minDimension * SIGN_CORNER_RADIUS_FRACTION
    val entries = vertices.indices.map { index ->
        vertices[index].towards(vertices[(index - 1 + vertices.size) % vertices.size], cornerRadius)
    }
    val exits = vertices.indices.map { index ->
        vertices[index].towards(vertices[(index + 1) % vertices.size], cornerRadius)
    }
    moveTo(exits.first().x, exits.first().y)
    for (index in 1 until vertices.size) {
        lineTo(entries[index].x, entries[index].y)
        quadraticTo(
            vertices[index].x,
            vertices[index].y,
            exits[index].x,
            exits[index].y,
        )
    }
    lineTo(entries.first().x, entries.first().y)
    quadraticTo(
        vertices.first().x,
        vertices.first().y,
        exits.first().x,
        exits.first().y,
    )
    close()
}

private fun Offset.towards(other: Offset, distance: Float): Offset {
    val delta = other - this
    val length = delta.getDistance()
    if (length == 0f) return this
    return this + delta * (distance.coerceAtMost(length / 2f) / length)
}

private fun Color.darkened(): Color = Color(
    red = red * SIGN_BORDER_DARKENING_FACTOR,
    green = green * SIGN_BORDER_DARKENING_FACTOR,
    blue = blue * SIGN_BORDER_DARKENING_FACTOR,
    alpha = alpha,
)


@Composable
private fun DrawingCanvas(
    drawing: FunFactsDrawing,
    modifier: Modifier = Modifier,
) {
    val inkColor = colorResource(R.color.black)
    Canvas(modifier = modifier) {
        drawing.strokes.forEachIndexed { index, stroke ->
            if (stroke.isEmpty()) return@forEachIndexed
            val strokeWidth = size.minDimension * DRAWING_STROKE_WIDTH_SCALE * (
                    drawing.strokeWidthFractions.getOrNull(index)
                        ?: DEFAULT_DRAWING_STROKE_WIDTH_FRACTION
                    )
            val scaledPoints = stroke.map { point ->
                Offset(
                    x = point.x * size.width,
                    y = point.y * size.height,
                )
            }
            if (scaledPoints.size == 1) {
                drawCircle(
                    color = inkColor,
                    radius = strokeWidth / 2f,
                    center = scaledPoints.single(),
                )
            } else {
                scaledPoints.zipWithNext().forEach { (start, end) ->
                    drawLine(
                        color = inkColor,
                        start = start,
                        end = end,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrawingCanvasPreview() {
    ImpulseTheme {
        DrawingCanvas(
            drawing = PreviewDrawing,
            modifier = Modifier.size(180.dp),
        )
    }
}

private fun Offset.normalized(
    width: Int,
    height: Int,
): Offset =
    Offset(
        x = (x / width.coerceAtLeast(1)).coerceIn(0f, 1f),
        y = (y / height.coerceAtLeast(1)).coerceIn(0f, 1f),
    )

private fun Rect.relativeTo(container: Rect): FunFactsQuestionOrigin? {
    if (container.width <= 0f || container.height <= 0f) return null
    return FunFactsQuestionOrigin(
        leftFraction = ((left - container.left) / container.width).coerceIn(0f, 1f),
        topFraction = ((top - container.top) / container.height).coerceIn(0f, 1f),
        widthFraction = (width / container.width).coerceIn(0f, 1f),
        heightFraction = (height / container.height).coerceIn(0f, 1f),
    ).takeIf(FunFactsQuestionOrigin::valid)
}

private val PreviewDrawing = FunFactsDrawing(
    strokes = listOf(
        listOf(
            Offset(0.2f, 0.2f),
            Offset(0.8f, 0.8f),
        ),
        listOf(
            Offset(0.8f, 0.2f),
            Offset(0.2f, 0.8f),
        ),
    ),
)

private val ColorTabWidth = 24.dp
private val ColorTabSpacing = 10.dp
private val ActionButtonHeight = 40.dp
private val MaxActionWidth = 580.dp
private val SelectedQuestionHorizontalPadding = 44.dp
private val SelectedQuestionCompactHeight = 112.dp
private val DrawingControlHeight = 40.dp
private val DrawingControlsTopAllowance = 44.dp
private val PreferredDrawingPadSpacing = 48.dp
private val MinimumDrawingPadSpacing = 0.dp
private val AnswerEntrySpacing = 8.dp
private val FinishButtonHeight = 40.dp
private val StrokeWidthButtonSize = 30.dp
private val StrokeWidthButtonSpacing = 8.dp
private val StrokeWidthPreviewDotSize = 6.dp
private val StrokeWidthFractions = listOf(
    DEFAULT_DRAWING_STROKE_WIDTH_FRACTION,
    MIDDLE_STROKE_WIDTH_FRACTION,
    DEFAULT_DRAWING_STROKE_WIDTH_FRACTION * 2f,
)
private const val SIGN_ASPECT_RATIO = 1.5555556f
private const val CONCAVE_TIP_HEIGHT_FRACTION = 0.72f
private const val TOP_CORNER_HEIGHT_FRACTION = 0.28f
private const val MIN_STACK_LAYOUT_SIGN_COUNT = 5
private const val STACK_TIP_GAP_PX = 5
private const val UNSELECTED_COLOR_TAB_ALPHA = 0.3f
private const val ACTION_COLOR_TAB_GAP_PX = 10
private const val DELETE_SIGN_GAP_PX = 2.5f
private const val SIGN_CORNER_RADIUS_FRACTION = 0.04f
private const val CATEGORY_LABEL_WIDTH_FRACTION = 0.42f
private const val MIDDLE_STROKE_WIDTH_FRACTION =
    DEFAULT_DRAWING_STROKE_WIDTH_FRACTION * 1.5f
private const val DRAWING_STROKE_WIDTH_SCALE = 0.9f
private const val SIGN_BORDER_DARKENING_FACTOR = 0.78f
private const val FIRST_PLAYER_HINT_DURATION_MILLIS = 5_000L
private const val QUESTION_SLIDE_DURATION_MILLIS = 480L
private const val NEXT_CARD_FADE_OUT_DURATION_MILLIS = 180
private const val NEXT_CARD_FADE_IN_DURATION_MILLIS = 220
private const val NEXT_CARD_LOAD_TIMEOUT_MILLIS = 1_500L

private fun colorTabShape(leftSide: Boolean): RoundedCornerShape =
    if (leftSide) {
        RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
    } else {
        RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
    }

private fun playerSignDescription(
    player: FunFactsPlayer,
    active: Boolean,
    flippingEnabled: Boolean
): Int =
    when {
        active -> R.string.active_sign_description
        flippingEnabled && player.revealed && player.answerVisible ->
            R.string.answer_side_description

        flippingEnabled && player.revealed ->
            R.string.name_side_description

        else -> R.string.sign_description
    }

private fun Modifier.reorderPlayerSign(
    playerId: Int,
    active: Boolean,
    dragThreshold: Float,
    onMove: (Int) -> Unit,
): Modifier =
    this.pointerInput(playerId, active, dragThreshold, onMove) {
        if (!active) return@pointerInput
        var accumulatedDrag = 0f
        detectDragGestures { change, dragAmount ->
            change.consume()
            accumulatedDrag += dragAmount.y
            if (abs(accumulatedDrag) >= dragThreshold) {
                onMove(if (accumulatedDrag < 0f) -1 else 1)
                accumulatedDrag = 0f
            }
        }
    }

internal data class FunFactsTransitionActions(
    val onQuestionTransitionStateChanged: (Boolean) -> Unit = {},
    val onCategoryTabsVisibilityChanged: (Boolean) -> Unit = {},
    val onNextCard: () -> Unit = {},
)

@Stable
private class FunFactsPlayAreaState {
    val nextCardAlpha = Animatable(1f)
    var nextCardTransitionRunning by mutableStateOf(false)
        private set
    var awaitingNextCardId by mutableStateOf<Long?>(null)
        private set
    var newlySelectedQuestionId by mutableStateOf<Int?>(null)
    var playAreaBounds by mutableStateOf(Rect.Zero)

    suspend fun revealNextCard(cardInstanceId: Long) {
        val previousCardId = awaitingNextCardId ?: return
        if (cardInstanceId != previousCardId) finishNextCardTransition()
    }

    suspend fun startNextRound(
        cardInstanceId: Long,
        session: FunFactsSession,
        actions: FunFactsTransitionActions,
    ) {
        nextCardTransitionRunning = true
        nextCardAlpha.animateTo(0f, tween(NEXT_CARD_FADE_OUT_DURATION_MILLIS))
        awaitingNextCardId = cardInstanceId
        session.startNextRound()
        actions.onCategoryTabsVisibilityChanged(true)
        actions.onNextCard()
        delay(NEXT_CARD_LOAD_TIMEOUT_MILLIS.milliseconds)
        if (awaitingNextCardId != null) finishNextCardTransition()
    }

    private suspend fun finishNextCardTransition() {
        nextCardAlpha.animateTo(1f, tween(NEXT_CARD_FADE_IN_DURATION_MILLIS))
        awaitingNextCardId = null
        nextCardTransitionRunning = false
    }
}

@Composable
private fun FunFactsQuestionStage(
    uiState: GameUiState,
    session: FunFactsSession,
    playState: FunFactsPlayAreaState,
    cardTextBounds: MutableMap<Int, Rect>,
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier,
    developerMode: Boolean = false,
    cardTextActions: CardTextActions = CardTextActions(),
    transitionActions: FunFactsTransitionActions = FunFactsTransitionActions(),
    gameContentHorizontalPadding: Dp = 0.dp,
) {
    val question = uiState.aktuelleKarte.kartentexte.firstOrNull {
        it.id == session.selectedQuestionId
    } ?: return
    val questionIndex = uiState.aktuelleKarte.kartentexte.indexOf(question)
    val questionColor = uiState.aktuelleKarte.textPanelColors(
        uiState.kategorien, CategoryTabColors, FallbackTextPanelColor,
    )[questionIndex]
    val categoryName = uiState.kategorien
        .firstOrNull { category -> category.id == question.kategorieId }
        ?.name
        .orEmpty()
    val questionOrigin = session.selectedQuestionOrigin
    val measuredQuestionBounds = cardTextBounds[question.id]
    LaunchedEffect(question.id, questionOrigin, measuredQuestionBounds, playState.playAreaBounds) {
        if (questionOrigin == null) {
            measuredQuestionBounds
                ?.relativeTo(playState.playAreaBounds)
                ?.let(session::rememberSelectedQuestionOrigin)
        }
    }
    val transitionProgress = remember(question.id) {
        Animatable(
            if (playState.newlySelectedQuestionId == question.id || questionOrigin == null) 0f else 1f,
        )
    }
    var returningToQuestionSelection by remember(question.id) { mutableStateOf(false) }
    LaunchedEffect(question.id, returningToQuestionSelection, questionOrigin) {
        animateQuestionTransition(
            questionOrigin = questionOrigin,
            returningToQuestionSelection = returningToQuestionSelection,
            transitionProgress = transitionProgress,
            session = session,
            playState = playState,
            transitionActions = transitionActions,
        )
    }

    val hiddenCardTextIds = if (questionOrigin == null) emptySet() else setOf(question.id)
    val backdropAlpha = if (questionOrigin == null) 1f else 1f - transitionProgress.value
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val actionColorTabGap = with(density) { ACTION_COLOR_TAB_GAP_PX.toDp() }
        val actionWidth = (
                maxWidth - (ColorTabWidth + actionColorTabGap) * 2
                ).coerceAtLeast(0.dp).coerceAtMost(MaxActionWidth)
        val progress = transitionProgress.value
        val placement = questionPlacement(
            container = DpSize(maxWidth, maxHeight),
            questionOrigin = questionOrigin,
            originBounds = cardTextBounds[question.id],
            playAreaBounds = playState.playAreaBounds,
            cardTextCount = uiState.aktuelleKarte.kartentexte.size,
            density = density,
            progress = progress,
        )

        if (questionOrigin == null || progress < 1f) {
            GamePlayArea(
                spielName = uiState.spielName,
                aktuelleKarte = uiState.aktuelleKarte,
                kategorien = uiState.kategorien,
                interactionsEnabled = false,
                developerMode = developerMode,
                hiddenCardTextIds = hiddenCardTextIds,
                onKartentextBoundsChanged = { cardTextId, bounds ->
                    cardTextBounds[cardTextId] = bounds
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = gameContentHorizontalPadding)
                    .graphicsLayer {
                        alpha = backdropAlpha
                    },
            )
        }

        FunFactsRoundControls(
            session = session,
            categoryName = categoryName,
            questionHeight = placement.targetHeight,
            actionWidth = actionWidth,
            progress = progress,
            returningToQuestionSelection = returningToQuestionSelection,
            nextRoundEnabled = !playState.nextCardTransitionRunning,
            onNextRound = onNextRound,
        )

        if (questionOrigin != null) {
            CardTextPanel(
                kartentext = question,
                index = questionIndex,
                kartentextCount = uiState.aktuelleKarte.kartentexte.size,
                textPanelColor = questionColor,
                interactionsEnabled = progress >= 1f &&
                        session.players.isEmpty() &&
                        !returningToQuestionSelection,
                markerInteractionsEnabled = progress >= 1f &&
                        !returningToQuestionSelection,
                developerMode = developerMode,
                cardTextActions = cardTextActions.withPlayedStateHandler { _, _ ->
                    transitionActions.onQuestionTransitionStateChanged(true)
                    transitionActions.onCategoryTabsVisibilityChanged(true)
                    returningToQuestionSelection = true
                },
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = placement.offset.x.roundToPx(),
                            y = placement.offset.y.roundToPx(),
                        )
                    }
                    .width(placement.size.width)
                    .height(placement.size.height),
            )
        }
    }
}

internal data class QuestionPlacement(
    val offset: DpOffset,
    val size: DpSize,
    val targetHeight: Dp,
)

internal fun questionPlacement(
    container: DpSize,
    questionOrigin: FunFactsQuestionOrigin?,
    originBounds: Rect?,
    playAreaBounds: Rect,
    cardTextCount: Int,
    density: Density,
    progress: Float,
): QuestionPlacement {
    val maxWidth = container.width
    val maxHeight = container.height
    val originalWidth = questionOrigin?.let { origin ->
        maxWidth * origin.widthFraction
    } ?: with(density) { originBounds?.width?.toDp() } ?: 0.dp
    val originalHeight = questionOrigin?.let { origin ->
        maxHeight * origin.heightFraction
    } ?: with(density) { originBounds?.height?.toDp() } ?: 0.dp
    val compactTargetWidth = (maxWidth - SelectedQuestionHorizontalPadding * 2)
        .coerceAtLeast(0.dp)
        .coerceAtMost(560.dp)
    val changesSize = cardTextCount < 3 ||
            (questionOrigin == null && originBounds == null)
    val targetWidth = if (changesSize) compactTargetWidth else originalWidth
    val targetHeight = if (changesSize) SelectedQuestionCompactHeight else originalHeight
    val targetX = (maxWidth - targetWidth) / 2
    val originX = questionOrigin?.let { origin ->
        maxWidth * origin.leftFraction
    } ?: originBounds?.let { bounds ->
        with(density) { (bounds.left - playAreaBounds.left).toDp() }
    } ?: targetX
    val originY = questionOrigin?.let { origin ->
        maxHeight * origin.topFraction
    } ?: originBounds?.let { bounds ->
        with(density) { (bounds.top - playAreaBounds.top).toDp() }
    } ?: (maxHeight - targetHeight)
    val animatedX = originX + (targetX - originX) * progress
    val animatedY = originY + (0.dp - originY) * progress
    val startWidth = originalWidth.takeIf { it > 0.dp } ?: targetWidth
    val startHeight = originalHeight.takeIf { it > 0.dp } ?: targetHeight
    val animatedWidth = if (changesSize) {
        startWidth + (targetWidth - startWidth) * progress
    } else {
        originalWidth
    }
    val animatedHeight = if (changesSize) {
        startHeight + (targetHeight - startHeight) * progress
    } else {
        originalHeight
    }

    return QuestionPlacement(
        offset = DpOffset(animatedX, animatedY),
        size = DpSize(animatedWidth, animatedHeight),
        targetHeight = targetHeight,
    )
}

@Composable
private fun FunFactsRoundControls(
    session: FunFactsSession,
    categoryName: String,
    questionHeight: Dp,
    actionWidth: Dp,
    progress: Float,
    returningToQuestionSelection: Boolean,
    nextRoundEnabled: Boolean,
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = progress },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(modifier = Modifier.height(questionHeight))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (session.phase == FunFactsPhase.EnterAnswer) {
                AnswerEntry(
                    session = session,
                    categoryName = categoryName,
                    colorTabsAlpha = progress,
                    colorTabsInteractionsEnabled = progress >= 1f &&
                            !returningToQuestionSelection,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                PlayerSignStack(
                    players = session.players,
                    activeSignId = session.activeSignId,
                    onMoveActiveSign = session::moveActiveSign,
                    onToggleRevealedSide = session::toggleRevealedSide,
                    revealedSignsCanBeFlipped = session.phase == FunFactsPhase.Revealing ||
                            session.phase == FunFactsPhase.Complete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
                FunFactsPhaseActions(
                    session = session,
                    nextRoundEnabled = nextRoundEnabled,
                    onNextRound = onNextRound,
                    modifier = Modifier.width(actionWidth),
                )
            }
        }
    }
}

@Composable
private fun FunFactsPhaseActions(
    session: FunFactsSession,
    nextRoundEnabled: Boolean,
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (session.phase) {
        FunFactsPhase.PositionSign -> PositioningActions(
            revealEnabled = session.players.size >= 2,
            nextPlayerEnabled = session.canAddPlayer,
            onNextPlayer = session::nextPlayer,
            onReveal = session::beginReveal,
            modifier = modifier,
        )

        FunFactsPhase.FinalPositioning -> RevealButtonWithHint(
            showHint = session.showFirstPlayerHint,
            onDismissHint = session::dismissFirstPlayerHint,
            onReveal = session::beginReveal,
            modifier = modifier,
        )

        FunFactsPhase.Revealing -> Button(
            onClick = session::beginReveal,
            modifier = modifier,
        ) { Text(stringResource(R.string.reveal)) }

        FunFactsPhase.Complete -> Button(
            onClick = onNextRound,
            enabled = nextRoundEnabled,
            modifier = modifier,
        ) { Text(stringResource(R.string.next_card)) }

        else -> Unit
    }
}

@Preview(showBackground = true)
@Composable
private fun FunFactsQuestionStagePreview() {
    ImpulseTheme {
        FunFactsQuestionStage(
            uiState = PreviewUiState,
            session = remember { previewSelectedQuestionSession() },
            playState = remember { FunFactsPlayAreaState() },
            cardTextBounds = remember { mutableStateMapOf() },
            onNextRound = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FunFactsRoundControlsPreview() {
    ImpulseTheme {
        FunFactsRoundControls(
            session = remember { previewSelectedQuestionSession() },
            categoryName = stringResource(R.string.preview_category_knowledge),
            questionHeight = SelectedQuestionCompactHeight,
            actionWidth = 280.dp,
            progress = 1f,
            returningToQuestionSelection = false,
            nextRoundEnabled = true,
            onNextRound = {},
            modifier = Modifier.width(360.dp).height(640.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FunFactsPhaseActionsPreview() {
    ImpulseTheme {
        FunFactsPhaseActions(
            session = remember {
                previewSelectedQuestionSession().apply {
                    draftName.restore(PreviewDrawing)
                    draftAnswer.restore(PreviewDrawing)
                    finishAnswer()
                }
            },
            nextRoundEnabled = true,
            onNextRound = {},
        )
    }
}

private fun previewSelectedQuestionSession(): FunFactsSession =
    FunFactsSession().apply {
        selectQuestion(
            questionId = 101,
            origin = FunFactsQuestionOrigin(0.15f, 0.35f, 0.7f, 0.2f),
        )
    }

private suspend fun animateQuestionTransition(
    questionOrigin: FunFactsQuestionOrigin?,
    returningToQuestionSelection: Boolean,
    transitionProgress: Animatable<Float, AnimationVector1D>,
    session: FunFactsSession,
    playState: FunFactsPlayAreaState,
    transitionActions: FunFactsTransitionActions,
) {

    transitionActions.onQuestionTransitionStateChanged(true)
    if (questionOrigin == null) {
        transitionActions.onCategoryTabsVisibilityChanged(false)
        return
    }
    if (returningToQuestionSelection) {
        transitionActions.onCategoryTabsVisibilityChanged(true)
        transitionProgress.animateTo(
            targetValue = 0f,
            animationSpec = tween(QUESTION_SLIDE_DURATION_MILLIS.toInt()),
        )
        session.reopenQuestionSelection()
    } else {
        transitionActions.onCategoryTabsVisibilityChanged(false)
        if (transitionProgress.value < 1f) {
            transitionProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(QUESTION_SLIDE_DURATION_MILLIS.toInt()),
            )
        }
        playState.newlySelectedQuestionId = null
    }
    transitionActions.onQuestionTransitionStateChanged(false)
}
