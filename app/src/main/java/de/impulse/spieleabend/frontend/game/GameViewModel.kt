package de.impulse.spieleabend.frontend.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.usecase.GetGameUseCase
import de.impulse.spieleabend.domain.usecase.GetNextCardFromCategoryUseCase
import de.impulse.spieleabend.domain.usecase.GetNextRandomCardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getGame: GetGameUseCase,
    private val getNextCardFromCategory: GetNextCardFromCategoryUseCase,
    private val getNextRandomCard: GetNextRandomCardUseCase,
) : ViewModel() {
    private val gameIdArg: String? = savedStateHandle[GAME_ID_ARG]
    private val gameId: Int = gameIdArg?.toIntOrNull() ?: DefaultGameId
    private val sprache: Sprache = Sprache.fromLocale(Locale.getDefault()) ?: Sprache.DE
    private var spiel: Spiel? = null

    private val _uiState = MutableStateFlow<GameScreenUiState>(GameScreenUiState.Loading)

    val uiState: StateFlow<GameScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val loadedSpiel = getGame(gameId)
            spiel = loadedSpiel
            showCard(getNextRandomCard(loadedSpiel))
        }
    }

    fun selectKategorie(kategorieId: Int) {
        val loadedSpiel = spiel ?: return

        showCard(
            aktuelleKarte = getNextCardFromCategory(
                spiel = loadedSpiel,
                kategorieId = kategorieId,
            ),
        )
    }

    fun selectRandom() {
        val loadedSpiel = spiel ?: return

        showCard(aktuelleKarte = getNextRandomCard(loadedSpiel))
    }

    private fun showCard(aktuelleKarte: GezogeneKarte) {
        val loadedSpiel = spiel ?: return

        _uiState.value = GameScreenUiState.Loaded(
            game = loadedSpiel.toUiState(aktuelleKarte = aktuelleKarte),
        )
    }

    private fun Spiel.toUiState(aktuelleKarte: GezogeneKarte): GameUiState =
        toGameUiState(
            aktuelleKarte = aktuelleKarte,
            sprache = sprache,
        )

    private companion object {
        const val DefaultGameId = 1
    }
}
