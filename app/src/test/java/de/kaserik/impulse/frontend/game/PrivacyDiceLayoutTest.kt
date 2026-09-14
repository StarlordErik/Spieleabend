package de.kaserik.impulse.frontend.game

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyDiceLayoutTest {
    @Test
    fun everyPlayerCountKeepsWholeDiceApartAndInsideTheOval() {
        repeat(128) { seed ->
            for (count in 0..PRIVACY_MAX_PLAYERS) {
                assertSafeLayout(privacyDiceLayout(count, Random(seed)), count)
            }
        }
    }

    @Test
    fun sameSeedRestoresThePileAndDifferentSeedsChangeItsPositions() {
        val original = privacyDiceLayout(PRIVACY_MAX_PLAYERS, Random(42))
        assertEquals(original, privacyDiceLayout(PRIVACY_MAX_PLAYERS, Random(42)))
        assertNotEquals(
            original.map { it.center },
            privacyDiceLayout(PRIVACY_MAX_PLAYERS, Random(43)).map { it.center },
        )
    }

    @Test
    fun diceHaveDifferentRotationsAndDoNotFormRowsOrColumns() {
        val placements = privacyDiceLayout(PRIVACY_MAX_PLAYERS, Random(12))
        assertEquals(PRIVACY_MAX_PLAYERS, placements.map { it.center.x }.distinct().size)
        assertEquals(PRIVACY_MAX_PLAYERS, placements.map { it.center.y }.distinct().size)
        assertEquals(PRIVACY_MAX_PLAYERS, placements.map { it.rotation }.distinct().size)
        assertTrue(placements.all { it.rotation in 0f..<360f })
    }

    @Test(timeout = 5000)
    fun repeatedRandomValuesStillProduceACompleteSafePile() {
        val repeating = object : Random() {
            override fun nextBits(bitCount: Int): Int = 0
        }
        assertSafeLayout(privacyDiceLayout(PRIVACY_MAX_PLAYERS, repeating), PRIVACY_MAX_PLAYERS)
    }

    private fun assertSafeLayout(placements: List<PrivacyDiePlacement>, count: Int) {
        assertEquals(count, placements.size)
        placements.forEachIndexed { index, placement ->
            placements.take(index).forEach { other ->
                val distance = hypot(
                    (placement.center.x - other.center.x).toDouble(),
                    (placement.center.y - other.center.y).toDouble(),
                )
                assertTrue("Dice overlap or lose their gap: $placements", distance >= 2 * PRIVACY_DIE_RADIUS + PRIVACY_DICE_GAP)
            }
            assertInsideOvalWithMargin(placement)
        }
    }

    private fun assertInsideOvalWithMargin(placement: PrivacyDiePlacement) {
        val area = PRIVACY_DICE_LANDING_AREA
        val clearance = PRIVACY_DIE_RADIUS + PRIVACY_DICE_EDGE_GAP
        // Check the expanded footprint against the actual ellipse, independently of the placement polygon.
        repeat(128) { sample ->
            val angle = sample * 2.0 * PI / 128
            val x = (placement.center.x - area.center.x + cos(angle) * clearance) / (area.width / 2.0)
            val y = (placement.center.y - area.center.y + sin(angle) * clearance) / (area.height / 2.0)
            assertTrue("Die exceeds the oval or loses its edge margin: $placement", x * x + y * y <= 1.0)
        }
    }
}
