package de.kaserik.impulse.frontend.game

import de.kaserik.impulse.domain.usecase.DrawNextCardUseCase
import de.kaserik.impulse.domain.usecase.PlannedCardDraw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Accessed on the UI thread; database reads and card planning run in the use case's background context.
internal class CardDrawPreloader(
    private val scope: CoroutineScope,
    private val drawNextCard: DrawNextCardUseCase,
    private val gameId: Int,
    private val idleDelayMillis: Long = CARD_PREFETCH_IDLE_DELAY_MILLIS,
) {
    private var cards = emptyMap<Int?, PlannedCardDraw>()
    private var fullyPrepared = false
    private var pendingLoad: Job? = null
    private var active = false
    private var pointerPressed = false
    private var interactionBlocked = false

    var version = 0L
        private set

    fun setActive(value: Boolean) {
        active = value
        if (!value) pointerPressed = false
        invalidate()
    }

    fun onPointerInput(pressed: Boolean) {
        pointerPressed = pressed
        schedule()
    }

    fun setInteractionBlocked(blocked: Boolean) {
        interactionBlocked = blocked
        schedule()
    }

    fun invalidate() {
        version++
        cards = emptyMap()
        fullyPrepared = false
        schedule()
    }

    suspend fun prepare(categoryId: Int?): PlannedCardDraw {
        cards[categoryId]?.let { return it }
        val initialVersion = version
        val draw = drawNextCard.prepare(gameId, categoryId)
        if (version == initialVersion) cards = cards + (categoryId to draw)
        return draw
    }

    private fun schedule() {
        pendingLoad?.cancel()
        if (!active || pointerPressed || interactionBlocked || fullyPrepared) return
        val initialVersion = version
        pendingLoad = scope.launch {
            delay(idleDelayMillis)
            val prepared = drawNextCard.prepareAll(gameId)
            if (version == initialVersion) {
                cards = prepared + cards
                fullyPrepared = true
            }
        }
    }
}

private const val CARD_PREFETCH_IDLE_DELAY_MILLIS = 300L
