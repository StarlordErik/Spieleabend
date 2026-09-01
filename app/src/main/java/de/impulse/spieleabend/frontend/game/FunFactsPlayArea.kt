@file:Suppress("CyclomaticComplexMethod", "LongMethod", "MagicNumber", "TooManyFunctions")

package de.impulse.spieleabend.frontend.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun FunFactsPlayArea(
    uiState: GameUiState,
    session: FunFactsSession,
    modifier: Modifier = Modifier,
    swipeRegions: Collection<SwipeRegion> = emptyList(),
    previousEnabled: Boolean = false,
    onHighlightedTargetChanged: (CardSwipeTarget?) -> Unit = {},
    onInteractionStateChanged: (Boolean) -> Unit = {},
    onSwipeTargetSelected: (CardSwipeTarget) -> Unit = {},
    onKartentextPlayedStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
    onQuestionTransitionStateChanged: (Boolean) -> Unit = {},
    onCategoryTabsVisibilityChanged: (Boolean) -> Unit = {},
    onNextCard: () -> Unit = {},
    gameContentHorizontalPadding: Dp = 0.dp,
) {
    val nextCardAlpha = remember { Animatable(1f) }
    val nextCardTransitionScope = rememberCoroutineScope()
    var nextCardTransitionRunning by remember { mutableStateOf(false) }
    var awaitingNextCardId by remember { mutableStateOf<Long?>(null) }
    var newlySelectedQuestionId by remember { mutableStateOf<Int?>(null) }
    var playAreaBounds by remember { mutableStateOf(Rect.Zero) }
    val cardTextBounds = remember(uiState.aktuelleKarte.instanceId) {
        mutableStateMapOf<Int, Rect>()
    }
    val measuredModifier = modifier
        .graphicsLayer { alpha = nextCardAlpha.value }
        .onGloballyPositioned { coordinates ->
            playAreaBounds = coordinates.boundsInRoot()
        }

    LaunchedEffect(uiState.aktuelleKarte.instanceId, awaitingNextCardId) {
        val previousCardId = awaitingNextCardId ?: return@LaunchedEffect
        if (uiState.aktuelleKarte.instanceId != previousCardId) {
            nextCardAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(NEXT_CARD_FADE_IN_DURATION_MILLIS),
            )
            awaitingNextCardId = null
            nextCardTransitionRunning = false
        }
    }

    if (session.selectingQuestion) {
        GamePlayArea(
            spielName = uiState.spielName,
            aktuelleKarte = uiState.aktuelleKarte,
            kategorien = uiState.kategorien,
            swipeRegions = swipeRegions,
            previousEnabled = previousEnabled,
            onHighlightedTargetChanged = onHighlightedTargetChanged,
            onInteractionStateChanged = onInteractionStateChanged,
            onSwipeTargetSelected = onSwipeTargetSelected,
            onKartentextPlayedStateChanged = { cardTextId, played ->
                if (played) {
                    onQuestionTransitionStateChanged(true)
                    onCategoryTabsVisibilityChanged(false)
                    newlySelectedQuestionId = cardTextId
                    session.selectQuestion(
                        questionId = cardTextId,
                        origin = cardTextBounds[cardTextId]
                            ?.relativeTo(playAreaBounds),
                    )
                }
                onKartentextPlayedStateChanged(cardTextId, played)
            },
            onKartentextBoundsChanged = { cardTextId, bounds ->
                cardTextBounds[cardTextId] = bounds
            },
            modifier = measuredModifier.padding(horizontal = gameContentHorizontalPadding),
        )
        return
    }

    val question = uiState.aktuelleKarte.kartentexte.firstOrNull {
        it.id == session.selectedQuestionId
    } ?: return
    val questionIndex = uiState.aktuelleKarte.kartentexte.indexOf(question)
    val questionColor = uiState.aktuelleKarte.textPanelColors(uiState.kategorien)[questionIndex]
    val categoryName = uiState.kategorien
        .firstOrNull { category -> category.id == question.kategorieId }
        ?.name
        .orEmpty()
    val transitionCard = uiState.aktuelleKarte.copy(
        kartentexte = uiState.aktuelleKarte.kartentexte.map { cardText ->
            if (cardText.id == question.id) cardText.copy(gespielt = false) else cardText
        },
    )
    val questionOrigin = session.selectedQuestionOrigin
    val measuredQuestionBounds = cardTextBounds[question.id]
    LaunchedEffect(question.id, questionOrigin, measuredQuestionBounds, playAreaBounds) {
        if (questionOrigin == null) {
            measuredQuestionBounds
                ?.relativeTo(playAreaBounds)
                ?.let(session::rememberSelectedQuestionOrigin)
        }
    }
    val transitionProgress = remember(question.id) {
        Animatable(
            if (newlySelectedQuestionId == question.id || questionOrigin == null) 0f else 1f,
        )
    }
    var returningToQuestionSelection by remember(question.id) { mutableStateOf(false) }
    LaunchedEffect(question.id, returningToQuestionSelection, questionOrigin) {
        onQuestionTransitionStateChanged(true)
        if (questionOrigin == null) {
            onCategoryTabsVisibilityChanged(false)
            return@LaunchedEffect
        }
        if (returningToQuestionSelection) {
            onCategoryTabsVisibilityChanged(true)
            transitionProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(QUESTION_SLIDE_DURATION_MILLIS.toInt()),
            )
            session.reopenQuestionSelection()?.let { questionId ->
                onKartentextPlayedStateChanged(questionId, false)
            }
        } else {
            onCategoryTabsVisibilityChanged(false)
            if (transitionProgress.value < 1f) {
                transitionProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(QUESTION_SLIDE_DURATION_MILLIS.toInt()),
                )
            }
            newlySelectedQuestionId = null
        }
        onQuestionTransitionStateChanged(false)
    }

    BoxWithConstraints(modifier = measuredModifier) {
        val density = LocalDensity.current
        val actionColorTabGap = with(density) { ACTION_COLOR_TAB_GAP_PX.toDp() }
        val actionWidth = (
            maxWidth - (ColorTabWidth + actionColorTabGap) * 2
        ).coerceAtLeast(0.dp).coerceAtMost(MaxActionWidth)
        val originBounds = cardTextBounds[question.id]
        val originalWidth = questionOrigin?.let { origin ->
            maxWidth * origin.widthFraction
        } ?: with(density) { originBounds?.width?.toDp() } ?: 0.dp
        val originalHeight = questionOrigin?.let { origin ->
            maxHeight * origin.heightFraction
        } ?: with(density) { originBounds?.height?.toDp() } ?: 0.dp
        val compactTargetWidth = (maxWidth - SelectedQuestionHorizontalPadding * 2)
            .coerceAtLeast(0.dp)
            .coerceAtMost(560.dp)
        val changesSize = uiState.aktuelleKarte.kartentexte.size < 3 ||
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
        val progress = transitionProgress.value
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

        if (questionOrigin == null || progress < 1f) {
            GamePlayArea(
                spielName = uiState.spielName,
                aktuelleKarte = transitionCard,
                kategorien = uiState.kategorien,
                interactionsEnabled = false,
                hiddenCardTextIds = if (questionOrigin == null) {
                    emptySet()
                } else {
                    setOf(question.id)
                },
                onKartentextBoundsChanged = { cardTextId, bounds ->
                    cardTextBounds[cardTextId] = bounds
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = gameContentHorizontalPadding)
                    .graphicsLayer {
                        alpha = if (questionOrigin == null) 1f else 1f - progress
                    },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = progress },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(targetHeight))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (session.phase == FunFactsPhase.EnterAnswer) {
                    AnswerEntry(
                        nameDrawing = session.draftName.snapshot(),
                        answerDrawing = session.draftAnswer.snapshot(),
                        selectedColorIndex = session.selectedColorIndex,
                        availableColorIndices = session.availableColorIndices,
                        onColorSelected = session::selectColor,
                        onNameStrokeStarted = { point ->
                            session.draftName.startStroke(
                                point,
                                StrokeWidthFractions[session.selectedStrokeWidthIndex],
                            )
                        },
                        onNameStrokeContinued = session.draftName::continueStroke,
                        onClearName = session.draftName::clear,
                        onAnswerStrokeStarted = { point ->
                            session.draftAnswer.startStroke(
                                point,
                                StrokeWidthFractions[session.selectedStrokeWidthIndex],
                            )
                        },
                        onAnswerStrokeContinued = session.draftAnswer::continueStroke,
                        onClearAnswer = session.draftAnswer::clear,
                        selectedStrokeWidthIndex = session.selectedStrokeWidthIndex,
                        onStrokeWidthSelected = session::selectStrokeWidth,
                        categoryName = categoryName,
                        onFinished = session::finishAnswer,
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
                    when (session.phase) {
                        FunFactsPhase.PositionSign -> PositioningActions(
                            revealEnabled = session.players.size >= 2,
                            nextPlayerEnabled = session.canAddPlayer,
                            onNextPlayer = session::nextPlayer,
                            onReveal = session::beginReveal,
                            modifier = Modifier.width(actionWidth),
                        )
                        FunFactsPhase.FinalPositioning -> RevealButtonWithHint(
                            showHint = session.showFirstPlayerHint,
                            onDismissHint = session::dismissFirstPlayerHint,
                            onReveal = session::beginReveal,
                            modifier = Modifier.width(actionWidth),
                        )
                        FunFactsPhase.Revealing -> Button(
                            onClick = session::beginReveal,
                            modifier = Modifier.width(actionWidth),
                        ) { Text("Aufdecken") }
                        FunFactsPhase.Complete -> Button(
                            onClick = {
                                nextCardTransitionScope.launch {
                                    nextCardTransitionRunning = true
                                    nextCardAlpha.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(NEXT_CARD_FADE_OUT_DURATION_MILLIS),
                                    )
                                    awaitingNextCardId = uiState.aktuelleKarte.instanceId
                                    session.startNextRound()
                                    onCategoryTabsVisibilityChanged(true)
                                    onNextCard()
                                    delay(NEXT_CARD_LOAD_TIMEOUT_MILLIS)
                                    if (awaitingNextCardId != null) {
                                        nextCardAlpha.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(NEXT_CARD_FADE_IN_DURATION_MILLIS),
                                        )
                                        awaitingNextCardId = null
                                        nextCardTransitionRunning = false
                                    }
                                }
                            },
                            enabled = !nextCardTransitionRunning,
                            modifier = Modifier.width(actionWidth),
                        ) { Text("Nächste Karte") }
                        else -> Unit
                    }
                }
            }
        }

        if (questionOrigin != null) {
            CardTextPanel(
                kartentext = question.copy(gespielt = false),
                index = questionIndex,
                kartentextCount = uiState.aktuelleKarte.kartentexte.size,
                textPanelColor = questionColor,
                interactionsEnabled = progress >= 1f &&
                    session.players.isEmpty() &&
                    !returningToQuestionSelection,
                onKartentextPlayedStateChanged = { _, _ ->
                    onQuestionTransitionStateChanged(true)
                    onCategoryTabsVisibilityChanged(true)
                    returningToQuestionSelection = true
                },
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = animatedX.roundToPx(),
                            y = animatedY.roundToPx(),
                        )
                    }
                    .width(animatedWidth)
                    .height(animatedHeight),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FunFactsPlayAreaPreview() {
    SpieleabendTheme {
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
    nameDrawing: FunFactsDrawing,
    answerDrawing: FunFactsDrawing,
    selectedColorIndex: Int,
    availableColorIndices: List<Int>,
    onColorSelected: (Int) -> Unit,
    onNameStrokeStarted: (androidx.compose.ui.geometry.Offset) -> Unit,
    onNameStrokeContinued: (androidx.compose.ui.geometry.Offset) -> Unit,
    onClearName: () -> Unit,
    onAnswerStrokeStarted: (androidx.compose.ui.geometry.Offset) -> Unit,
    onAnswerStrokeContinued: (androidx.compose.ui.geometry.Offset) -> Unit,
    onClearAnswer: () -> Unit,
    selectedStrokeWidthIndex: Int,
    onStrokeWidthSelected: (Int) -> Unit,
    categoryName: String,
    onFinished: () -> Unit,
    colorTabsAlpha: Float = 1f,
    colorTabsInteractionsEnabled: Boolean = true,
    modifier: Modifier = Modifier,
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
                        label = "Name:",
                        drawing = nameDrawing,
                        onStrokeStarted = onNameStrokeStarted,
                        onStrokeContinued = onNameStrokeContinued,
                        onClear = onClearName,
                        signHeight = drawingHeight,
                        signColor = SignColors[selectedColorIndex % SignColors.size],
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DrawingPad(
                        label = "Antwort:",
                        drawing = answerDrawing,
                        onStrokeStarted = onAnswerStrokeStarted,
                        onStrokeContinued = onAnswerStrokeContinued,
                        onClear = onClearAnswer,
                        categoryName = categoryName,
                        signHeight = drawingHeight,
                        signColor = SignColors[selectedColorIndex % SignColors.size],
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                StrokeWidthSelector(
                    selectedIndex = selectedStrokeWidthIndex,
                    onSelected = onStrokeWidthSelected,
                    currentStrokeWidth = currentStrokeWidth,
                    selectedColor = SignColors[selectedColorIndex % SignColors.size],
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = strokeWidthSelectorOffset),
                )
            }
            Button(
                onClick = onFinished,
                enabled = nameDrawing.strokes.isNotEmpty() &&
                    answerDrawing.strokes.isNotEmpty() &&
                    selectedColorIndex in availableColorIndices,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FinishButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) { Text("Fertig") }
        }
        if (colorTabsAlpha > 0f) {
            SideColorTabs(
                selectedColorIndex = selectedColorIndex,
                availableColorIndices = availableColorIndices,
                onColorSelected = onColorSelected,
                interactionsEnabled = colorTabsInteractionsEnabled,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = colorTabsAlpha },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnswerEntryPreview() {
    SpieleabendTheme {
        AnswerEntry(
            nameDrawing = PreviewDrawing,
            answerDrawing = PreviewDrawing,
            selectedColorIndex = 0,
            availableColorIndices = SignColors.indices.toList(),
            onColorSelected = {},
            onNameStrokeStarted = {},
            onNameStrokeContinued = {},
            onClearName = {},
            onAnswerStrokeStarted = {},
            onAnswerStrokeContinued = {},
            onClearAnswer = {},
            selectedStrokeWidthIndex = DEFAULT_STROKE_WIDTH_INDEX,
            onStrokeWidthSelected = {},
            categoryName = "Wissen",
            onFinished = {},
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 520.dp),
        )
    }
}

@Composable
private fun SideColorTabs(
    selectedColorIndex: Int,
    availableColorIndices: List<Int>,
    onColorSelected: (Int) -> Unit,
    interactionsEnabled: Boolean = true,
    modifier: Modifier = Modifier,
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
    SpieleabendTheme {
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
    tabHeight: androidx.compose.ui.unit.Dp,
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
                label = "color-tab-scale",
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
                        if (leftSide) {
                            RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
                        } else {
                            RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                        },
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
    SpieleabendTheme {
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
    onStrokeStarted: (androidx.compose.ui.geometry.Offset) -> Unit,
    onStrokeContinued: (androidx.compose.ui.geometry.Offset) -> Unit,
    onClear: () -> Unit,
    signHeight: Dp,
    signColor: Color,
    categoryName: String? = null,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.height(signHeight + DrawingControlsTopAllowance)) {
        var labelSize by remember { mutableStateOf(IntSize.Zero) }
        var deleteSize by remember { mutableStateOf(IntSize.Zero) }
        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val signTopPx = with(density) { DrawingControlsTopAllowance.toPx() }
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
                .onSizeChanged { size -> deleteSize = size },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        ) {
            Text("Löschen")
        }
        categoryName?.takeIf(String::isNotBlank)?.let { name ->
            Text(
                text = name,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(CATEGORY_LABEL_WIDTH_FRACTION)
                    .padding(bottom = 1.dp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrawingPadPreview() {
    SpieleabendTheme {
        DrawingPad(
            label = "Name:",
            drawing = FunFactsDrawing(
                strokes = listOf(
                    listOf(
                        androidx.compose.ui.geometry.Offset(0.1f, 0.2f),
                        androidx.compose.ui.geometry.Offset(0.9f, 0.8f),
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
    currentStrokeWidth: Dp = StrokeWidthPreviewDotSize,
    selectedColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(StrokeWidthButtonSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StrokeWidthFractions.forEachIndexed { index, strokeWidth ->
            val selected = index == selectedIndex
            Surface(
                onClick = { onSelected(index) },
                modifier = Modifier
                    .size(StrokeWidthButtonSize)
                    .semantics {
                        contentDescription = "Stiftdicke ${index + 1}"
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
                                (strokeWidth / DEFAULT_DRAWING_STROKE_WIDTH_FRACTION),
                        ),
                    ) {
                        drawCircle(selectedColor)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StrokeWidthSelectorPreview() {
    SpieleabendTheme {
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
            "Ziehe dein Schild nach oben oder unten an die richtige Stelle.",
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
                Text("Nächster Spieler")
            }
            Button(onClick = onReveal, enabled = revealEnabled, modifier = Modifier.weight(1f)) {
                Text("Aufdecken")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PositioningActionsPreview() {
    SpieleabendTheme { PositioningActions(true, true, {}, {}) }
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
            delay(FIRST_PLAYER_HINT_DURATION_MILLIS)
            onDismissHint()
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ActionButtonHeight),
    ) {
        Button(onClick = onReveal, modifier = Modifier.fillMaxSize()) { Text("Aufdecken") }
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
                        "Erster Spieler: Schild verschieben",
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
    SpieleabendTheme { RevealButtonWithHint(true, {}, {}) }
}

@Composable
private fun PlayerSignStack(
    players: List<FunFactsPlayer>,
    activeSignId: Int?,
    onMoveActiveSign: (Int) -> Unit,
    onToggleRevealedSide: (Int) -> Unit = {},
    revealedSignsCanBeFlipped: Boolean = false,
    modifier: Modifier = Modifier,
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
    SpieleabendTheme {
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
    onToggleRevealedSide: (Int) -> Unit = {},
    flippingEnabled: Boolean = false,
    borderWidth: Dp = 2.dp,
    modifier: Modifier = Modifier,
) {
    val signColor = SignColors[player.colorIndex % SignColors.size]
    val rotation by animateFloatAsState(
        targetValue = if (player.answerVisible) 180f else 0f,
        label = "Schild aufdecken",
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
                .pointerInput(player.id, active) {
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
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = flippingEnabled && player.revealed,
                ) { onToggleRevealedSide(player.id) }
                .semantics {
                    contentDescription = when {
                        active -> "Aktives Schild, vertikal verschiebbar"
                        flippingEnabled && player.revealed && player.answerVisible ->
                            "Antwortseite, antippen für den Namen"
                        flippingEnabled && player.revealed ->
                            "Namensseite, antippen für die Antwort"
                        else -> "Schild"
                    }
                },
            shape = WideCaretShape,
            color = Color.Transparent,
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
    SpieleabendTheme {
        PlayerSign(
            player = FunFactsPlayer(0, PreviewDrawing, PreviewDrawing, 0),
            active = true,
            onMove = {},
        )
    }
}

private val WideCaretShape = GenericShape { size, _ ->
    val vertices = listOf(
        Offset(0f, size.height * 0.28f),
        Offset(size.width * 0.5f, 0f),
        Offset(size.width, size.height * 0.28f),
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

private val SignColors = listOf(
    Color(0xFFEF5350),
    Color(0xFFFB8C00),
    Color(0xFFFDD835),
    Color(0xFF66BB6A),
    Color(0xFF26A69A),
    Color(0xFF29B6F6),
    Color(0xFF7986CB),
    Color(0xFFBA68C8),
    Color(0xFFEC407A),
    Color(0xFFA1887F),
)

@Composable
private fun DrawingCanvas(
    drawing: FunFactsDrawing,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawing.strokes.forEachIndexed { index, stroke ->
            if (stroke.isEmpty()) return@forEachIndexed
            val strokeWidth = size.minDimension * (
                drawing.strokeWidthFractions.getOrNull(index)
                    ?: DEFAULT_DRAWING_STROKE_WIDTH_FRACTION
            )
            val scaledPoints = stroke.map { point ->
                androidx.compose.ui.geometry.Offset(
                    x = point.x * size.width,
                    y = point.y * size.height,
                )
            }
            if (scaledPoints.size == 1) {
                drawCircle(
                    color = Color.Black,
                    radius = strokeWidth / 2f,
                    center = scaledPoints.single(),
                )
            } else {
                scaledPoints.zipWithNext().forEach { (start, end) ->
                    drawLine(
                        color = Color.Black,
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
    SpieleabendTheme {
        DrawingCanvas(
            drawing = PreviewDrawing,
            modifier = Modifier.size(180.dp),
        )
    }
}

private fun androidx.compose.ui.geometry.Offset.normalized(
    width: Int,
    height: Int,
): androidx.compose.ui.geometry.Offset =
    androidx.compose.ui.geometry.Offset(
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
            androidx.compose.ui.geometry.Offset(0.2f, 0.2f),
            androidx.compose.ui.geometry.Offset(0.8f, 0.8f),
        ),
        listOf(
            androidx.compose.ui.geometry.Offset(0.8f, 0.2f),
            androidx.compose.ui.geometry.Offset(0.2f, 0.8f),
        ),
    ),
)

private val ColorTabWidth = 24.dp
private val ColorTabSpacing = 10.dp
private val ActionButtonHeight = 40.dp
private val MaxActionWidth = 580.dp
private val SelectedQuestionHorizontalPadding = 44.dp
private val SelectedQuestionCompactHeight = 112.dp
private val AssignedColorTabColor = Color(0xFF414854)
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
private const val SIGN_BORDER_DARKENING_FACTOR = 0.78f
private const val FIRST_PLAYER_HINT_DURATION_MILLIS = 5_000L
private const val QUESTION_SLIDE_DURATION_MILLIS = 480L
private const val NEXT_CARD_FADE_OUT_DURATION_MILLIS = 180
private const val NEXT_CARD_FADE_IN_DURATION_MILLIS = 220
private const val NEXT_CARD_LOAD_TIMEOUT_MILLIS = 1_500L
