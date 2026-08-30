@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme
import de.impulse.spieleabend.frontend.settings.GameSettingsDialog

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
                onKategorieSelected = viewModel::selectKategorie,
                onRandomSelected = viewModel::selectRandom,
                onPreviousSelected = viewModel::selectPrevious,
                onResetSeenCards = viewModel::resetSeenCards,
                onResetAllCards = viewModel::resetAllCards,
                onTextsPerCardChanged = viewModel::setTextsPerCard,
                onResetTextsPerCard = viewModel::resetTextsPerCard,
                onKartentextPlayedStateChanged = viewModel::setKartentextGespielt,
                onFunFactsModeChanged = viewModel::setFunFactsModeEnabled,
                funFactsSession = viewModel.funFactsSession,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    SpieleabendTheme {
        GameScreenContent(uiState = PreviewUiState)
    }
}

@Preview(showBackground = true)
@Composable
private fun GameLoadingContentPreview() {
    SpieleabendTheme {
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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = TitleColor)
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
    onKategorieSelected: (Int) -> Unit = {},
    onRandomSelected: () -> Unit = {},
    onPreviousSelected: () -> Unit = {},
    onResetSeenCards: () -> Unit = {},
    onResetAllCards: () -> Unit = {},
    onTextsPerCardChanged: (Int) -> Unit = {},
    onResetTextsPerCard: () -> Unit = {},
    onKartentextPlayedStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
    onFunFactsModeChanged: (Boolean) -> Unit = {},
    funFactsSession: FunFactsSession? = null,
) {
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var highlightedTarget by remember { mutableStateOf<CardSwipeTarget?>(null) }
    var swipeInteractionLocked by remember { mutableStateOf(false) }
    var funFactsQuestionTransitionActive by remember { mutableStateOf(false) }
    val tabBounds = remember { mutableStateMapOf<CardSwipeTarget, Rect>() }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = TableBackground,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val horizontalPadding = if (maxWidth < CompactWidthBreakpoint) {
                CompactHorizontalPadding
            } else {
                ExpandedHorizontalPadding
            }

            val funFactsActive = uiState.spielId == FUN_FACTS_GAME_ID && uiState.funFactsModeEnabled
            val activeFunFactsSession = funFactsSession ?: remember { FunFactsSession() }
            if (funFactsActive) {
                FunFactsPlayArea(
                    uiState = uiState,
                    session = activeFunFactsSession,
                    swipeRegions = tabBounds.map { (target, bounds) -> SwipeRegion(target, bounds) },
                    previousEnabled = uiState.hasPreviousCard,
                    onHighlightedTargetChanged = { highlightedTarget = it },
                    onInteractionStateChanged = { swipeInteractionLocked = it },
                    onSwipeTargetSelected = { target ->
                        when (target) {
                            CardSwipeTarget.Random -> onRandomSelected()
                            CardSwipeTarget.Previous -> onPreviousSelected()
                            is CardSwipeTarget.Category -> onKategorieSelected(target.id)
                        }
                    },
                    onKartentextPlayedStateChanged = onKartentextPlayedStateChanged,
                    onQuestionTransitionStateChanged = { active ->
                        funFactsQuestionTransitionActive = active
                    },
                    onNextCard = onRandomSelected,
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
                    swipeRegions = tabBounds.map { (target, bounds) -> SwipeRegion(target, bounds) },
                    previousEnabled = uiState.hasPreviousCard,
                    onHighlightedTargetChanged = { highlightedTarget = it },
                    onInteractionStateChanged = { swipeInteractionLocked = it },
                    onSwipeTargetSelected = { target ->
                        when (target) {
                            CardSwipeTarget.Random -> onRandomSelected()
                            CardSwipeTarget.Previous -> onPreviousSelected()
                            is CardSwipeTarget.Category -> onKategorieSelected(target.id)
                        }
                    },
                    onKartentextPlayedStateChanged = onKartentextPlayedStateChanged,
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
                funFactsQuestionTransitionActive
            AnimatedVisibility(
                visible = categoryTabsVisible,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(CATEGORY_TAB_TRANSITION_DURATION_MILLIS)),
                exit = fadeOut(tween(0)),
            ) {
                CategoryTabs(
                    kategorien = uiState.kategorien,
                    modifier = Modifier.fillMaxSize(),
                    highlightedTarget = highlightedTarget,
                    previousEnabled = uiState.hasPreviousCard,
                    interactionsEnabled = !swipeInteractionLocked &&
                        !funFactsQuestionTransitionActive,
                    onKategorieSelected = { kategorieId -> onKategorieSelected(kategorieId) },
                    onRandomSelected = onRandomSelected,
                    onPreviousSelected = onPreviousSelected,
                    onTabBoundsChanged = { target, bounds ->
                        if (tabBounds[target] != bounds) tabBounds[target] = bounds
                    },
                )
            }

            IconButton(
                onClick = { showSettings = true },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .semantics { contentDescription = "Spieleinstellungen" },
            ) {
                Text(text = "\u2699", color = TitleColor, style = MaterialTheme.typography.headlineMedium)
            }
        }
    }

    if (showSettings) {
        GameSettingsDialog(
            textsPerCard = uiState.texteProKarte,
            defaultTextsPerCard = uiState.standardTexteProKarte,
            developerMode = developerMode,
            supportsFunFactsMode = uiState.spielId == FUN_FACTS_GAME_ID,
            funFactsModeEnabled = uiState.funFactsModeEnabled,
            onFunFactsModeChanged = onFunFactsModeChanged,
            onResetSeenCards = onResetSeenCards,
            onResetAllCards = onResetAllCards,
            onTextsPerCardChanged = onTextsPerCardChanged,
            onResetTextsPerCard = onResetTextsPerCard,
            onShowCards = {
                showSettings = false
                onShowCards()
            },
            onDismiss = { showSettings = false },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GamePlayAreaPreview() {
    SpieleabendTheme {
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
    swipeRegions: Collection<SwipeRegion> = emptyList(),
    previousEnabled: Boolean = false,
    onHighlightedTargetChanged: (CardSwipeTarget?) -> Unit = {},
    onInteractionStateChanged: (Boolean) -> Unit = {},
    onSwipeTargetSelected: (CardSwipeTarget) -> Unit = {},
    interactionsEnabled: Boolean = true,
    onKartentextBoundsChanged: (Int, Rect) -> Unit = { _, _ -> },
    onKartentextPlayedStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
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
                swipeRegions = swipeRegions,
                previousEnabled = previousEnabled,
                onHighlightedTargetChanged = onHighlightedTargetChanged,
                onInteractionStateChanged = onInteractionStateChanged,
                onTargetSelected = onSwipeTargetSelected,
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .heightIn(max = 720.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = 12.dp),
            ) { idleEffectsEnabled ->
                GameCard(
                    kartentexte = aktuelleKarte.kartentexte,
                    cardInstanceId = aktuelleKarte.instanceId,
                    textPanelColors = aktuelleKarte.textPanelColors(kategorien),
                    idleEffectsEnabled = idleEffectsEnabled,
                    interactionsEnabled = interactionsEnabled,
                    onKartentextBoundsChanged = onKartentextBoundsChanged,
                    onKartentextPlayedStateChanged = onKartentextPlayedStateChanged,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenContentPreview() {
    SpieleabendTheme {
        GameScreenContent(uiState = PreviewUiState)
    }
}

private val TableBackground = Color(0xFFE5EFE9)
private val TitleColor = Color(0xFF22201D)
private val CompactWidthBreakpoint = 420.dp
private val CompactHorizontalPadding = 52.dp
private val ExpandedHorizontalPadding = 76.dp
private const val CATEGORY_TAB_TRANSITION_DURATION_MILLIS = 480

internal fun GameCardUiModel.textPanelColors(kategorien: List<GameKategorieUiModel>): List<Color> =
    kartentexte.map { kartentext ->
        val kategorieIndex = kategorien.indexOfFirst { kategorie ->
            kategorie.id == kartentext.kategorieId
        }

        if (kategorieIndex >= 0) {
            categoryTabColor(kategorieIndex)
        } else {
            FallbackTextPanelColor
        }
    }

private val FallbackTextPanelColor = Color(0xFFE8E0FF)
internal const val FUN_FACTS_GAME_ID = 149
