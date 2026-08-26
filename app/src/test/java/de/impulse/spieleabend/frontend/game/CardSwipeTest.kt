package de.impulse.spieleabend.frontend.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardSwipeTest {
    private val randomRegion =
        SwipeRegion(
            target = CardSwipeTarget.Random,
            boundsInRoot = Rect(left = 0f, top = 100f, right = 80f, bottom = 220f),
        )
    private val previousRegion =
        SwipeRegion(
            target = CardSwipeTarget.Previous,
            boundsInRoot = Rect(left = 0f, top = 400f, right = 110f, bottom = 520f),
        )
    private val categoryRegion =
        SwipeRegion(
            target = CardSwipeTarget.Category(id = 7),
            boundsInRoot = Rect(left = 320f, top = 400f, right = 420f, bottom = 520f),
        )
    private val regions = listOf(randomRegion, previousRegion, categoryRegion)

    @Test
    fun rightSwipeAtRandomHeightSelectsRandom() {
        assertEquals(
            CardSwipeTarget.Random,
            resolveCardSwipeTarget(
                startPositionInRoot = Offset(x = 180f, y = 160f),
                direction = CardSwipeDirection.Right,
                regions = regions,
                previousEnabled = true,
            ),
        )
    }

    @Test
    fun leftSwipeAtCategoryHeightSelectsCategory() {
        assertEquals(
            CardSwipeTarget.Category(id = 7),
            resolveCardSwipeTarget(
                startPositionInRoot = Offset(x = 360f, y = 460f),
                direction = CardSwipeDirection.Left,
                regions = regions,
                previousEnabled = true,
            ),
        )
    }

    @Test
    fun leftSwipeAtPreviousHeightSelectsPrevious() {
        assertEquals(
            CardSwipeTarget.Previous,
            resolveCardSwipeTarget(
                startPositionInRoot = Offset(x = 60f, y = 460f),
                direction = CardSwipeDirection.Left,
                regions = regions,
                previousEnabled = true,
            ),
        )
    }

    @Test
    fun overlappingPreviousAndCategoryUseHorizontalStartPosition() {
        val leftStart = Offset(x = 100f, y = 460f)
        val rightStart = Offset(x = 330f, y = 460f)

        assertEquals(
            CardSwipeTarget.Previous,
            resolveCardSwipeTarget(leftStart, CardSwipeDirection.Left, regions, previousEnabled = true),
        )
        assertEquals(
            CardSwipeTarget.Category(id = 7),
            resolveCardSwipeTarget(rightStart, CardSwipeDirection.Left, regions, previousEnabled = true),
        )
    }

    @Test
    fun disabledPreviousIsNeverSelected() {
        assertNull(
            resolveCardSwipeTarget(
                startPositionInRoot = Offset(x = 60f, y = 460f),
                direction = CardSwipeDirection.Left,
                regions = listOf(previousRegion),
                previousEnabled = false,
            ),
        )
    }

    @Test
    fun startOutsideEveryVerticalRegionSelectsNothing() {
        assertNull(
            resolveCardSwipeTarget(
                startPositionInRoot = Offset(x = 200f, y = 300f),
                direction = CardSwipeDirection.Left,
                regions = regions,
                previousEnabled = true,
            ),
        )
    }

    @Test
    fun distanceOrVelocityCanCommitSwipe() {
        assertTrue(
            isCardSwipeCommitThresholdReached(
                dragOffsetX = 100f,
                velocityX = 0f,
                cardWidthPx = 400f,
                minimumDistancePx = 72f,
                distanceFraction = 0.25f,
                velocityThresholdPxPerSecond = 900f,
            ),
        )
        assertTrue(
            isCardSwipeCommitThresholdReached(
                dragOffsetX = 20f,
                velocityX = 1_000f,
                cardWidthPx = 400f,
                minimumDistancePx = 72f,
                distanceFraction = 0.25f,
                velocityThresholdPxPerSecond = 900f,
            ),
        )
        assertFalse(
            isCardSwipeCommitThresholdReached(
                dragOffsetX = 99f,
                velocityX = 899f,
                cardWidthPx = 400f,
                minimumDistancePx = 72f,
                distanceFraction = 0.25f,
                velocityThresholdPxPerSecond = 900f,
            ),
        )
    }
}
