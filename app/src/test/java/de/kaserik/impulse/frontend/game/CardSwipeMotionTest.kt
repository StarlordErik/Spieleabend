package de.kaserik.impulse.frontend.game

import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardSwipeMotionTest {
    private val current = GameCardUiModel(2, emptyList())
    private val previous = GameCardUiModel(1, emptyList())
    private val next = GameCardUiModel(3, emptyList())
    private val previousPath = CardSwipePath(CardSwipeTarget.Previous, Offset(-0.6f, -0.8f))

    @Test
    fun previousFollowsTheFingerOverTheStationaryCurrentCardAndCanBeCancelled() = runBlocking(TestFrameClock()) {
        val motion = CardSwipeMotion(current)
        assertTrue(motion.begin(previousPath, previous, 500f))
        motion.dragBy(Offset(-60f, -80f))

        assertEquals(current, motion.stationaryCard)
        assertEquals(previous, motion.movingCard)
        assertOffset(Offset(240f, 320f), motion.movingOffset)
        motion.dragBy(Offset(-80f, 60f))
        assertOffset(Offset(240f, 320f), motion.movingOffset)
        assertFalse(motion.begin(CardSwipePath(CardSwipeTarget.Random, Offset(-1f, 0f)), null, 500f))

        motion.cancel()
        assertEquals(current, motion.movingCard)
        assertOffset(Offset.Zero, motion.movingOffset)
        assertNull(motion.stationaryCard)
        assertTrue(motion.idle)
    }

    @Test
    fun previousCommitsOnlyOnceAndStaysOnTopUntilTheHistoryUpdateArrives() = runBlocking(TestFrameClock()) {
        val motion = CardSwipeMotion(current)
        val selections = mutableListOf<CardSwipeTarget>()
        motion.begin(previousPath, previous, 500f)
        motion.dragBy(Offset(-90f, -120f))
        motion.commit { selections.add(it) }
        motion.commit { selections.add(it) }

        assertEquals(listOf(CardSwipeTarget.Previous), selections)
        assertEquals(current, motion.stationaryCard)
        assertEquals(previous, motion.movingCard)
        assertOffset(Offset.Zero, motion.movingOffset)
        assertFalse(motion.idle)

        motion.showCard(previous, previousPath, 500f)
        assertTrue(motion.idle)
        assertEquals(previous, motion.movingCard)
        assertNull(motion.stationaryCard)
        assertOffset(Offset.Zero, motion.movingOffset)
    }

    @Test
    fun reversingPastTheStartKeepsTheCardOnItsTrack() {
        val motion = CardSwipeMotion(current)
        motion.begin(CardSwipePath(CardSwipeTarget.Category(7), Offset(1f, 0f)), null, 500f)
        motion.previewNextCard(next)
        motion.dragBy(Offset(100f, 100f))
        motion.dragBy(Offset(-150f, -300f))
        motion.dragBy(Offset(20f, 400f))
        assertOffset(Offset.Zero, motion.movingOffset)
        motion.dragBy(Offset(40f, 0f))
        assertOffset(Offset(10f, 0f), motion.movingOffset)
    }

    @Test
    fun drawAfterManualMarkingRevealsTheStationaryNextCardUnderTheOutgoingCard() {
        listOf(
            CardSwipePath(CardSwipeTarget.Category(7), Offset(0.6f, 0.8f)),
            CardSwipePath(CardSwipeTarget.Random, Offset(-1f, 0f)),
        ).forEach { path ->
            val motion = CardSwipeMotion(current)
            val frames = mutableListOf<Pair<Long, Float>>()
            runBlocking(TestFrameClock {
                assertEquals(next, motion.stationaryCard)
                frames.add(motion.movingCard.instanceId to path.project(motion.movingOffset))
            }) {
                motion.showCard(next, path, 500f)
            }

            assertTrue(frames.any { (id, distance) -> id == 2L && distance > 0f })
            assertTrue(frames.all { (id, distance) -> id == 2L && distance >= 0f })
            assertEquals(3L, motion.movingCard.instanceId)
            assertOffset(Offset.Zero, motion.movingOffset)
            assertTrue(motion.idle)
        }
    }

    @Test
    fun updatingMarksOnTheSameCardDoesNotStartATransition() = runBlocking(TestFrameClock()) {
        val motion = CardSwipeMotion(current)
        motion.showCard(current, previousPath, 500f)
        assertTrue(motion.idle)
        assertFalse(motion.begin(previousPath, null, 500f))
    }

    @Test
    fun committedForwardSwipeRevealsThePreparedCardWithoutAnIncomingAnimation() = runBlocking(TestFrameClock()) {
        val motion = CardSwipeMotion(current)
        val path = CardSwipePath(CardSwipeTarget.Category(7), Offset(0.6f, 0.8f))
        motion.begin(path, previous, 500f)
        motion.previewNextCard(next)
        motion.dragBy(Offset(60f, 80f))
        assertEquals(next, motion.stationaryCard)
        assertEquals(current, motion.movingCard)
        assertOffset(Offset(60f, 80f), motion.movingOffset)
        motion.commit {}
        assertEquals(CardSwipePhase.AwaitingCard, motion.phase)
        assertEquals(next, motion.stationaryCard)
        assertOffset(Offset(300f, 400f), motion.movingOffset)
        var additionalFrames = 0
        kotlinx.coroutines.withContext(TestFrameClock { additionalFrames++ }) {
            motion.showCard(next, previousPath, 900f)
        }
        assertEquals(0, additionalFrames)
        assertEquals(next, motion.movingCard)
        assertNull(motion.stationaryCard)
        assertOffset(Offset.Zero, motion.movingOffset)
        assertTrue(motion.idle)
    }

    @Test
    fun slowPreviewKeepsTheOldCardInPlaceUntilThereIsACardToReveal() {
        val motion = CardSwipeMotion(current)
        motion.begin(CardSwipePath(CardSwipeTarget.Random, Offset(-1f, 0f)), null, 500f)
        motion.dragBy(Offset(-100f, 0f))
        assertOffset(Offset.Zero, motion.movingOffset)
        motion.previewNextCard(next)
        assertEquals(next, motion.stationaryCard)
        assertOffset(Offset(-100f, 0f), motion.movingOffset)
    }

    @Test
    fun cancellingAForwardSwipeDiscardsItsPreviewAndIgnoresLateResults() = runBlocking(TestFrameClock()) {
        val motion = CardSwipeMotion(current)
        motion.begin(CardSwipePath(CardSwipeTarget.Random, Offset(-1f, 0f)), null, 500f)
        motion.previewNextCard(next)
        motion.dragBy(Offset(-100f, 0f))
        motion.cancel()
        motion.previewNextCard(next)
        assertEquals(current, motion.movingCard)
        assertNull(motion.stationaryCard)
        assertOffset(Offset.Zero, motion.movingOffset)
        assertTrue(motion.idle)
    }

    private fun assertOffset(expected: Offset, actual: Offset) {
        assertEquals(expected.x, actual.x, 0.001f)
        assertEquals(expected.y, actual.y, 0.001f)
    }
}

private class TestFrameClock(private val beforeFrame: () -> Unit = {}) : MonotonicFrameClock {
    private var timeNanos = 0L

    override suspend fun <R> withFrameNanos(onFrame: (Long) -> R): R {
        beforeFrame()
        timeNanos += 16_666_667L
        return onFrame(timeNanos)
    }
}
