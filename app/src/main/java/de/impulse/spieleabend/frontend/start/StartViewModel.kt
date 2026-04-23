package de.impulse.spieleabend.frontend.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.usecase.GetGamesUseCase
import java.util.Locale
import java.util.Locale.ROOT
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StartViewModel @Inject constructor(
    getGames: GetGamesUseCase,
) : ViewModel() {
    private val sprache: Sprache = spracheAusLocale(Locale.getDefault())

    private val _uiState = MutableStateFlow<StartScreenUiState>(StartScreenUiState.Loading)

    val uiState: StateFlow<StartScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = StartScreenUiState.Loaded(games = getGames().toBoardGameShelfItems(sprache))
        }
    }

    private fun spracheAusLocale(locale: Locale): Sprache =
        Sprache.entries.firstOrNull { sprache ->
            sprache.name == locale.language.uppercase(ROOT)
        } ?: Sprache.DE
}
