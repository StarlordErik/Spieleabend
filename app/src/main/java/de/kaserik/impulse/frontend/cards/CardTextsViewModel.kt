package de.kaserik.impulse.frontend.cards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kaserik.impulse.common.GAME_ID_ARG
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.repository.AppSettingsRepository
import de.kaserik.impulse.domain.usecase.GetGameUseCase
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
