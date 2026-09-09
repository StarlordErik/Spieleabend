package de.kaserik.impulse.frontend.game

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

internal enum class FunFactsLandscapeTarget {
    Name,
    Answer,
}

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

    DisposableEffect(activity, rotationEnabled) {
        activity?.requestedOrientation = if (rotationEnabled) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        onDispose {
            // A rotation recreates the activity; keep its orientation request during that handoff.
            if (activity != null && !activity.isChangingConfigurations) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

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
