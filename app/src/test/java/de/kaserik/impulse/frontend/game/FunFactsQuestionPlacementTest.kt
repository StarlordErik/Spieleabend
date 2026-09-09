package de.kaserik.impulse.frontend.game

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class FunFactsQuestionPlacementTest {
    @Test
    fun compactQuestionInterpolatesFromOriginalPanelToTopOfPlayArea() {
        val placements = listOf(0f, 0.5f, 1f).map { progress ->
            questionPlacement(
                container = DpSize(400.dp, 800.dp),
                questionOrigin = FunFactsQuestionOrigin(0.1f, 0.5f, 0.8f, 0.25f),
                originBounds = null,
                playAreaBounds = Rect.Zero,
                cardTextCount = 2,
                density = Density(1f),
                progress = progress,
            )
        }

        assertEquals(
            listOf(DpOffset(40.dp, 400.dp), DpOffset(42.dp, 200.dp), DpOffset(44.dp, 0.dp)),
            placements.map { it.offset })
        assertEquals(
            listOf(DpSize(320.dp, 200.dp), DpSize(316.dp, 156.dp), DpSize(312.dp, 112.dp)),
            placements.map { it.size })
    }

    @Test
    fun questionFromThreeTextCardKeepsItsOriginalSize() {
        val placement = questionPlacement(
            container = DpSize(400.dp, 800.dp),
            questionOrigin = FunFactsQuestionOrigin(0.1f, 0.5f, 0.8f, 0.25f),
            originBounds = null,
            playAreaBounds = Rect.Zero,
            cardTextCount = 3,
            density = Density(1f),
            progress = 1f,
        )

        assertEquals(DpSize(320.dp, 200.dp), placement.size)
        assertEquals(DpOffset(40.dp, 0.dp), placement.offset)
        assertEquals(200.dp, placement.targetHeight)
    }

    @Test
    fun landscapeQuestionStaysCenteredAndWithinMaximumWidth() {
        val placement = questionPlacement(
            container = DpSize(800.dp, 400.dp),
            questionOrigin = FunFactsQuestionOrigin(0.1f, 0.5f, 0.8f, 0.25f),
            originBounds = null,
            playAreaBounds = Rect.Zero,
            cardTextCount = 1,
            density = Density(1f),
            progress = 1f,
        )

        assertEquals(DpSize(560.dp, 112.dp), placement.size)
        assertEquals(DpOffset(120.dp, 0.dp), placement.offset)
    }

    @Test
    fun measuredBoundsUseDensityAndPlayAreaOffsetUntilOriginIsSaved() {
        val placement = questionPlacement(
            container = DpSize(400.dp, 800.dp),
            questionOrigin = null,
            originBounds = Rect(180f, 900f, 820f, 1300f),
            playAreaBounds = Rect(100f, 100f, 900f, 1700f),
            cardTextCount = 2,
            density = Density(2f),
            progress = 0f,
        )

        assertEquals(DpSize(320.dp, 200.dp), placement.size)
        assertEquals(DpOffset(40.dp, 400.dp), placement.offset)
    }

    @Test
    fun missingBoundsUseCompactFallbackWithPositiveDimensions() {
        val placement = questionPlacement(
            container = DpSize(400.dp, 800.dp),
            questionOrigin = null,
            originBounds = null,
            playAreaBounds = Rect.Zero,
            cardTextCount = 3,
            density = Density(1f),
            progress = 0f,
        )

        assertEquals(DpSize(312.dp, 112.dp), placement.size)
        assertEquals(DpOffset(44.dp, 688.dp), placement.offset)
    }
}
