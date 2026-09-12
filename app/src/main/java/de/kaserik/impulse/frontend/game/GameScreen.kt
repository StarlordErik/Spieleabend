@file:Suppress("MagicNumber")

package de.kaserik.impulse.frontend.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Stable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.settings.GameSettingsActions
import de.kaserik.impulse.frontend.settings.CustomTranslationActions
import de.kaserik.impulse.frontend.settings.GameSettingsState
import de.kaserik.impulse.frontend.settings.GameSettingsDialog
import de.kaserik.impulse.frontend.theme.CategoryTabColors
import de.kaserik.impulse.frontend.theme.FallbackTextPanelColor
import de.kaserik.impulse.frontend.theme.GameTableBrush
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import de.kaserik.impulse.frontend.theme.TableBackground
import de.kaserik.impulse.frontend.theme.TitleColor

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    developerMode: Boolean = false,
    onShowCards: () -> Unit = {},
    viewModel: GameViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        GameScreenUiState.Loading -> GameLoadingContent(modifier = modifier)
        is GameScreenUiState.Loaded -> {
            GameScreenContent(
                uiState = state.game,
                modifier = modifier,
                developerMode = developerMode,
                onShowCards = onShowCards,
                navigationActions = GameNavigationActions(
                    onKategorieSelected = viewModel::selectKategorie,
                    onRandomSelected = viewModel::selectRandom,
                    onPreviousSelected = viewModel::selectPrevious,
                    onNextSelected = viewModel::selectNextFromLastCategory,
                ),
                settingsActions = GameSettingsActions(
                    onResetSeenCards = viewModel::resetSeenCards,
                    onResetAllCards = viewModel::resetAllCards,
                    onTextsPerCardChanged = viewModel::setTextsPerCard,
                    onResetTextsPerCard = viewModel::resetTextsPerCard,
                    onDeletedCardTextsModeChanged = viewModel::setGeloeschteKartentexteModus,
                    onFavoritesModeChanged = viewModel::setFavoritenModus,
                    onEditedCardTextsModeChanged = viewModel::setBearbeiteteKartentexteModus,
                    onFunFactsModeChanged = viewModel::setFunFactsModeEnabled,
                    customTranslationActions = CustomTranslationActions(
                        onApplyErikTranslations = viewModel::applyErikTranslations,
                        onResetCustomTranslations = viewModel::resetCustomTranslations,
                    ),
                ),
                cardTextActions = CardTextActions(
                    onKartentextPlayedStateChanged = viewModel::setKartentextGespielt,
                    onKartentextManuallyPlayedStateChanged = viewModel::setKartentextManuellGespielt,
                    onKartentextDeletedStateChanged = viewModel::setKartentextGeloescht,
                    onKartentextFavoriteStateChanged = viewModel::setKartentextFavorit,
                ),
                onCustomCardTextChanged = viewModel::setEigeneKartentextLokalisierung,
                funFactsSession = viewModel.funFactsSession,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    ImpulseTheme {
        GameScreenContent(uiState = PreviewUiState)
    }
}

@Preview(showBackground = true)
@Composable
private fun GameLoadingContentPreview() {
    ImpulseTheme {
        GameLoadingContent()
    }
}

@Composable
private fun GameLoadingContent(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = TableBackground,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GameTableBrush)
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
@Suppress("CyclomaticComplexMethod", "LongMethod")
private fun GameScreenContent(
    uiState: GameUiState,
    modifier: Modifier = Modifier,
    developerMode: Boolean = false,
    onShowCards: () -> Unit = {},
    navigationActions: GameNavigationActions = GameNavigationActions(),
    settingsActions: GameSettingsActions = GameSettingsActions(),
    cardTextActions: CardTextActions = CardTextActions(),
    onCustomCardTextChanged: (Int, String?) -> Unit = { _, _ -> },
    funFactsSession: FunFactsSession? = null,
) {
    val accessibilityLabel = stringResource(R.string.game_settings)
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var editingCardTextId by rememberSaveable { mutableStateOf<Int?>(null) }
    val swipeState = remember { GameScreenSwipeState() }
    var funFactsQuestionTransitionActive by remember { mutableStateOf(false) }
    var funFactsCategoryTabsVisible by remember { mutableStateOf(true) }
    val funFactsActive = uiState.spielId == FUN_FACTS_GAME_ID && uiState.funFactsModeEnabled
    val activeFunFactsSession = funFactsSession ?: remember { FunFactsSession() }
    val landscapeTarget = rememberFunFactsLandscapeTarget(
        rotationEnabled = funFactsActive &&
                activeFunFactsSession.phase == FunFactsPhase.EnterAnswer &&
                !funFactsQuestionTransitionActive && !showSettings && editingCardTextId == null,
        hasName = activeFunFactsSession.draftName.strokes.isNotEmpty(),
    )
    if (landscapeTarget != null) {
        FunFactsLandscapeDrawing(
            uiState = uiState,
            session = activeFunFactsSession,
            target = landscapeTarget,
            modifier = modifier,
        )
        return
    }

    val swipeControls = CardSwipeControls(
        swipeRegions = swipeState.tabBounds.map { (target, bounds) -> SwipeRegion(target, bounds) },
        previousEnabled = uiState.hasPreviousCard,
        swipeRequest = swipeState.swipeRequest,
        onSwipeRequestConsumed = swipeState::consumeRequest,
        onHighlightedTargetChanged = { swipeState.highlightedTarget = it },
        onInteractionStateChanged = { swipeState.swipeInteractionLocked = it },
        onSwipeTargetSelected = navigationActions::select,
    )
    val editableCardTextActions = cardTextActions.copy(
        onKartentextEditRequested = { editingCardTextId = it },
    )

    CompositionLocalProvider(LocalCardTimerPaused provides (showSettings || editingCardTextId != null)) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = TableBackground,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GameTableBrush)
                    .safeDrawingPadding(),
            ) {
                val horizontalPadding = if (maxWidth < CompactWidthBreakpoint) {
                    CompactHorizontalPadding
                } else {
                    ExpandedHorizontalPadding
                }

                if (funFactsActive) {
                    FunFactsPlayArea(
                        uiState = uiState,
                        session = activeFunFactsSession,
                        swipeControls = swipeControls,
                        cardTextActions = editableCardTextActions,
                        developerMode = developerMode,
                        transitionActions = FunFactsTransitionActions(
                            onQuestionTransitionStateChanged = { active ->
                                funFactsQuestionTransitionActive = active
                            },
                            onCategoryTabsVisibilityChanged = { visible ->
                                funFactsCategoryTabsVisible = visible
                            },
                            onNextCard = navigationActions.onNextSelected,
                        ),
                        gameContentHorizontalPadding = horizontalPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = 24.dp,
                                bottom = 24.dp,
                            ),
                    )
                } else {
                    GamePlayArea(
                        spielName = uiState.spielName,
                        aktuelleKarte = uiState.aktuelleKarte,
                        kategorien = uiState.kategorien,
                        swipeControls = swipeControls,
                        cardTextActions = editableCardTextActions,
                        developerMode = developerMode,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = horizontalPadding,
                                top = 24.dp,
                                end = horizontalPadding,
                                bottom = 24.dp,
                            ),
                    )
                }

                val categoryTabsVisible =
                    !funFactsActive ||
                            activeFunFactsSession.selectingQuestion ||
                            funFactsCategoryTabsVisible
                AnimatedVisibility(
                    visible = categoryTabsVisible,
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(tween(CATEGORY_TAB_TRANSITION_DURATION_MILLIS)),
                    exit = fadeOut(tween(CATEGORY_TAB_TRANSITION_DURATION_MILLIS)),
                ) {
                    CategoryTabs(
                        kategorien = uiState.kategorien,
                        modifier = Modifier.fillMaxSize(),
                        state = CategoryTabsState(
                            highlightedTarget = swipeState.highlightedTarget,
                            previousEnabled = uiState.hasPreviousCard,
                            interactionsEnabled = !swipeState.swipeInteractionLocked &&
                                    !funFactsQuestionTransitionActive,
                            dimWhenInteractionsDisabled = !funFactsActive,
                        ),
                        actions = CategoryTabsActions(
                            onKategorieSelected = { kategorieId ->
                                swipeState.requestSwipe(CardSwipeTarget.Category(kategorieId))
                            },
                            onRandomSelected = { swipeState.requestSwipe(CardSwipeTarget.Random) },
                            onPreviousSelected = { swipeState.requestSwipe(CardSwipeTarget.Previous) },
                            onTabBoundsChanged = swipeState::updateTabBounds,
                        ),
                    )
                }

                IconButton(
                    onClick = { showSettings = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .semantics { contentDescription = accessibilityLabel },
                ) {
                    Text(
                        text = stringResource(R.string.symbol_settings),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            }
        }
    }

    if (showSettings) {
        GameSettingsDialog(
            settings = GameSettingsState(
                textsPerCard = uiState.texteProKarte,
                defaultTextsPerCard = uiState.standardTexteProKarte,
                developerMode = developerMode,
                supportsFunFactsMode = uiState.spielId == FUN_FACTS_GAME_ID,
                funFactsModeEnabled = uiState.funFactsModeEnabled,
                deletedCardTextsMode = uiState.geloeschteKartentexteModus,
                favoritesMode = uiState.favoritenModus,
                editedCardTextsMode = uiState.bearbeiteteKartentexteModus,
            ),
            settingsActions = settingsActions,
            onRestartFunFactsGame = {
                activeFunFactsSession.selectedQuestionId?.let { questionId ->
                    cardTextActions.onKartentextPlayedStateChanged(questionId, false)
                }
                activeFunFactsSession.restartGame()
                funFactsQuestionTransitionActive = false
                funFactsCategoryTabsVisible = true
                swipeState.highlightedTarget = null
                swipeState.swipeInteractionLocked = false
                showSettings = false
            },
            onShowCards = {
                showSettings = false
                onShowCards()
            },
            onDismiss = { showSettings = false },
        )
    }

    val editingCardText = uiState.aktuelleKarte.kartentexte.firstOrNull { cardText ->
        cardText.id == editingCardTextId
    }
    if (developerMode && editingCardText != null) {
        CardTextEditorDialog(
            cardText = editingCardText,
            language = uiState.sprache,
            onSave = { text ->
                onCustomCardTextChanged(editingCardText.id, text)
                editingCardTextId = null
            },
            onDeleteOwnTranslation = {
                onCustomCardTextChanged(editingCardText.id, null)
                editingCardTextId = null
            },
            onDismiss = { editingCardTextId = null },
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun GamePlayAreaPreview() {
    ImpulseTheme {
        GamePlayArea(
            spielName = PreviewUiState.spielName,
            aktuelleKarte = PreviewUiState.aktuelleKarte,
            kategorien = PreviewUiState.kategorien,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
internal fun GamePlayArea(
    spielName: String,
    aktuelleKarte: GameCardUiModel,
    kategorien: List<GameKategorieUiModel>,
    modifier: Modifier = Modifier,
    swipeControls: CardSwipeControls = CardSwipeControls(),
    interactionsEnabled: Boolean = true,
    hiddenCardTextIds: Set<Int> = emptySet(),
    developerMode: Boolean = false,
    onKartentextBoundsChanged: (Int, Rect) -> Unit = { _, _ -> },
    cardTextActions: CardTextActions = CardTextActions(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = spielName,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            color = TitleColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
            ),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            SwipeableGameCard(
                cardInstanceId = aktuelleKarte.instanceId,
                swipeRegions = swipeControls.swipeRegions,
                previousEnabled = swipeControls.previousEnabled,
                swipeRequest = swipeControls.swipeRequest,
                onSwipeRequestConsumed = swipeControls.onSwipeRequestConsumed,
                onHighlightedTargetChanged = swipeControls.onHighlightedTargetChanged,
                onInteractionStateChanged = swipeControls.onInteractionStateChanged,
                onTargetSelected = swipeControls.onSwipeTargetSelected,
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .heightIn(max = 720.dp)
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
            ) { idleEffectsEnabled ->
                GameCard(
                    kartentexte = aktuelleKarte.kartentexte,
                    cardInstanceId = aktuelleKarte.instanceId,
                    textPanelColors = aktuelleKarte.textPanelColors(
                        kategorien, CategoryTabColors, FallbackTextPanelColor,
                    ),
                    idleEffectsEnabled = idleEffectsEnabled,
                    interactionsEnabled = interactionsEnabled,
                    hiddenCardTextIds = hiddenCardTextIds,
                    developerMode = developerMode,
                    onKartentextBoundsChanged = onKartentextBoundsChanged,
                    cardTextActions = cardTextActions,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenContentPreview() {
    ImpulseTheme {
        GameScreenContent(uiState = PreviewUiState)
    }
}

private val CompactWidthBreakpoint = 420.dp
private val CompactHorizontalPadding = 52.dp
private val ExpandedHorizontalPadding = 76.dp
private const val CATEGORY_TAB_TRANSITION_DURATION_MILLIS = 480

internal fun GameCardUiModel.textPanelColors(
    kategorien: List<GameKategorieUiModel>,
    categoryColors: List<Color>,
    fallbackColor: Color,
): List<Color> =
    kartentexte.map { kartentext ->
        val kategorieIndex = kategorien.indexOfFirst { kategorie ->
            kategorie.id == kartentext.kategorieId
        }

        if (kategorieIndex >= 0) {
            categoryTabColor(kategorieIndex, categoryColors)
        } else {
            fallbackColor
        }
    }

internal const val FUN_FACTS_GAME_ID = 149

@Stable
private class GameScreenSwipeState {
    var highlightedTarget by mutableStateOf<CardSwipeTarget?>(null)
    var swipeInteractionLocked by mutableStateOf(false)
    var swipeRequest by mutableStateOf<CardSwipeRequest?>(null)
        private set
    val tabBounds = mutableStateMapOf<CardSwipeTarget, Rect>()
    private var nextRequestId = 0L

    fun requestSwipe(target: CardSwipeTarget) {
        if (swipeRequest == null && !swipeInteractionLocked) {
            nextRequestId += 1
            swipeRequest = CardSwipeRequest(id = nextRequestId, target = target)
        }
    }

    fun consumeRequest(requestId: Long) {
        if (swipeRequest?.id == requestId) swipeRequest = null
    }

    fun updateTabBounds(target: CardSwipeTarget, bounds: Rect) {
        if (tabBounds[target] != bounds) tabBounds[target] = bounds
    }
}
