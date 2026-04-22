package de.impulse.spieleabend.frontend.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.domain.usecase.GetGameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getGame: GetGameUseCase,
) : ViewModel() {
    private val gameId: String = savedStateHandle[GAME_ID_ARG] ?: DefaultGameId

    private val _uiState = MutableStateFlow(
        getGame(gameId).toGameUiState(sprachCode = Locale.getDefault().toLanguageTag()),
    )

    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private companion object {
        const val DefaultGameId = "placeholder-1"
    }
}
