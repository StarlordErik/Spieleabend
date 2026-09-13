package de.kaserik.impulse.frontend.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardSwipePathTest {
    private val center = Offset(200f, 400f)
    private val category = SwipeRegion(CardSwipeTarget.Category(7), Rect(0f, 100f, 40f, 220f))
    private val random = SwipeRegion(CardSwipeTarget.Random, Rect(360f, 300f, 400f, 500f))
    private val previous = SwipeRegion(CardSwipeTarget.Previous, Rect(360f, 580f, 400f, 700f))
    private val regions = listOf(category, random, previous)

    @Test
    fun horizontalGestureKeepsTheCentralHorizontalTrack() {
        val path = requireNotNull(resolveCardSwipePath(Offset(150f, 160f), Offset(40f, 1f), center, regions, true))

        assertEquals(category.target, path.target)
        assertEquals(Offset(1f, 0f), path.direction)
    }

    @Test
    fun diagonalGestureSelectsTheLineFromCategoryThroughTheScreenCenter() {
        val path = requireNotNull(resolveCardSwipePath(center, Offset(18f, 24f), center, regions, true))

        assertEquals(category.target, path.target)
        assertEquals(0.6f, path.direction.x, 0.001f)
        assertEquals(0.8f, path.direction.y, 0.001f)
        assertEquals(0f, path.project(Offset(-80f, 60f)), 0.001f)
    }

    @Test
    fun previousMovesFromTheLowerRightThroughTheCenter() {
        val path = requireNotNull(resolveCardSwipePath(center, Offset(-18f, -24f), center, regions, true))

        assertEquals(CardSwipeTarget.Previous, path.target)
        assertEquals(-0.6f, path.direction.x, 0.001f)
        assertEquals(-0.8f, path.direction.y, 0.001f)
        assertEquals(Offset(-1f, 0f), cardSwipePathForTarget(random.target, regions, center).direction)
    }

    @Test
    fun unavailablePreviousAndInvalidGesturesCannotStartASwipe() {
        assertNull(resolveCardSwipePath(center, Offset(-18f, -24f), center, listOf(previous), false))
        assertNull(resolveCardSwipePath(center, Offset(0f, 40f), center, regions, true))
        assertNull(resolveCardSwipePath(Offset.Unspecified, Offset(40f, 0f), center, regions, true))
    }

    @Test
    fun incomingAndOutgoingCardsClearTheWholeScreenOnEveryTrack() {
        val screen = Rect(0f, 0f, 400f, 800f)
        val card = Rect(60f, 80f, 340f, 760f)
        regions.forEach { region ->
            val path = cardSwipePathForTarget(region.target, regions, center)
            val offset = path.direction * path.offscreenDistance(card, screen)
            assertFalse(card.translate(offset).overlaps(screen))
            assertFalse(card.translate(-offset).overlaps(screen))
            assertTrue(path.offscreenDistance(card, screen).isFinite())
        }
    }
}
