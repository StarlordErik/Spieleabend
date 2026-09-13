package de.kaserik.impulse.frontend.game

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme

internal val LocalCardTimerPaused = compositionLocalOf { false }

@Composable
internal fun CardIdlePlayedEffect(
    cardInstanceId: Long,
    kartentext: GameKartentextUiModel?,
    enabled: Boolean,
    onKartentextPlayed: (Int) -> Unit,
) {
    val timer = remember(cardInstanceId, kartentext?.id, kartentext?.gespielt) {
        CardIdleTimer()
    }
    val onPlayed by rememberUpdatedState(onKartentextPlayed)
    val lifecycleOwner = LocalLifecycleOwner.current
    val windowFocused = LocalWindowInfo.current.isWindowFocused
    val paused = LocalCardTimerPaused.current
    val cardIsUnplayed = kartentext != null && !kartentext.gespielt
    val canRun = enabled && windowFocused && !paused

    LaunchedEffect(timer, canRun, cardIsUnplayed, lifecycleOwner) {
        if (!canRun || !cardIsUnplayed) {
            return@LaunchedEffect
        }
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (timer.awaitTimeout()) onPlayed(requireNotNull(kartentext).id)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardIdlePlayedEffectPreview() {
    ImpulseTheme {
        CardIdlePlayedEffect(cardInstanceId = 1, kartentext = null, enabled = false, onKartentextPlayed = {})
        Text(stringResource(R.string.card_text_played))
    }
}
