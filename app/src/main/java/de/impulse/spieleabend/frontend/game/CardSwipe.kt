package de.impulse.spieleabend.frontend.game

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.abs
import kotlin.math.max

@Immutable
internal sealed interface CardSwipeTarget {
    @Immutable
    data object Random : CardSwipeTarget

    @Immutable
    data object Previous : CardSwipeTarget

    @Immutable
    data class Category(
        val id: Int,
    ) : CardSwipeTarget
}

@Immutable
internal data class CardSwipeRequest(
    val id: Long,
    val target: CardSwipeTarget,
)

@Immutable
internal data class SwipeRegion(
    val target: CardSwipeTarget,
    val boundsInRoot: Rect,
)

internal enum class CardSwipeDirection(
    val sign: Float,
) {
    Left(sign = -1f),
    Right(sign = 1f),
}

internal fun cardSwipeDirectionForTarget(target: CardSwipeTarget): CardSwipeDirection =
    when (target) {
        CardSwipeTarget.Random,
        CardSwipeTarget.Previous,
        -> CardSwipeDirection.Left

        is CardSwipeTarget.Category -> CardSwipeDirection.Right
    }

internal fun resolveCardSwipeTarget(
    startPositionInRoot: Offset,
    direction: CardSwipeDirection,
    regions: Collection<SwipeRegion>,
    previousEnabled: Boolean,
): CardSwipeTarget? =
    regions
        .asSequence()
        .filter { region -> region.isEligible(direction, previousEnabled) }
        .filter { region -> region.containsVertically(startPositionInRoot.y) }
        .minWithOrNull(
            compareBy<SwipeRegion> { region ->
                region.squaredDistanceToCenter(startPositionInRoot)
            }.thenBy { region -> region.target.selectionPriority() },
        )
        ?.target

internal fun cardSwipeDirection(
    dragOffsetX: Float,
    velocityX: Float,
    velocityThresholdPxPerSecond: Float,
): CardSwipeDirection? {
    val threshold = velocityThresholdPxPerSecond.coerceAtLeast(0f)
    val directionalValue =
        if (velocityX.isFinite() && abs(velocityX) >= threshold && velocityX != 0f) {
            velocityX
        } else {
            dragOffsetX
        }

    return when {
        !directionalValue.isFinite() || directionalValue == 0f -> null
        directionalValue < 0f -> CardSwipeDirection.Left
        else -> CardSwipeDirection.Right
    }
}

internal fun isCardSwipeCommitThresholdReached(
    dragOffsetX: Float,
    velocityX: Float,
    cardWidthPx: Float,
    minimumDistancePx: Float,
    distanceFraction: Float,
    velocityThresholdPxPerSecond: Float,
): Boolean {
    if (!dragOffsetX.isFinite() || !velocityX.isFinite() || !cardWidthPx.isFinite()) {
        return false
    }

    val requiredDistance = max(
        minimumDistancePx.coerceAtLeast(0f),
        cardWidthPx.coerceAtLeast(0f) * distanceFraction.coerceAtLeast(0f),
    )
    val reachedDistance = dragOffsetX != 0f && abs(dragOffsetX) >= requiredDistance
    val reachedVelocity =
        velocityX != 0f &&
            abs(velocityX) >= velocityThresholdPxPerSecond.coerceAtLeast(0f)

    return reachedDistance || reachedVelocity
}

private fun SwipeRegion.isEligible(
    direction: CardSwipeDirection,
    previousEnabled: Boolean,
): Boolean =
    when (direction) {
        CardSwipeDirection.Right -> target is CardSwipeTarget.Category
        CardSwipeDirection.Left ->
            when (target) {
                CardSwipeTarget.Random -> true
                CardSwipeTarget.Previous -> previousEnabled
                is CardSwipeTarget.Category -> false
            }
    }

private fun SwipeRegion.containsVertically(y: Float): Boolean =
    y.isFinite() &&
        boundsInRoot.height > 0f &&
        y >= boundsInRoot.top &&
        y <= boundsInRoot.bottom

private fun SwipeRegion.squaredDistanceToCenter(position: Offset): Float {
    val deltaX = position.x - boundsInRoot.center.x
    val deltaY = position.y - boundsInRoot.center.y
    return deltaX * deltaX + deltaY * deltaY
}

private fun CardSwipeTarget.selectionPriority(): Int =
    when (this) {
        CardSwipeTarget.Previous -> 0
        CardSwipeTarget.Random -> 1
        is CardSwipeTarget.Category -> id + 2
    }
