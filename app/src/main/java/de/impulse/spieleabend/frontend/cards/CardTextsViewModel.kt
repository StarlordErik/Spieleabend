package de.impulse.spieleabend.frontend.cards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.usecase.GetGameUseCase
import de.impulse.spieleabend.frontend.game.GAME_ID_ARG
import java.util.Locale
import java.util.Locale.ROOT
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CardTextsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGame: GetGameUseCase,
) : ViewModel() {
    private val gameId: Int = savedStateHandle.get<String>(GAME_ID_ARG)?.toIntOrNull() ?: 1
    private val language = languageFromLocale(Locale.getDefault())
    private val _uiState = MutableStateFlow<CardTextsUiState>(CardTextsUiState.Loading)
    val uiState: StateFlow<CardTextsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = getGame(gameId).toCardTextsUiState(language)
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

    private fun languageFromLocale(locale: Locale): Sprache =
        Sprache.entries.firstOrNull { language -> language.name == locale.language.uppercase(ROOT) } ?: Sprache.DE
}
