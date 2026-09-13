package de.kaserik.impulse.frontend.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kaserik.impulse.common.GAME_ID_ARG
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.model.BearbeiteteKartentexteModus
import de.kaserik.impulse.domain.model.FavoritenModus
import de.kaserik.impulse.domain.model.GeloeschteKartentexteModus
import de.kaserik.impulse.domain.model.GezogeneKarte
import de.kaserik.impulse.domain.model.Spiel
import de.kaserik.impulse.domain.repository.AppSettingsRepository
import de.kaserik.impulse.domain.usecase.DrawCardResult
import de.kaserik.impulse.domain.usecase.DrawNextCardUseCase
import de.kaserik.impulse.domain.usecase.GetOrDrawInitialCardUseCase
import de.kaserik.impulse.domain.usecase.ResetAllCardsForGameUseCase
import de.kaserik.impulse.domain.usecase.ResetSeenCardsUseCase
import de.kaserik.impulse.domain.usecase.ResetTextsPerCardUseCase
import de.kaserik.impulse.domain.usecase.SetCardTextPlayedStateUseCase
import de.kaserik.impulse.domain.usecase.SetTextsPerCardUseCase
import de.kaserik.impulse.domain.usecase.ShowPreviousCardUseCase
import de.kaserik.impulse.domain.usecase.UpdateCardTextSettingsUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@HiltViewModel
@Suppress("TooManyFunctions")
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val drawNextCard: DrawNextCardUseCase,
    private val getOrDrawInitialCard: GetOrDrawInitialCardUseCase,
    private val showPreviousCard: ShowPreviousCardUseCase,
    private val setCardTextPlayedState: SetCardTextPlayedStateUseCase,
    private val resetSeenCardsUseCase: ResetSeenCardsUseCase,
    private val resetAllCardsUseCase: ResetAllCardsForGameUseCase,
    private val setTextsPerCardUseCase: SetTextsPerCardUseCase,
    private val resetTextsPerCardUseCase: ResetTextsPerCardUseCase,
    private val updateCardTextSettings: UpdateCardTextSettingsUseCase,
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel() {
    private val gameIdArg: String? = savedStateHandle[GAME_ID_ARG]
    private val gameId: Int = gameIdArg?.toIntOrNull() ?: DEFAULT_GAME_ID
    private var lastDrawCategoryId = appSettingsRepository.getLastDrawCategoryId(gameId)
    private var sprache: Sprache = Sprache.DE
    private val cardChangeMutex = Mutex()
    private var funFactsModeEnabled = true
    private var privacyModeEnabled = true
    private var funFactsPersistenceJob: Job? = null

    internal var funFactsSession by mutableStateOf(
        FunFactsSession.restore(
            appSettingsRepository.getFunFactsSession(),
            ::scheduleFunFactsPersistence
        ),
    )
        private set

    internal val privacySession = PrivacySession.restore(
        appSettingsRepository.getPrivacySession(), ::persistPrivacySession,
    )

    private val _uiState = MutableStateFlow<GameScreenUiState>(GameScreenUiState.Loading)

    val uiState: StateFlow<GameScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appSettingsRepository.privacyModeEnabled.collect { enabled ->
                privacyModeEnabled = enabled
                val state = _uiState.value as? GameScreenUiState.Loaded ?: return@collect
                _uiState.value =
                    GameScreenUiState.Loaded(state.game.copy(privacyModeEnabled = enabled))
            }
        }
        viewModelScope.launch {
            appSettingsRepository.funFactsModeEnabled.collect { enabled ->
                funFactsModeEnabled = enabled
                val state = _uiState.value as? GameScreenUiState.Loaded ?: return@collect
                _uiState.value =
                    GameScreenUiState.Loaded(state.game.copy(funFactsModeEnabled = enabled))
            }
        }
        viewModelScope.launch {
            appSettingsRepository.language.collect { language ->
                cardChangeMutex.withLock {
                    val languageChanged = sprache != language
                    sprache = language
                    if (languageChanged && _uiState.value is GameScreenUiState.Loaded) {
                        showCard(getOrDrawInitialCard(gameId))
                    }
                }
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
                drawCard(kategorieId)
            }
        }
    }

    fun selectRandom() {
        viewModelScope.launch {
            cardChangeMutex.withLock {
                drawCard(categoryId = null)
            }
        }
    }

    fun selectNextFromLastCategory() {
        viewModelScope.launch {
            cardChangeMutex.withLock { drawFromLastCategory() }
        }
    }

    private suspend fun drawFromLastCategory() {
        val state = _uiState.value as? GameScreenUiState.Loaded ?: return
        val categoryId =
            lastDrawCategoryId?.takeIf { id -> state.game.kategorien.any { it.id == id } }
        drawCard(categoryId)
    }

    private suspend fun drawCard(categoryId: Int?) {
        val nextCard = drawNextCard(gameId, categoryId)
        lastDrawCategoryId = categoryId
        appSettingsRepository.setLastDrawCategoryId(gameId, categoryId)
        showCard(nextCard)
    }

    fun setFunFactsModeEnabled(enabled: Boolean) {
        val state = _uiState.value as? GameScreenUiState.Loaded
        if (state != null) {
            _uiState.value =
                GameScreenUiState.Loaded(state.game.copy(funFactsModeEnabled = enabled))
        }
        viewModelScope.launch { appSettingsRepository.setFunFactsModeEnabled(enabled) }
    }

    fun setPrivacyModeEnabled(enabled: Boolean) {
        privacyModeEnabled = enabled
        updateGameSettings { it.copy(privacyModeEnabled = enabled) }
        viewModelScope.launch { appSettingsRepository.setPrivacyModeEnabled(enabled) }
    }

    fun selectPrevious() {
        viewModelScope.launch {
            cardChangeMutex.withLock {
                showPreviousCard(gameId)?.let { showCard(it) }
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
    ) = updatePlayedState(cardTextId, gespielt, drawNext = false)

    fun setKartentextManuellGespielt(
        cardTextId: Int,
        gespielt: Boolean,
    ) = updatePlayedState(cardTextId, gespielt, drawNext = gespielt)

    private fun updatePlayedState(
        cardTextId: Int,
        gespielt: Boolean,
        drawNext: Boolean,
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
                cardChangeMutex.withLock {
                    setCardTextPlayedState(cardTextId = cardTextId, gespielt = gespielt)
                    val latestState = _uiState.value as? GameScreenUiState.Loaded
                    if (drawNext && latestState?.game?.aktuelleKarte?.instanceId ==
                        currentState.game.aktuelleKarte.instanceId
                    ) {
                        drawFromLastCategory()
                    }
                }
            }
        }
    }

    fun setKartentextGeloescht(
        cardTextId: Int,
        deleted: Boolean,
    ) {
        val state = _uiState.value as? GameScreenUiState.Loaded ?: return
        if (state.game.aktuelleKarte.kartentexte.none { cardText -> cardText.id == cardTextId }) return
        _uiState.value = GameScreenUiState.Loaded(
            state.game.withCardTextDeletedState(cardTextId, deleted),
        )
        viewModelScope.launch {
            cardChangeMutex.withLock {
                updateCardTextSettings.setDeleted(cardTextId, deleted)
            }
        }
    }

    fun setKartentextFavorit(
        cardTextId: Int,
        favorite: Boolean,
    ) {
        val state = _uiState.value as? GameScreenUiState.Loaded ?: return
        if (state.game.aktuelleKarte.kartentexte.none { cardText -> cardText.id == cardTextId }) return
        _uiState.value = GameScreenUiState.Loaded(
            state.game.withCardTextFavoriteState(cardTextId, favorite),
        )
        viewModelScope.launch {
            cardChangeMutex.withLock {
                updateCardTextSettings.setFavorite(cardTextId, favorite)
            }
        }
    }

    fun setEigeneKartentextLokalisierung(
        cardTextId: Int,
        text: String?,
    ) {
        viewModelScope.launch {
            cardChangeMutex.withLock {
                updateCardTextSettings.setCustomTranslation(cardTextId, sprache, text)
                showCard(getOrDrawInitialCard(gameId))
            }
        }
    }

    fun applyErikTranslations(overwriteExisting: Boolean) {
        viewModelScope.launch {
            cardChangeMutex.withLock {
                updateCardTextSettings.applyErikTranslations(gameId, overwriteExisting)
                showCard(getOrDrawInitialCard(gameId))
            }
        }
    }

    fun resetCustomTranslations() {
        viewModelScope.launch {
            cardChangeMutex.withLock {
                updateCardTextSettings.resetCustomTranslations(gameId)
                showCard(getOrDrawInitialCard(gameId))
            }
        }
    }

    fun setGeloeschteKartentexteModus(mode: GeloeschteKartentexteModus) {
        updateGameSettings { game -> game.copy(geloeschteKartentexteModus = mode) }
        viewModelScope.launch {
            cardChangeMutex.withLock {
                updateCardTextSettings.setDeletedMode(gameId, mode)
            }
        }
    }

    fun setFavoritenModus(mode: FavoritenModus) {
        updateGameSettings { game -> game.copy(favoritenModus = mode) }
        viewModelScope.launch {
            cardChangeMutex.withLock {
                updateCardTextSettings.setFavoritesMode(gameId, mode)
            }
        }
    }

    fun setBearbeiteteKartentexteModus(mode: BearbeiteteKartentexteModus) {
        updateGameSettings { game -> game.copy(bearbeiteteKartentexteModus = mode) }
        viewModelScope.launch {
            cardChangeMutex.withLock {
                updateCardTextSettings.setEditedMode(gameId, mode)
            }
        }
    }

    override fun onCleared() {
        appSettingsRepository.setFunFactsSession(funFactsSession.serialize())
        if (gameId == PRIVACY_GAME_ID) persistPrivacySession()
    }

    private fun persistPrivacySession() {
        appSettingsRepository.setPrivacySession(privacySession.serialize())
    }

    private fun scheduleFunFactsPersistence() {
        funFactsPersistenceJob?.cancel()
        funFactsPersistenceJob = viewModelScope.launch {
            delay(FUN_FACTS_PERSISTENCE_DELAY_MILLIS.milliseconds)
            appSettingsRepository.setFunFactsSession(funFactsSession.serialize())
        }
    }

    private suspend fun showCard(drawCardResult: DrawCardResult) {
        val loadedSpiel = drawCardResult.spiel
        val previous = showPreviousCard.preview(gameId)
        if (gameId == PRIVACY_GAME_ID) privacySession.onCardChanged(drawCardResult.instanceId)
        _uiState.value = GameScreenUiState.Loaded(
            game = loadedSpiel.toUiState(
                aktuelleKarte = drawCardResult.karte,
                cardInstanceId = drawCardResult.instanceId,
                hasPreviousCard = drawCardResult.hasPrevious,
            ).copy(
                previousCard = previous?.card?.toGameCardUiModel(sprache, previous.instanceId),
                lastDrawCategoryId = lastDrawCategoryId,
                funFactsModeEnabled = funFactsModeEnabled,
                privacyModeEnabled = privacyModeEnabled
            ),
        )
    }

    private fun updateTextCount(value: Int) {
        val state = _uiState.value as? GameScreenUiState.Loaded ?: return
        _uiState.value = GameScreenUiState.Loaded(
            state.game.copy(texteProKarte = value.coerceIn(MIN_TEXTS_PER_CARD, MAX_TEXTS_PER_CARD)),
        )
    }

    private fun updateGameSettings(transform: (GameUiState) -> GameUiState) {
        val state = _uiState.value as? GameScreenUiState.Loaded ?: return
        _uiState.value = GameScreenUiState.Loaded(transform(state.game))
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

    private companion object {
        const val DEFAULT_GAME_ID = 1
        const val MIN_TEXTS_PER_CARD = 1
        const val MAX_TEXTS_PER_CARD = 5
        const val FUN_FACTS_PERSISTENCE_DELAY_MILLIS = 250L
    }
}
