package de.impulse.spieleabend.frontend.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.repository.AppSettingsRepository
import de.impulse.spieleabend.domain.usecase.DrawCardResult
import de.impulse.spieleabend.domain.usecase.DrawNextCardFromCategoryUseCase
import de.impulse.spieleabend.domain.usecase.DrawNextRandomCardUseCase
import de.impulse.spieleabend.domain.usecase.GetOrDrawInitialCardUseCase
import de.impulse.spieleabend.domain.usecase.ResetAllCardsForGameUseCase
import de.impulse.spieleabend.domain.usecase.ResetSeenCardsUseCase
import de.impulse.spieleabend.domain.usecase.ResetTextsPerCardUseCase
import de.impulse.spieleabend.domain.usecase.SetCardTextPlayedStateUseCase
import de.impulse.spieleabend.domain.usecase.SetTextsPerCardUseCase
import de.impulse.spieleabend.domain.usecase.ShowPreviousCardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale
import java.util.Locale.ROOT
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val drawNextCardFromCategory: DrawNextCardFromCategoryUseCase,
    private val drawNextRandomCard: DrawNextRandomCardUseCase,
    private val getOrDrawInitialCard: GetOrDrawInitialCardUseCase,
    private val showPreviousCard: ShowPreviousCardUseCase,
    private val setCardTextPlayedState: SetCardTextPlayedStateUseCase,
    private val resetSeenCardsUseCase: ResetSeenCardsUseCase,
    private val resetAllCardsUseCase: ResetAllCardsForGameUseCase,
    private val setTextsPerCardUseCase: SetTextsPerCardUseCase,
    private val resetTextsPerCardUseCase: ResetTextsPerCardUseCase,
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel() {
    private val gameIdArg: String? = savedStateHandle[GAME_ID_ARG]
    private val gameId: Int = gameIdArg?.toIntOrNull() ?: DefaultGameId
    private val sprache: Sprache = spracheAusLocale(Locale.getDefault())
    private val cardChangeMutex = Mutex()
    private var funFactsModeEnabled = true
    private var funFactsPersistenceJob: Job? = null

    internal var funFactsSession by mutableStateOf(
        FunFactsSession.restore(appSettingsRepository.getFunFactsSession(), ::scheduleFunFactsPersistence),
    )
        private set

    private val _uiState = MutableStateFlow<GameScreenUiState>(GameScreenUiState.Loading)

    val uiState: StateFlow<GameScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appSettingsRepository.funFactsModeEnabled.collect { enabled ->
                funFactsModeEnabled = enabled
                val state = _uiState.value as? GameScreenUiState.Loaded ?: return@collect
                _uiState.value = GameScreenUiState.Loaded(state.game.copy(funFactsModeEnabled = enabled))
            }
        }
        viewModelScope.launch {
            cardChangeMutex.withLock {
                showCard(getOrDrawInitialCard(gameId))
            }
        }
    }

    fun selectKategorie(kategorieId: Int) {
        viewModelScope.launch {
            cardChangeMutex.withLock {
                showCard(
                    drawNextCardFromCategory(
                        gameId = gameId,
                        kategorieId = kategorieId,
                    ),
                )
            }
        }
    }

    fun selectRandom() {
        viewModelScope.launch {
            cardChangeMutex.withLock {
                showCard(drawNextRandomCard(gameId))
            }
        }
    }

    fun setFunFactsModeEnabled(enabled: Boolean) {
        val state = _uiState.value as? GameScreenUiState.Loaded
        if (state != null) {
            _uiState.value = GameScreenUiState.Loaded(state.game.copy(funFactsModeEnabled = enabled))
        }
        viewModelScope.launch { appSettingsRepository.setFunFactsModeEnabled(enabled) }
    }

    fun selectPrevious() {
        viewModelScope.launch {
            cardChangeMutex.withLock {
                showPreviousCard(gameId)?.let(::showCard)
            }
        }
    }

    fun resetSeenCards() {
        viewModelScope.launch { resetSeenCardsUseCase(gameId) }
    }

    fun resetAllCards() {
        val currentState = _uiState.value as? GameScreenUiState.Loaded
        if (currentState != null) {
            _uiState.value = GameScreenUiState.Loaded(currentState.game.withAllCardTextsUnplayed())
        }
        viewModelScope.launch { resetAllCardsUseCase(gameId) }
    }

    fun setTextsPerCard(value: Int) {
        updateTextCount(value)
        viewModelScope.launch { setTextsPerCardUseCase(gameId, value) }
    }

    fun resetTextsPerCard() {
        val state = _uiState.value as? GameScreenUiState.Loaded ?: return
        updateTextCount(state.game.standardTexteProKarte)
        viewModelScope.launch { resetTextsPerCardUseCase(gameId) }
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

    override fun onCleared() {
        appSettingsRepository.setFunFactsSession(funFactsSession.serialize())
    }

    private fun scheduleFunFactsPersistence() {
        funFactsPersistenceJob?.cancel()
        funFactsPersistenceJob = viewModelScope.launch {
            delay(FUN_FACTS_PERSISTENCE_DELAY_MILLIS)
            appSettingsRepository.setFunFactsSession(funFactsSession.serialize())
        }
    }

    private fun showCard(drawCardResult: DrawCardResult) {
        val loadedSpiel = drawCardResult.spiel
        _uiState.value = GameScreenUiState.Loaded(
            game = loadedSpiel.toUiState(
                aktuelleKarte = drawCardResult.karte,
                cardInstanceId = drawCardResult.instanceId,
                hasPreviousCard = drawCardResult.hasPrevious,
            ).copy(funFactsModeEnabled = funFactsModeEnabled),
        )
    }

    private fun updateTextCount(value: Int) {
        val state = _uiState.value as? GameScreenUiState.Loaded ?: return
        _uiState.value = GameScreenUiState.Loaded(
            state.game.copy(texteProKarte = value.coerceIn(MIN_TEXTS_PER_CARD, MAX_TEXTS_PER_CARD)),
        )
    }

    private fun Spiel.toUiState(
        aktuelleKarte: GezogeneKarte,
        cardInstanceId: Long,
        hasPreviousCard: Boolean,
    ): GameUiState =
        toGameUiState(
            aktuelleKarte = aktuelleKarte,
            sprache = sprache,
            cardInstanceId = cardInstanceId,
            hasPreviousCard = hasPreviousCard,
        )

    private fun spracheAusLocale(locale: Locale): Sprache =
        Sprache.entries.firstOrNull { sprache ->
            sprache.name == locale.language.uppercase(ROOT)
        } ?: Sprache.DE

    private companion object {
        const val DefaultGameId = 1
        const val MIN_TEXTS_PER_CARD = 1
        const val MAX_TEXTS_PER_CARD = 5
        const val FUN_FACTS_PERSISTENCE_DELAY_MILLIS = 250L
    }
}
