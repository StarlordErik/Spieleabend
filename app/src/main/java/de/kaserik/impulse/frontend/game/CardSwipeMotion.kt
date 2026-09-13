package de.kaserik.impulse.frontend.game

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

internal enum class CardSwipePhase { Idle, Dragging, Returning, Departing, AwaitingCard }

@Stable
internal class CardSwipeMotion(initialCard: GameCardUiModel) {
    var displayedCard by mutableStateOf(initialCard)
        private set
    var overlayCard by mutableStateOf<GameCardUiModel?>(null)
        private set
    private var underlayCard by mutableStateOf<GameCardUiModel?>(null)
    var path by mutableStateOf<CardSwipePath?>(null)
        private set
    var phase by mutableStateOf(CardSwipePhase.Idle)
        private set
    var distance by mutableFloatStateOf(0f)
        private set
    private var travelDistance = 0f
    private var dragOffset = Offset.Zero

    val idle: Boolean get() = phase == CardSwipePhase.Idle
    val movingCard: GameCardUiModel get() = overlayCard ?: displayedCard
    val stationaryCard: GameCardUiModel? get() = if (overlayCard != null) displayedCard else underlayCard
    val movingOffset: Offset
        get() = if (overlayCard == null && underlayCard == null) {
            Offset.Zero
        } else {
            path?.direction?.times(
                if (overlayCard != null) distance - travelDistance else distance,
            ) ?: Offset.Zero
        }

    fun updateCurrentCard(card: GameCardUiModel) {
        if (displayedCard.instanceId == card.instanceId) displayedCard = card
    }

    fun begin(swipePath: CardSwipePath, previousCard: GameCardUiModel?, offscreenDistance: Float): Boolean {
        if (!idle || (swipePath.target == CardSwipeTarget.Previous && previousCard == null)) return false
        path = swipePath
        travelDistance = offscreenDistance
        overlayCard = previousCard.takeIf { swipePath.target == CardSwipeTarget.Previous }
        distance = 0f
        dragOffset = Offset.Zero
        phase = CardSwipePhase.Dragging
        return true
    }

    fun previewNextCard(card: GameCardUiModel) {
        if (phase == CardSwipePhase.Dragging && path?.target != CardSwipeTarget.Previous) underlayCard = card
    }

    fun dragBy(drag: Offset) {
        if (phase != CardSwipePhase.Dragging) return
        dragOffset += drag
        val projected = path?.project(dragOffset) ?: return
        distance = projected.coerceIn(0f, travelDistance)
    }

    suspend fun cancel() {
        phase = CardSwipePhase.Returning
        animateDistance(0f, RETURN_ANIMATION_MILLIS)
        reset()
    }

    suspend fun commit(onTargetSelected: suspend (CardSwipeTarget) -> Unit) {
        if (phase != CardSwipePhase.Dragging) return
        val target = path?.target ?: return
        phase = CardSwipePhase.Departing
        animateDistance(travelDistance, OUTGOING_ANIMATION_MILLIS)
        phase = CardSwipePhase.AwaitingCard
        onTargetSelected(target)
    }

    suspend fun showCard(card: GameCardUiModel, defaultPath: CardSwipePath, offscreenDistance: Float) {
        if (card.instanceId == displayedCard.instanceId) {
            updateCurrentCard(card)
            return
        }
        if (phase == CardSwipePhase.AwaitingCard && overlayCard != null) {
            displayedCard = card
            reset()
            return
        }
        underlayCard = card
        if (phase != CardSwipePhase.AwaitingCard) {
            // This also animates draws initiated by manually marking a text as played.
            path = defaultPath
            overlayCard = null
            distance = 0f
            travelDistance = offscreenDistance
            phase = CardSwipePhase.Departing
            animateDistance(travelDistance, OUTGOING_ANIMATION_MILLIS)
        }
        displayedCard = card
        reset()
    }

    private suspend fun animateDistance(target: Float, durationMillis: Int) {
        animate(distance, target, animationSpec = tween(durationMillis)) { value, _ -> distance = value }
    }

    private fun reset() {
        distance = 0f
        path = null
        overlayCard = null
        underlayCard = null
        phase = CardSwipePhase.Idle
    }
}

private const val OUTGOING_ANIMATION_MILLIS = 180
private const val RETURN_ANIMATION_MILLIS = 160
