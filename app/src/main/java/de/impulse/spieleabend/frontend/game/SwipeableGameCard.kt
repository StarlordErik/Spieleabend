package de.impulse.spieleabend.frontend.game

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
@Suppress("CyclomaticComplexMethod", "LongMethod")
internal fun SwipeableGameCard(
    cardInstanceId: Long,
    swipeRegions: Collection<SwipeRegion>,
    previousEnabled: Boolean,
    onHighlightedTargetChanged: (CardSwipeTarget?) -> Unit,
    onTargetSelected: (CardSwipeTarget) -> Unit,
    modifier: Modifier = Modifier,
    onInteractionStateChanged: (Boolean) -> Unit = {},
    content: @Composable (idleEffectsEnabled: Boolean) -> Unit,
) {
    var translationX by remember { mutableFloatStateOf(0f) }
    var cardWidth by remember { mutableIntStateOf(0) }
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var startPositionInRoot by remember { mutableStateOf(Offset.Unspecified) }
    var highlightedTarget by remember { mutableStateOf<CardSwipeTarget?>(null) }
    var interactionLocked by remember { mutableStateOf(false) }
    var awaitingCardId by remember { mutableStateOf<Long?>(null) }
    var outgoingDirection by remember { mutableStateOf<CardSwipeDirection?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(interactionLocked) {
        onInteractionStateChanged(interactionLocked)
    }

    fun updateHighlight(target: CardSwipeTarget?) {
        if (highlightedTarget != target) {
            highlightedTarget = target
            onHighlightedTargetChanged(target)
        }
    }

    LaunchedEffect(cardInstanceId, awaitingCardId, cardWidth) {
        val oldCardId = awaitingCardId
        val direction = outgoingDirection
        val incomingCardReady = oldCardId != null && oldCardId != cardInstanceId
        if (incomingCardReady && direction != null && cardWidth > 0) {
            translationX = -direction.sign * cardWidth * OFFSCREEN_DISTANCE_FACTOR
            animate(
                initialValue = translationX,
                targetValue = 0f,
                animationSpec = tween(INCOMING_ANIMATION_MILLIS),
            ) { value, _ -> translationX = value }
            awaitingCardId = null
            outgoingDirection = null
            interactionLocked = false
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                coordinates = layoutCoordinates
                cardWidth = layoutCoordinates.size.width
            }
            .pointerInput(cardInstanceId, swipeRegions, previousEnabled, interactionLocked, cardWidth) {
                if (interactionLocked) return@pointerInput

                detectHorizontalDragGestures(
                    onDragStart = { localPosition ->
                        startPositionInRoot = coordinates?.localToRoot(localPosition) ?: Offset.Unspecified
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        translationX += dragAmount
                        val direction = cardSwipeDirection(
                            dragOffsetX = translationX,
                            velocityX = 0f,
                            velocityThresholdPxPerSecond = SWIPE_VELOCITY_THRESHOLD,
                        )
                        val target = if (
                            direction == null ||
                            !startPositionInRoot.x.isFinite() ||
                            !startPositionInRoot.y.isFinite()
                        ) {
                            null
                        } else {
                            resolveCardSwipeTarget(
                                startPositionInRoot = startPositionInRoot,
                                direction = direction,
                                regions = swipeRegions,
                                previousEnabled = previousEnabled,
                            )
                        }
                        updateHighlight(target)
                    },
                    onDragCancel = {
                        updateHighlight(null)
                        scope.launch {
                            animate(
                                initialValue = translationX,
                                targetValue = 0f,
                                animationSpec = tween(RETURN_ANIMATION_MILLIS),
                            ) { value, _ -> translationX = value }
                        }
                    },
                    onDragEnd = {
                        val target = highlightedTarget
                        val direction = cardSwipeDirection(
                            dragOffsetX = translationX,
                            velocityX = 0f,
                            velocityThresholdPxPerSecond = SWIPE_VELOCITY_THRESHOLD,
                        )
                        val shouldCommit = target != null && direction != null &&
                            isCardSwipeCommitThresholdReached(
                                dragOffsetX = translationX,
                                velocityX = 0f,
                                cardWidthPx = cardWidth.toFloat(),
                                minimumDistancePx = MinimumSwipeDistance.toPx(),
                                distanceFraction = SWIPE_DISTANCE_FRACTION,
                                velocityThresholdPxPerSecond = SWIPE_VELOCITY_THRESHOLD,
                            )
                        updateHighlight(null)

                        if (shouldCommit) {
                            interactionLocked = true
                            outgoingDirection = direction
                            awaitingCardId = cardInstanceId
                            scope.launch {
                                animate(
                                    initialValue = translationX,
                                    targetValue = direction.sign * cardWidth * OFFSCREEN_DISTANCE_FACTOR,
                                    animationSpec = tween(OUTGOING_ANIMATION_MILLIS),
                                ) { value, _ -> translationX = value }
                                onTargetSelected(target)
                            }
                        } else {
                            scope.launch {
                                animate(
                                    initialValue = translationX,
                                    targetValue = 0f,
                                    animationSpec = tween(RETURN_ANIMATION_MILLIS),
                                ) { value, _ -> translationX = value }
                            }
                        }
                    },
                )
            }
            .graphicsLayer { this.translationX = translationX },
        contentAlignment = Alignment.Center,
    ) {
        content(!interactionLocked && abs(translationX) < 1f)
    }
}

@Preview(showBackground = true)
@Composable
private fun SwipeableGameCardPreview() {
    SpieleabendTheme {
        SwipeableGameCard(
            cardInstanceId = 1,
            swipeRegions = emptyList(),
            previousEnabled = false,
            onHighlightedTargetChanged = {},
            onInteractionStateChanged = {},
            onTargetSelected = {},
            modifier = Modifier.width(240.dp).height(320.dp),
        ) {
            Surface(modifier = Modifier.fillMaxSize()) { Text("Karte") }
        }
    }
}

private val MinimumSwipeDistance = 72.dp
private const val SWIPE_DISTANCE_FRACTION = 0.25f
private const val SWIPE_VELOCITY_THRESHOLD = 900f
private const val OFFSCREEN_DISTANCE_FACTOR = 1.4f
private const val OUTGOING_ANIMATION_MILLIS = 180
private const val INCOMING_ANIMATION_MILLIS = 220
private const val RETURN_ANIMATION_MILLIS = 160
