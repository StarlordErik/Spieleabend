package de.kaserik.impulse.frontend.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardIdleTimerTest {
    @Test
    fun newTimerStartsWithSixtySeconds() {
        assertEquals(60_000L, CardIdleTimer().remainingMillis)
    }

    @Test
    fun dialogsPauseTheRemainingTimeAcrossRepeatedInterruptions() = runBlocking {
        val clock = TestClock()
        val timer = CardIdleTimer(nowMillis = { clock.now })
        consumeActiveTime(timer, clock, 10_000L)
        assertEquals(50_000L, timer.remainingMillis)

        clock.now += 120_000L
        assertEquals(50_000L, timer.remainingMillis)
        consumeActiveTime(timer, clock, 20_000L)
        assertEquals(30_000L, timer.remainingMillis)

        clock.now += 90_000L
        assertEquals(30_000L, timer.remainingMillis)
    }

    @Test
    fun sixtySecondsOfVisibleTimeAreRequiredAndTimeoutIsDeliveredOnlyOnce() = runBlocking {
        val clock = TestClock()
        val timer = CardIdleTimer(nowMillis = { clock.now })
        consumeActiveTime(timer, clock, 59_999L)
        assertEquals(1L, timer.remainingMillis)
        clock.now += 300_000L
        assertEquals(1L, timer.remainingMillis)

        consumeActiveTime(timer, clock, 1L)
        assertEquals(0L, timer.remainingMillis)
        assertTrue(timer.awaitTimeout())
        assertFalse(timer.awaitTimeout())
    }

    @Test
    fun reopeningTheGameCreatesAFullTimerInsteadOfRestoringTheOldRemainder() = runBlocking {
        val clock = TestClock()
        val previousTimer = CardIdleTimer(nowMillis = { clock.now })
        consumeActiveTime(previousTimer, clock, 45_000L)

        assertEquals(15_000L, previousTimer.remainingMillis)
        assertEquals(60_000L, CardIdleTimer(nowMillis = { clock.now }).remainingMillis)
    }

    @Test
    fun cancellationAtTheDeadlineDefersMarkingUntilTheCardIsVisibleAgain() = runBlocking {
        val clock = TestClock()
        val timer = CardIdleTimer(nowMillis = { clock.now })
        var marked = false
        val job = launch(start = CoroutineStart.UNDISPATCHED) { marked = timer.awaitTimeout() }
        clock.now += 65_000L
        job.cancelAndJoin()

        assertFalse(marked)
        assertEquals(0L, timer.remainingMillis)
        assertTrue(timer.awaitTimeout())
    }

    private suspend fun CoroutineScope.consumeActiveTime(timer: CardIdleTimer, clock: TestClock, millis: Long) {
        val job = launch(start = CoroutineStart.UNDISPATCHED) { timer.awaitTimeout() }
        clock.now += millis
        job.cancelAndJoin()
    }

    private class TestClock {
        var now = 0L
    }
}
