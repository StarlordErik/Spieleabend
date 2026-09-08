package de.kaserik.impulse.frontend.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.repository.AppSettingsRepository
import de.kaserik.impulse.domain.usecase.GetGamesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StartViewModel @Inject constructor(
    private val getGames: GetGamesUseCase,
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<StartScreenUiState>(StartScreenUiState.Loading)

    val uiState: StateFlow<StartScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val games = getGames()
            appSettingsRepository.language.collect { language ->
                _uiState.value = StartScreenUiState.Loaded(games = games.toBoardGameShelfItems(language))
            }
        }
    }
}
