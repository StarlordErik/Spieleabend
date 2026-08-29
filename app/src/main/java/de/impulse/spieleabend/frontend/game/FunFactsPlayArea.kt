@file:Suppress("CyclomaticComplexMethod", "LongMethod", "MagicNumber", "TooManyFunctions")

package de.impulse.spieleabend.frontend.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme
import kotlin.math.abs
import kotlinx.coroutines.delay

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
    onNextCard: () -> Unit = {},
) {
    var fadingPreviousContent by remember { mutableStateOf(false) }
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
                    fadingPreviousContent = true
                    session.selectQuestion(cardTextId)
                }
                onKartentextPlayedStateChanged(cardTextId, played)
            },
            modifier = modifier,
        )
        return
    }

    val question = uiState.aktuelleKarte.kartentexte.firstOrNull {
        it.id == session.selectedQuestionId
    } ?: return
    val questionIndex = uiState.aktuelleKarte.kartentexte.indexOf(question)
    val questionColor = uiState.aktuelleKarte.textPanelColors(uiState.kategorien)[questionIndex]
    val transitionCard = uiState.aktuelleKarte.copy(
        kartentexte = uiState.aktuelleKarte.kartentexte.map { cardText ->
            if (cardText.id == question.id) cardText.copy(gespielt = false) else cardText
        },
    )
    var roundContentVisible by remember(question.id) { mutableStateOf(false) }
    LaunchedEffect(question.id) {
        delay(QUESTION_SLIDE_DURATION_MILLIS)
        roundContentVisible = true
    }

    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = fadingPreviousContent,
            exit = fadeOut(tween(QUESTION_SLIDE_DURATION_MILLIS.toInt())),
        ) {
            GamePlayArea(
                spielName = uiState.spielName,
                aktuelleKarte = transitionCard,
                kategorien = uiState.kategorien,
                modifier = Modifier.fillMaxSize(),
            )
        }
        LaunchedEffect(fadingPreviousContent) {
            if (fadingPreviousContent) fadingPreviousContent = false
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
        SelectedQuestion(
            text = question.text,
            backgroundColor = questionColor,
            canChange = session.players.isEmpty(),
            modifier = Modifier.padding(horizontal = 44.dp),
            onClick = {
                session.reopenQuestionSelection()?.let { questionId ->
                    onKartentextPlayedStateChanged(questionId, false)
                }
            },
        )

        AnimatedVisibility(
            visible = roundContentVisible,
            modifier = Modifier.weight(1f),
            enter = fadeIn(tween(220)),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (session.phase == FunFactsPhase.EnterAnswer) {
                    AnswerEntry(
                        nameStrokes = session.draftName.strokes,
                        answerStrokes = session.draftAnswer.strokes,
                        selectedColorIndex = session.selectedColorIndex,
                        availableColorIndices = session.availableColorIndices,
                        onColorSelected = session::selectColor,
                        onNameStrokeStarted = session.draftName::startStroke,
                        onNameStrokeContinued = session.draftName::continueStroke,
                        onClearName = session.draftName::clear,
                        onAnswerStrokeStarted = session.draftAnswer::startStroke,
                        onAnswerStrokeContinued = session.draftAnswer::continueStroke,
                        onClearAnswer = session.draftAnswer::clear,
                        onFinished = session::finishAnswer,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    PlayerSignStack(
                        players = session.players,
                        activeSignId = session.activeSignId,
                        onMoveActiveSign = session::moveActiveSign,
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
                        )
                        FunFactsPhase.FinalPositioning -> RevealButtonWithHint(
                            showHint = session.showFirstPlayerHint,
                            onDismissHint = session::dismissFirstPlayerHint,
                            onReveal = session::beginReveal,
                        )
                        FunFactsPhase.Revealing -> Button(
                            onClick = session::beginReveal,
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Aufdecken") }
                        FunFactsPhase.Complete -> Button(
                            onClick = {
                                session.startNextRound()
                                onNextCard()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Nächste Karte") }
                        else -> Unit
                    }
                }
            }
        }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun FunFactsPlayAreaPreview() {
    SpieleabendTheme {
        val session = remember { FunFactsSession().apply { selectQuestion(101) } }
        FunFactsPlayArea(uiState = PreviewUiState, session = session)
    }
}

@Composable
private fun SelectedQuestion(
    text: String,
    backgroundColor: Color,
    canChange: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visible by remember(text) { mutableStateOf(false) }
    LaunchedEffect(text) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(durationMillis = 480),
            initialOffsetY = { height -> height * 4 },
        ) + fadeIn(tween(durationMillis = 260)),
    ) {
        Surface(
            onClick = onClick,
            enabled = canChange,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 112.dp),
            shape = RoundedCornerShape(18.dp),
            color = backgroundColor,
            shadowElevation = 5.dp,
        ) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                Text(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectedQuestionPreview() {
    SpieleabendTheme {
        SelectedQuestion(
            text = "Wie schüchtern bist du?",
            backgroundColor = Color(0xFF9C7AE8),
            canChange = true,
            onClick = {},
        )
    }
}

@Composable
private fun AnswerEntry(
    nameStrokes: List<List<androidx.compose.ui.geometry.Offset>>,
    answerStrokes: List<List<androidx.compose.ui.geometry.Offset>>,
    selectedColorIndex: Int,
    availableColorIndices: List<Int>,
    onColorSelected: (Int) -> Unit,
    onNameStrokeStarted: (androidx.compose.ui.geometry.Offset) -> Unit,
    onNameStrokeContinued: (androidx.compose.ui.geometry.Offset) -> Unit,
    onClearName: () -> Unit,
    onAnswerStrokeStarted: (androidx.compose.ui.geometry.Offset) -> Unit,
    onAnswerStrokeContinued: (androidx.compose.ui.geometry.Offset) -> Unit,
    onClearAnswer: () -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val drawingWidthReduction =
            if (maxWidth < ColorTabCompactWidthBreakpoint) 88.dp else 136.dp
        val drawingWidth = (maxWidth - drawingWidthReduction)
            .coerceAtLeast(0.dp)
            .coerceAtMost(560.dp)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(drawingWidth),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            DrawingPad(
                label = "Name malen",
                strokes = nameStrokes,
                onStrokeStarted = onNameStrokeStarted,
                onStrokeContinued = onNameStrokeContinued,
                onClear = onClearName,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            DrawingPad(
                label = "Antwort malen",
                strokes = answerStrokes,
                onStrokeStarted = onAnswerStrokeStarted,
                onStrokeContinued = onAnswerStrokeContinued,
                onClear = onClearAnswer,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            Button(
                onClick = onFinished,
                enabled = nameStrokes.isNotEmpty() &&
                    answerStrokes.isNotEmpty() &&
                    selectedColorIndex in availableColorIndices,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Fertig") }
        }
        SideColorTabs(
            selectedColorIndex = selectedColorIndex,
            availableColorIndices = availableColorIndices,
            onColorSelected = onColorSelected,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnswerEntryPreview() {
    SpieleabendTheme {
        AnswerEntry(
            nameStrokes = PreviewDrawing.strokes,
            answerStrokes = PreviewDrawing.strokes,
            selectedColorIndex = 0,
            availableColorIndices = SignColors.indices.toList(),
            onColorSelected = {},
            onNameStrokeStarted = {},
            onNameStrokeContinued = {},
            onClearName = {},
            onAnswerStrokeStarted = {},
            onAnswerStrokeContinued = {},
            onClearAnswer = {},
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
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val tabHeight = ((maxHeight - ColorTabSpacing * 6) / 5).coerceAtLeast(0.dp)
        ColorTabColumn(
            indices = 0 until 5,
            selectedColorIndex = selectedColorIndex,
            availableColorIndices = availableColorIndices,
            onColorSelected = onColorSelected,
            leftSide = true,
            tabHeight = tabHeight,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        ColorTabColumn(
            indices = 5 until 10,
            selectedColorIndex = selectedColorIndex,
            availableColorIndices = availableColorIndices,
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
            val scale by animateFloatAsState(
                targetValue = if (index == selectedColorIndex && available) 1.1f else 1f,
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
                        alpha = if (available) 1f else 0.45f
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
                    .background(SignColors[index])
                    .clickable(enabled = available) { onColorSelected(index) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorTabColumnPreview() {
    SpieleabendTheme {
        ColorTabColumn(0 until 5, 2, listOf(0, 2, 4), {}, true, 72.dp)
    }
}

@Composable
private fun DrawingPad(
    label: String,
    strokes: List<List<androidx.compose.ui.geometry.Offset>>,
    onStrokeStarted: (androidx.compose.ui.geometry.Offset) -> Unit,
    onStrokeContinued: (androidx.compose.ui.geometry.Offset) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                modifier = Modifier.weight(1f),
                color = Color.Black,
                style = MaterialTheme.typography.labelLarge,
            )
            OutlinedButton(onClick = onClear, enabled = strokes.isNotEmpty()) { Text("Löschen") }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(SIGN_ASPECT_RATIO),
            shape = WideCaretShape,
            color = Color.White,
            border = BorderStroke(2.dp, Color.Black),
        ) {
            DrawingCanvas(
                drawing = FunFactsDrawing(strokes),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { position ->
                                onStrokeStarted(position.normalized(size.width, size.height))
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                onStrokeContinued(change.position.normalized(size.width, size.height))
                            },
                        )
                    },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrawingPadPreview() {
    SpieleabendTheme {
        DrawingPad(
            label = "Name malen",
            strokes = listOf(
                listOf(
                    androidx.compose.ui.geometry.Offset(0.1f, 0.2f),
                    androidx.compose.ui.geometry.Offset(0.9f, 0.8f),
                ),
            ),
            onStrokeStarted = {},
            onStrokeContinued = {},
            onClear = {},
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 240.dp)
                .padding(12.dp),
        )
    }
}

@Composable
private fun PositioningActions(
    revealEnabled: Boolean,
    nextPlayerEnabled: Boolean,
    onNextPlayer: () -> Unit,
    onReveal: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            "Ziehe dein Schild nach oben oder unten an die richtige Stelle.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
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
) {
    LaunchedEffect(showHint) {
        if (showHint) {
            delay(FIRST_PLAYER_HINT_DURATION_MILLIS)
            onDismissHint()
        }
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onReveal, modifier = Modifier.fillMaxWidth()) { Text("Aufdecken") }
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
                color = Color.Black.copy(alpha = 0.82f),
                shadowElevation = 2.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "Erster Spieler: Schild noch einmal verschieben · Antippen zum Schließen",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = Color.White,
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
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-10).dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(players, key = FunFactsPlayer::id) { player ->
            PlayerSign(
                player = player,
                active = player.id == activeSignId,
                onMove = onMoveActiveSign,
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .aspectRatio(SIGN_ASPECT_RATIO),
            )
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
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (player.revealed) 180f else 0f,
        label = "Schild aufdecken",
    )
    val dragThreshold = with(LocalDensity.current) { 32.dp.toPx() }
    val flipping = rotation > 0.5f && rotation < 179.5f
    Surface(
        modifier = modifier
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
            .semantics {
                contentDescription =
                    if (active) "Aktives Schild, vertikal verschiebbar" else "Schild"
            },
        shape = WideCaretShape,
        color = Color.Transparent,
        shadowElevation = if (flipping) 0.dp else if (active) 8.dp else 4.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { rotationY = rotation }
                .clip(WideCaretShape)
                .background(SignColors[player.colorIndex % SignColors.size])
                .then(
                    if (active) {
                        Modifier.border(3.dp, Color.Black, WideCaretShape)
                    } else {
                        Modifier
                    },
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
    moveTo(0f, size.height * 0.28f)
    lineTo(size.width * 0.5f, 0f)
    lineTo(size.width, size.height * 0.28f)
    lineTo(size.width, size.height)
    lineTo(size.width * 0.5f, size.height * 0.72f)
    lineTo(0f, size.height)
    close()
}

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
        val strokeWidth = size.minDimension * DRAWING_STROKE_WIDTH_FRACTION
        drawing.strokes.forEach { stroke ->
            if (stroke.isEmpty()) return@forEach
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
private val ColorTabCompactWidthBreakpoint = 404.dp
private const val SIGN_ASPECT_RATIO = 3.5f
private const val DRAWING_STROKE_WIDTH_FRACTION = 0.045f
private const val FIRST_PLAYER_HINT_DURATION_MILLIS = 5_000L
private const val QUESTION_SLIDE_DURATION_MILLIS = 480L
