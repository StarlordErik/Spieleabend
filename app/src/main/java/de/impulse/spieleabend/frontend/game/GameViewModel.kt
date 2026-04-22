package de.impulse.spieleabend.frontend.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.usecase.GetGameUseCase
import de.impulse.spieleabend.domain.usecase.GetNextCardFromCategoryUseCase
import de.impulse.spieleabend.domain.usecase.GetNextRandomCardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getGame: GetGameUseCase,
    private val getNextCardFromCategory: GetNextCardFromCategoryUseCase,
    private val getNextRandomCard: GetNextRandomCardUseCase,
) : ViewModel() {
    private val gameId: String = savedStateHandle[GAME_ID_ARG] ?: DefaultGameId
    private val spiel: Spiel = getGame(gameId)
    private val sprachCode: String = Locale.getDefault().toLanguageTag()

    private val _uiState = MutableStateFlow(
        spiel.toUiState(aktuelleKarte = getNextRandomCard(spiel)),
    )

    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun selectKategorie(kategorieId: String) {
        _uiState.value = spiel.toUiState(
            aktuelleKarte = getNextCardFromCategory(
                spiel = spiel,
                kategorieId = kategorieId,
            ),
        )
    }

    fun selectRandom() {
        _uiState.value = spiel.toUiState(
            aktuelleKarte = getNextRandomCard(spiel),
        )
    }

    private fun Spiel.toUiState(aktuelleKarte: GezogeneKarte): GameUiState =
        toGameUiState(
            aktuelleKarte = aktuelleKarte,
            sprachCode = sprachCode,
        )

    private companion object {
        const val DefaultGameId = "placeholder-1"
    }
}
