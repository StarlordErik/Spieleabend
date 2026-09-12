package de.kaserik.impulse.frontend.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.repository.AppSettingsRepository
import de.kaserik.impulse.domain.usecase.ResetAllCardsForAllGamesUseCase
import de.kaserik.impulse.domain.usecase.UpdateCardTextSettingsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val resetAllCardsForAllGames: ResetAllCardsForAllGamesUseCase,
    private val updateCardTextSettings: UpdateCardTextSettingsUseCase,
) : ViewModel() {
    private val translationChangeMutex = Mutex()
    val developerMode: StateFlow<Boolean> = appSettingsRepository.developerMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = false,
    )

    val language: StateFlow<Sprache> = appSettingsRepository.language.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = Sprache.DE,
    )

    fun setDeveloperMode(enabled: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.setDeveloperMode(enabled)
        }
    }

    fun setLanguage(language: Sprache) {
        viewModelScope.launch {
            appSettingsRepository.setLanguage(language)
        }
    }

    fun resetAllCards() {
        viewModelScope.launch {
            resetAllCardsForAllGames()
        }
    }

    fun applyErikTranslations(overwriteExisting: Boolean) {
        viewModelScope.launch {
            translationChangeMutex.withLock {
                updateCardTextSettings.applyErikTranslations(gameId = null, overwriteExisting = overwriteExisting)
            }
        }
    }

    fun resetCustomTranslations() {
        viewModelScope.launch {
            translationChangeMutex.withLock {
                updateCardTextSettings.resetCustomTranslations(gameId = null)
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
