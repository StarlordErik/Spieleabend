package de.kaserik.impulse.frontend.game

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyPredictionDialTest {
    @Test
    fun gesturesMeasureBothDirectionsAndCrossTheAngleBoundary() {
        assertEquals(90f, privacyDialDelta(Offset(0f, -1f), Offset(1f, 0f)), 0.001f)
        assertEquals(-90f, privacyDialDelta(Offset(1f, 0f), Offset(0f, -1f)), 0.001f)
        assertTrue(privacyDialDelta(Offset(-1f, 0.01f), Offset(-1f, -0.01f)) in 0f..2f)
    }

    @Test
    fun numbersWrapInBothDirectionsLikeACircularDisc() {
        for (maximum in 1 until MAX_GAME_PLAYERS) {
            val step = 360f / maximum
            for (turn in -2..2) {
                for (value in 1..maximum) {
                    val rotation = turn * 360f - (value - 1) * step
                    assertEquals(value, privacyDialValue(rotation, maximum))
                }
            }
            assertEquals(maximum, privacyDialValue(step, maximum))
            assertEquals(1, privacyDialValue(-360f, maximum))
        }
    }

    @Test
    fun snappingUsesTheNearestNumberWithoutAnExtraFullTurn() {
        for (maximum in 1 until MAX_GAME_PLAYERS) {
            for (value in 1..maximum) {
                val rotation = 725f
                val target = privacyDialTargetRotation(rotation, value, maximum)
                assertTrue(kotlin.math.abs(target - rotation) <= 180f)
                assertEquals(value, privacyDialValue(target, maximum))
            }
        }
    }

    @Test
    fun releasingHalfwayBetweenNumbersKeepsTheSubmittedPrediction() {
        for (maximum in 1 until MAX_GAME_PLAYERS) {
            for (direction in listOf(-1, 1)) {
                val rotation = direction * 180f / maximum
                val prediction = privacyDialValue(rotation, maximum)
                val snapped = privacyDialTargetRotation(rotation, prediction, maximum)
                assertEquals(prediction, privacyDialValue(snapped, maximum))
            }
        }
    }
}
