package de.kaserik.impulse.frontend.game

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme

internal fun landscapeDrawingTarget(
    previousTarget: FunFactsLandscapeTarget?,
    rotationEnabled: Boolean,
    isLandscape: Boolean,
    hasName: Boolean,
): FunFactsLandscapeTarget? = when {
    !rotationEnabled || !isLandscape -> null
    previousTarget != null -> previousTarget
    hasName -> FunFactsLandscapeTarget.Answer
    else -> FunFactsLandscapeTarget.Name
}

@Composable
internal fun rememberFunFactsLandscapeTarget(
    rotationEnabled: Boolean,
    hasName: Boolean,
): FunFactsLandscapeTarget? {
    val activity = LocalActivity.current
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var previousTarget by rememberSaveable { mutableStateOf<FunFactsLandscapeTarget?>(null) }
    val target = landscapeDrawingTarget(previousTarget, rotationEnabled, isLandscape, hasName)
    SideEffect { previousTarget = target }

    val fullscreen = target != null
    DisposableEffect(activity, fullscreen) {
        val controller = activity?.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView)
        }
        val previousBehavior = controller?.systemBarsBehavior
        if (fullscreen) {
            controller?.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller?.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            if (controller != null && previousBehavior != null) {
                controller.systemBarsBehavior = previousBehavior
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
    return target
}

@Preview(showBackground = true)
@Composable
private fun RememberFunFactsLandscapeTargetPreview() {
    ImpulseTheme {
        val target = rememberFunFactsLandscapeTarget(rotationEnabled = false, hasName = false)
        Text(
            stringResource(
                if (target == FunFactsLandscapeTarget.Answer) R.string.answer_label else R.string.player_name_label,
            ),
        )
    }
}
