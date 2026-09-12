package de.kaserik.impulse.frontend.game

import kotlinx.coroutines.delay

internal class CardIdleTimer(
    initialRemainingMillis: Long = SINGLE_CARD_IDLE_PLAY_DELAY_MILLIS,
    private val nowMillis: () -> Long = { System.nanoTime() / NANOS_PER_MILLISECOND },
) {
    private var remainingAtResume = initialRemainingMillis
    private var resumedAt: Long? = null
    private var expired = false

    val remainingMillis: Long
        get() = (remainingAtResume - (resumedAt?.let { nowMillis() - it } ?: 0L)).coerceAtLeast(0L)

    suspend fun awaitTimeout(): Boolean {
        if (expired) return false
        resumedAt = nowMillis()
        try {
            delay(remainingAtResume)
            expired = true
            return true
        } finally {
            remainingAtResume = remainingMillis
            resumedAt = null
        }
    }
}

internal const val SINGLE_CARD_IDLE_PLAY_DELAY_MILLIS = 60_000L
private const val NANOS_PER_MILLISECOND = 1_000_000L
