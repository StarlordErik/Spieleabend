package de.impulse.spieleabend.frontend.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.usecase.DrawCardResult
import de.impulse.spieleabend.domain.usecase.DrawNextCardFromCategoryUseCase
import de.impulse.spieleabend.domain.usecase.DrawNextRandomCardUseCase
import de.impulse.spieleabend.domain.usecase.SetCardTextPlayedStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Locale.ROOT
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val drawNextCardFromCategory: DrawNextCardFromCategoryUseCase,
    private val drawNextRandomCard: DrawNextRandomCardUseCase,
    private val setCardTextPlayedState: SetCardTextPlayedStateUseCase,
) : ViewModel() {
    private val gameIdArg: String? = savedStateHandle[GAME_ID_ARG]
    private val gameId: Int = gameIdArg?.toIntOrNull() ?: DefaultGameId
    private val sprache: Sprache = spracheAusLocale(Locale.getDefault())
    private var nextCardInstanceId: Long = 0

    private val _uiState = MutableStateFlow<GameScreenUiState>(GameScreenUiState.Loading)

    val uiState: StateFlow<GameScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            showCard(drawNextRandomCard(gameId))
        }
    }

    fun selectKategorie(kategorieId: Int) {
        viewModelScope.launch {
            showCard(
                drawNextCardFromCategory(
                    gameId = gameId,
                    kategorieId = kategorieId,
                ),
            )
        }
    }

    fun selectRandom() {
        viewModelScope.launch {
            showCard(drawNextRandomCard(gameId))
        }
    }

    fun setKartentextGespielt(
        cardTextId: Int,
        gespielt: Boolean,
    ) {
        val currentState = _uiState.value as? GameScreenUiState.Loaded
        val aktuellerKartentext =
            currentState?.game?.aktuelleKarte?.kartentexte?.firstOrNull { kartentext ->
                kartentext.id == cardTextId
            }

        if (currentState != null && aktuellerKartentext != null && aktuellerKartentext.gespielt != gespielt) {
            _uiState.value =
                GameScreenUiState.Loaded(
                    currentState.game.withCardTextPlayedState(
                        cardTextId = cardTextId,
                        gespielt = gespielt,
                    ),
                )

            viewModelScope.launch {
                setCardTextPlayedState(
                    cardTextId = cardTextId,
                    gespielt = gespielt,
                )
            }
        }
    }

    private fun showCard(drawCardResult: DrawCardResult) {
        val loadedSpiel = drawCardResult.spiel
        _uiState.value = GameScreenUiState.Loaded(
            game = loadedSpiel.toUiState(aktuelleKarte = drawCardResult.karte),
        )
    }

    private fun Spiel.toUiState(aktuelleKarte: GezogeneKarte): GameUiState =
        toGameUiState(
            aktuelleKarte = aktuelleKarte,
            sprache = sprache,
            cardInstanceId = nextCardInstanceId++,
        )

    private fun spracheAusLocale(locale: Locale): Sprache =
        Sprache.entries.firstOrNull { sprache ->
            sprache.name == locale.language.uppercase(ROOT)
        } ?: Sprache.DE

    private companion object {
        const val DefaultGameId = 1
    }
}
