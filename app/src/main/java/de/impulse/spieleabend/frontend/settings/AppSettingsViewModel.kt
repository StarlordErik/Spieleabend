package de.impulse.spieleabend.frontend.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.domain.repository.AppSettingsRepository
import de.impulse.spieleabend.domain.usecase.ResetAllCardsForAllGamesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val resetAllCardsForAllGames: ResetAllCardsForAllGamesUseCase,
) : ViewModel() {
    val developerMode: StateFlow<Boolean> = appSettingsRepository.developerMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = false,
    )

    fun setDeveloperMode(enabled: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.setDeveloperMode(enabled)
        }
    }

    fun resetAllCards() {
        viewModelScope.launch {
            resetAllCardsForAllGames()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
