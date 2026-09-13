package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

@Stable
internal class CardSwipeGestureInput {
    var handlers: CardSwipeGestureHandlers? = null
}

internal data class CardSwipeGestureHandlers(
    val onStart: (Offset) -> Unit,
    val onDrag: (Offset) -> Unit,
    val onEnd: () -> Unit,
    val onCancel: () -> Unit,
)

internal fun Modifier.cardSwipeGestures(
    input: CardSwipeGestureInput,
    enabled: Boolean,
    positionInRoot: () -> Offset,
): Modifier = pointerInput(input, enabled) {
    if (!enabled) return@pointerInput
    var activeHandlers: CardSwipeGestureHandlers? = null
    detectDragGestures(
        onDragStart = { position ->
            activeHandlers = input.handlers
            activeHandlers?.onStart?.invoke(positionInRoot() + position)
        },
        onDrag = { change, drag ->
            if (activeHandlers != null) {
                change.consume()
                activeHandlers?.onDrag?.invoke(drag)
            }
        },
        onDragEnd = {
            activeHandlers?.onEnd?.invoke()
            activeHandlers = null
        },
        onDragCancel = {
            activeHandlers?.onCancel?.invoke()
            activeHandlers = null
        },
    )
}
