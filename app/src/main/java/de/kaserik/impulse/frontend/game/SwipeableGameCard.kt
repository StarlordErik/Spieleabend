package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

@Composable
internal fun SwipeableGameCard(
    card: GameCardUiModel,
    controls: CardSwipeControls,
    modifier: Modifier = Modifier,
    content: @Composable (GameCardUiModel, idleEffectsEnabled: Boolean) -> Unit,
) {
    val motion = remember { CardSwipeMotion(card) }
    val latestControls by rememberUpdatedState(controls)
    var cardBounds by remember { mutableStateOf(Rect.Zero) }
    val geometryReady = !cardBounds.isEmpty
    val scope = rememberCoroutineScope()
    var animationJob by remember { mutableStateOf<Job?>(null) }
    var preparation by remember { mutableStateOf<Deferred<PreparedCardSwipe?>?>(null) }
    val minimumDistance = with(LocalDensity.current) { MinimumSwipeDistance.toPx() }

    fun pathFor(target: CardSwipeTarget): CardSwipePath = cardSwipePathForTarget(
        target, latestControls.swipeRegions, latestControls.screenBounds.center,
    )

    fun travelDistance(path: CardSwipePath): Float = path.offscreenDistance(
        cardBounds, latestControls.screenBounds.takeUnless { it.isEmpty } ?: cardBounds,
    )

    fun finishGesture(commit: Boolean) {
        if (motion.phase != CardSwipePhase.Dragging) return
        animationJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            if (commit) {
                val prepared = preparation?.await()
                motion.commit { target ->
                    if (prepared != null) prepared.commit() else latestControls.navigationActions.select(target)
                }
            } else {
                preparation?.cancel()
                motion.cancel()
            }
        }
    }

    fun beginGesture(path: CardSwipePath): Boolean {
        if (!motion.begin(path, latestControls.previousCard, travelDistance(path))) return false
        preparation = if (path.target == CardSwipeTarget.Previous) null else {
            scope.async(start = CoroutineStart.UNDISPATCHED) {
                latestControls.navigationActions.prepareNextCard(path.target)?.also { motion.previewNextCard(it.card) }
            }
        }
        return true
    }

    SideEffect { motion.updateCurrentCard(card) }
    LaunchedEffect(motion.idle, motion.path?.target) {
        latestControls.onInteractionStateChanged(!motion.idle)
        latestControls.onHighlightedTargetChanged(motion.path?.target)
    }
    LaunchedEffect(card.instanceId, geometryReady) {
        if (!geometryReady || card.instanceId == motion.displayedCard.instanceId) return@LaunchedEffect
        animationJob?.cancelAndJoin()
        val path = pathFor(latestControls.nextDrawTarget)
        motion.showCard(card, path, travelDistance(path))
    }
    LaunchedEffect(controls.swipeRequest?.id, motion.idle, geometryReady) {
        val request = latestControls.swipeRequest ?: return@LaunchedEffect
        if (!geometryReady || !motion.idle) return@LaunchedEffect
        val path = pathFor(request.target)
        if (beginGesture(path)) finishGesture(commit = true)
        latestControls.onSwipeRequestConsumed(request.id)
    }

    val currentHandlers by rememberUpdatedState(
        swipeGestureHandlers(motion, latestControls, cardBounds, minimumDistance, ::beginGesture, ::finishGesture),
    )
    DisposableEffect(controls.gestureInput) {
        val input = controls.gestureInput
        var activeHandlers: CardSwipeGestureHandlers? = null
        input.handlers = CardSwipeGestureHandlers(
            onStart = {
                activeHandlers = currentHandlers
                activeHandlers.onStart(it)
            },
            onDrag = { activeHandlers?.onDrag?.invoke(it) },
            onEnd = { activeHandlers?.onEnd?.invoke() },
            onCancel = { activeHandlers?.onCancel?.invoke() },
        )
        onDispose {
            input.handlers = null
            latestControls.onHighlightedTargetChanged(null)
            latestControls.onInteractionStateChanged(false)
        }
    }

    CardSwipeLayers(
        motion = motion,
        modifier = modifier.onGloballyPositioned { cardBounds = it.boundsInRoot() },
        content = content,
    )
}

@Composable
private fun CardSwipeLayers(
    motion: CardSwipeMotion,
    modifier: Modifier,
    content: @Composable (GameCardUiModel, Boolean) -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        motion.stationaryCard?.let { stationary ->
            Box(Modifier.fillMaxSize().clearAndSetSemantics {}) { content(stationary, false) }
        }
        Box(
            Modifier.fillMaxSize().graphicsLayer {
                translationX = motion.movingOffset.x
                translationY = motion.movingOffset.y
            },
        ) {
            content(motion.movingCard, motion.idle)
        }
    }
}

private fun swipeGestureHandlers(
    motion: CardSwipeMotion,
    controls: CardSwipeControls,
    cardBounds: Rect,
    minimumDistance: Float,
    beginGesture: (CardSwipePath) -> Boolean,
    finishGesture: (Boolean) -> Unit,
): CardSwipeGestureHandlers {
    var startPosition = Offset.Unspecified
    var initialDrag = Offset.Zero
    var acceptingGesture = false
    return CardSwipeGestureHandlers(
        onStart = {
            startPosition = it
            initialDrag = Offset.Zero
            acceptingGesture = motion.idle && !cardBounds.isEmpty
        },
        onDrag = { drag ->
            if (acceptingGesture) {
                if (motion.idle) {
                    initialDrag += drag
                    val path = resolveCardSwipePath(
                        startPosition, initialDrag, controls.screenBounds.center,
                        controls.swipeRegions, controls.previousEnabled,
                    )
                    if (path != null && beginGesture(path)) {
                        motion.dragBy(initialDrag)
                    }
                } else {
                    motion.dragBy(drag)
                }
            }
        },
        onEnd = {
            finishGesture(isCardSwipeCommitThresholdReached(
                dragOffsetX = motion.distance,
                velocityX = 0f,
                cardWidthPx = cardBounds.width,
                minimumDistancePx = minimumDistance,
                distanceFraction = SWIPE_DISTANCE_FRACTION,
                velocityThresholdPxPerSecond = SWIPE_VELOCITY_THRESHOLD,
            ))
            acceptingGesture = false
        },
        onCancel = {
            finishGesture(false)
            acceptingGesture = false
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SwipeableGameCardPreview() {
    ImpulseTheme {
        SwipeableGameCard(
            card = PreviewUiState.aktuelleKarte,
            controls = CardSwipeControls(),
            modifier = Modifier.width(240.dp).height(320.dp),
        ) { _, _ ->
            Surface(modifier = Modifier.fillMaxSize()) { Text(stringResource(R.string.card)) }
        }
    }
}

private val MinimumSwipeDistance = 72.dp
private const val SWIPE_DISTANCE_FRACTION = 0.25f
private const val SWIPE_VELOCITY_THRESHOLD = 900f

@Preview(showBackground = true)
@Composable
private fun CardSwipeLayersPreview() {
    ImpulseTheme {
        val card = PreviewUiState.aktuelleKarte
        val motion = remember(card) {
            CardSwipeMotion(card).apply {
                begin(CardSwipePath(CardSwipeTarget.Previous, Offset(-0.6f, -0.8f)), card.copy(instanceId = 0), 400f)
                dragBy(Offset(-132f, -176f))
            }
        }
        CardSwipeLayers(motion, Modifier.width(240.dp).height(320.dp)) { visibleCard, _ ->
            GameCard(visibleCard.kartentexte, visibleCard.instanceId, Modifier.fillMaxSize(), idleEffectsEnabled = false)
        }
    }
}
