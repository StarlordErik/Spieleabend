package de.impulse.spieleabend.frontend.start

import androidx.compose.runtime.Immutable

@Immutable
sealed interface StartScreenUiState {
    @Immutable
    data object Loading : StartScreenUiState

    @Immutable
    data class Loaded(
        val games: List<BoardGameShelfItem>,
    ) : StartScreenUiState
}
