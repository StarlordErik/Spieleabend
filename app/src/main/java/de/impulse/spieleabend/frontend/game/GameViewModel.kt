package de.impulse.spieleabend.frontend.game

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.domain.usecase.GetGreetingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    getGreeting: GetGreetingUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        GameUiState(message = getGreeting().text),
    )

    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
}
