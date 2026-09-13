package de.kaserik.impulse.frontend.game

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.max

@Immutable
internal data class CardSwipePath(
    val target: CardSwipeTarget,
    val direction: Offset,
) {
    fun project(drag: Offset): Float = drag.x * direction.x + drag.y * direction.y

    fun offscreenDistance(cardBounds: Rect, screenBounds: Rect): Float =
        max(
            distanceOutside(cardBounds, screenBounds, direction),
            distanceOutside(cardBounds, screenBounds, -direction),
        ) + 1f
}

internal fun cardSwipePathForTarget(
    target: CardSwipeTarget,
    regions: Collection<SwipeRegion>,
    screenCenter: Offset,
): CardSwipePath {
    val origin = regions.firstOrNull { it.target == target }?.boundsInRoot?.center
    val delta = origin?.let { screenCenter - it }.let {
        if (target == CardSwipeTarget.Previous) {
            // History cards always enter from below and to the right, including horizontal gestures.
            it?.takeIf { offset -> offset.x < 0f && offset.y < 0f } ?: Offset(-1f, -1f)
        } else {
            it
        }
    }
    val direction = if (delta != null && delta.hasFiniteCoordinates() && delta.getDistance() > 0f) {
        delta / delta.getDistance()
    } else {
        Offset(cardSwipeDirectionForTarget(target).sign, 0f)
    }
    return CardSwipePath(target, direction)
}

internal fun resolveCardSwipePath(
    startPositionInRoot: Offset,
    drag: Offset,
    screenCenter: Offset,
    regions: Collection<SwipeRegion>,
    previousEnabled: Boolean,
): CardSwipePath? {
    if (!startPositionInRoot.hasFiniteCoordinates() || !drag.hasFiniteCoordinates() || drag.x == 0f) return null
    val direction = if (drag.x < 0f) CardSwipeDirection.Left else CardSwipeDirection.Right
    val horizontalTarget = resolveCardSwipeTarget(startPositionInRoot, direction, regions, previousEnabled)
    val paths = regions.asSequence()
        .filter { it.target != CardSwipeTarget.Previous || previousEnabled }
        .filter { cardSwipeDirectionForTarget(it.target) == direction }
        .map { cardSwipePathForTarget(it.target, regions, screenCenter) }
        .toMutableList()
    if (horizontalTarget != null) {
        // Prefer the central horizontal track when both tracks have the same direction.
        paths.add(0, CardSwipePath(horizontalTarget, Offset(direction.sign, 0f)))
    }
    val selected = paths.maxByOrNull { it.project(drag) }?.takeIf { it.project(drag) > 0f } ?: return null
    return if (selected.target == CardSwipeTarget.Previous) {
        cardSwipePathForTarget(CardSwipeTarget.Previous, regions, screenCenter)
    } else {
        selected
    }
}

private fun distanceOutside(card: Rect, screen: Rect, direction: Offset): Float {
    val horizontal = when {
        direction.x > 0f -> (screen.right - card.left) / direction.x
        direction.x < 0f -> (screen.left - card.right) / direction.x
        else -> Float.POSITIVE_INFINITY
    }
    val vertical = when {
        direction.y > 0f -> (screen.bottom - card.top) / direction.y
        direction.y < 0f -> (screen.top - card.bottom) / direction.y
        else -> Float.POSITIVE_INFINITY
    }
    return minOf(horizontal, vertical).coerceAtLeast(0f)
}

private fun Offset.hasFiniteCoordinates(): Boolean = x.isFinite() && y.isFinite()
