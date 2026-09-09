package de.kaserik.impulse.frontend.game

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FunFactsLandscapeLayoutTest {
    @Test
    fun signKeepsItsAspectRatioAndUsesAllAvailableWidthOrHeight() {
        val sizes = listOf(
            DpSize(712.dp, 312.dp),
            DpSize(500.dp, 272.dp),
            DpSize(360.dp, 600.dp),
            DpSize(1_000.dp, 520.dp),
        )
        sizes.forEach { available ->
            val layout = landscapeDrawingPadLayout(available, 76.dp, 2.5.dp)
            val sign = layout.signSize
            val totalWidth = sign.width + 8.dp + 30.dp
            val totalHeight = sign.height + layout.controlsTopAllowance

            assertEquals(1.5555556f, sign.width / sign.height, 0.00001f)
            assertTrue(totalWidth <= available.width + 0.01.dp)
            assertTrue(totalHeight <= available.height + 0.01.dp)
            assertTrue(
                totalWidth >= available.width - 0.01.dp ||
                        totalHeight >= available.height - 0.01.dp,
            )
        }
        val fullscreenLayout = landscapeDrawingPadLayout(DpSize(712.dp, 312.dp), 76.dp, 2.5.dp)
        assertEquals(312.dp, fullscreenLayout.signSize.height)
        assertEquals(0.dp, fullscreenLayout.controlsTopAllowance)
        assertEquals(0.dp, fullscreenLayout.drawingPadOffset.y)
    }

    @Test
    fun strokeButtonsFitToTheRightAndAreCenteredOnTheSideEdge() {
        listOf(180.dp, 224.dp, 272.dp, 312.dp).forEach { height ->
            listOf(250.dp, 500.dp, 712.dp).forEach { width ->
                val layout = landscapeDrawingPadLayout(DpSize(width, height), 76.dp, 2.5.dp)
                val sign = layout.signSize
                val pad = layout.drawingPadOffset
                val buttons = layout.strokeWidthSelectorOffset
                val columnHeight = 30.dp * 3 + 8.dp * 2
                val sideTop = pad.y + layout.controlsTopAllowance + sign.height * 0.28f
                val sideBottom = pad.y + layout.controlsTopAllowance + sign.height

                assertEquals((pad.x + sign.width + 8.dp).value, buttons.x.value, 0.01f)
                assertTrue(buttons.x + 30.dp <= width + 0.01.dp)
                assertTrue(buttons.y >= 0.dp)
                assertTrue(buttons.y + columnHeight <= height + 0.01.dp)
                assertEquals(
                    ((sideTop + sideBottom) / 2f).value,
                    (buttons.y + columnHeight / 2f).value,
                    0.01f,
                )
                if ((width - sign.width) / 2f >= 38.dp) {
                    assertEquals((width / 2f).value, (pad.x + sign.width / 2f).value, 0.01f)
                }
            }
        }
    }

    @Test
    fun deleteControlKeepsItsGapWithoutClippingOnShortDisplays() {
        listOf(180.dp, 224.dp, 272.dp, 312.dp).forEach { height ->
            listOf(64.dp, 90.dp, 120.dp).forEach { deleteWidth ->
                val gap = 2.5.dp
                val layout = landscapeDrawingPadLayout(DpSize(500.dp, height), deleteWidth, gap)
                val sign = layout.signSize
                val boundaryAtDeleteStart = sign.height * 0.28f *
                        (1f - deleteWidth / sign.width * 2f).coerceAtLeast(0f)
                val deleteTop = layout.controlsTopAllowance + boundaryAtDeleteStart - gap - 40.dp

                assertTrue(deleteTop >= (-0.01).dp)
                assertTrue(sign.height + layout.controlsTopAllowance <= height + 0.01.dp)
            }
        }
    }
}
