package de.impulse.spieleabend.frontend.cards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.repository.AppSettingsRepository
import de.impulse.spieleabend.domain.usecase.GetGameUseCase
import de.impulse.spieleabend.frontend.game.GAME_ID_ARG
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CardTextsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGame: GetGameUseCase,
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel() {
    private val gameId: Int = savedStateHandle.get<String>(GAME_ID_ARG)?.toIntOrNull() ?: 1
    private val _uiState = MutableStateFlow<CardTextsUiState>(CardTextsUiState.Loading)
    val uiState: StateFlow<CardTextsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appSettingsRepository.language.collect { language ->
                _uiState.value = getGame(gameId).toCardTextsUiState(language)
            }
        }
    }

    fun sort(categoryId: Int, column: CardTableSortColumn) {
        val state = _uiState.value as? CardTextsUiState.Loaded ?: return
        _uiState.value = state.copy(
            categories = state.categories.map { category ->
                if (category.id == categoryId) category.toggleSort(column) else category
            },
        )
    }
}
