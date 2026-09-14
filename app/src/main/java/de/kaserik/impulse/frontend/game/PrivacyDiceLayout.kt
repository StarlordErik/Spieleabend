package de.kaserik.impulse.frontend.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

internal data class PrivacyDiePlacement(val center: Offset, val rotation: Float)

internal fun privacyDiceLayout(count: Int, random: Random): List<PrivacyDiePlacement> {
    require(count in 0..PRIVACY_MAX_PLAYERS)
    repeat(PLACEMENT_RESTARTS) {
        val centers = randomPrivacyDiceCenters(count, random)
        if (centers != null) return centers.map { PrivacyDiePlacement(it, random.nextFloat() * 360f) }
    }
    return fallbackPrivacyDiceCenters(count, random).map { PrivacyDiePlacement(it, random.nextFloat() * 360f) }
}

private fun randomPrivacyDiceCenters(count: Int, random: Random): List<Offset>? {
    val centers = mutableListOf<Offset>()
    repeat(count) {
        val center = nextPrivacyDieCenter(centers, random) ?: return null
        centers += center
    }
    return centers
}

private fun nextPrivacyDieCenter(centers: List<Offset>, random: Random): Offset? {
    val clearance = PRIVACY_DIE_RADIUS + PRIVACY_DICE_EDGE_GAP
    val horizontal = PRIVACY_DICE_LANDING_AREA.width / 2f - clearance
    val vertical = PRIVACY_DICE_LANDING_AREA.height / 2f - clearance
    repeat(PLACEMENT_ATTEMPTS) {
        val relative = Offset((random.nextFloat() * 2f - 1f) * horizontal, (random.nextFloat() * 2f - 1f) * vertical)
        val center = PRIVACY_DICE_LANDING_AREA.center + relative
        val inside = LANDING_EDGES.all { edge ->
            relative.x * edge.normal.x + relative.y * edge.normal.y <= edge.limit
        }
        if (inside && centers.none { (it - center).getDistanceSquared() < MINIMUM_DIE_DISTANCE_SQUARED }) return center
    }
    return null
}

private fun fallbackPrivacyDiceCenters(count: Int, random: Random): List<Offset> {
    val horizontal = if (random.nextBoolean()) 1f else -1f
    val vertical = if (random.nextBoolean()) 1f else -1f
    // A loose, irregular packing keeps placement bounded even if random attempts get stuck.
    return FALLBACK_CENTERS.shuffled(random).take(count).map { center ->
        PRIVACY_DICE_LANDING_AREA.center + Offset(
            center.x * horizontal + random.nextFloat() - 0.5f,
            center.y * vertical + random.nextFloat() - 0.5f,
        )
    }
}

private data class LandingEdge(val normal: Offset, val limit: Float)

private fun landingVertex(index: Int): Offset {
    val angle = index * 2f * PI.toFloat() / LANDING_SEGMENTS
    return Offset(
        cos(angle) * PRIVACY_DICE_LANDING_AREA.width / 2f,
        sin(angle) * PRIVACY_DICE_LANDING_AREA.height / 2f,
    )
}

internal const val PRIVACY_DIE_RADIUS = 13.5f
internal const val PRIVACY_DICE_GAP = 3f
internal const val PRIVACY_DICE_EDGE_GAP = 6f
internal val PRIVACY_DICE_LANDING_AREA = Rect(30f, 164f, 298f, 260f)
private const val PLACEMENT_RESTARTS = 16
private const val PLACEMENT_ATTEMPTS = 256
private const val LANDING_SEGMENTS = 64
private const val MINIMUM_DIE_DISTANCE = 2f * PRIVACY_DIE_RADIUS + PRIVACY_DICE_GAP
private const val MINIMUM_DIE_DISTANCE_SQUARED = MINIMUM_DIE_DISTANCE * MINIMUM_DIE_DISTANCE

// The inscribed polygon keeps the entire rotated die, including its margin, inside the oval.
private val LANDING_EDGES = List(LANDING_SEGMENTS) { index ->
    val start = landingVertex(index)
    val direction = landingVertex(index + 1) - start
    val normal = Offset(direction.y, -direction.x) / direction.getDistance()
    LandingEdge(normal, start.x * normal.x + start.y * normal.y - PRIVACY_DIE_RADIUS - PRIVACY_DICE_EDGE_GAP)
}
private val FALLBACK_CENTERS = listOf(
    Offset(-88f, -9f), Offset(-52f, -23f), Offset(-15f, -24f), Offset(25f, -24f), Offset(66f, -20f),
    Offset(91f, 7f), Offset(53f, 19f), Offset(12f, 20f), Offset(-27f, 20f), Offset(-66f, 15f),
)
