package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.awaitCancellation

@Composable
internal fun rememberCardPrefetchModifier(
    onActiveChanged: (Boolean) -> Unit,
    onPointerInput: (Boolean) -> Unit,
): Modifier {
    val activeChanged by rememberUpdatedState(onActiveChanged)
    val pointerInput by rememberUpdatedState(onPointerInput)
    val lifecycleOwner = LocalLifecycleOwner.current
    val windowFocused = LocalWindowInfo.current.isWindowFocused
    LaunchedEffect(lifecycleOwner, windowFocused) {
        if (!windowFocused) return@LaunchedEffect
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            activeChanged(true)
            try {
                awaitCancellation()
            } finally {
                activeChanged(false)
            }
        }
    }
    return Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                pointerInput(event.changes.any { it.pressed })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RememberCardPrefetchModifierPreview() {
    Box(modifier = rememberCardPrefetchModifier(onActiveChanged = {}, onPointerInput = {}))
}
