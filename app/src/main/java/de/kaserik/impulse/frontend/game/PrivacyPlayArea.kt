package de.kaserik.impulse.frontend.game

import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.frontend.theme.CategoryTabColors
import de.kaserik.impulse.frontend.theme.FallbackTextPanelColor
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import kotlinx.coroutines.launch

@Composable
internal fun PrivacyPlayArea(
    uiState: GameUiState,
    session: PrivacySession,
    modifier: Modifier = Modifier,
    swipeControls: CardSwipeControls = CardSwipeControls(),
    cardTextActions: CardTextActions = CardTextActions(),
    developerMode: Boolean = false,
    onNextCard: () -> Unit = {},
    gameContentHorizontalPadding: Dp = 0.dp,
) {
    val window = LocalActivity.current?.window
    DisposableEffect(window) {
        val previousMode = window?.attributes?.softInputMode
        if (previousMode != null) {
            window.setSoftInputMode(
                (previousMode and WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST.inv()) or
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING,
            )
        }
        onDispose { if (previousMode != null) window.setSoftInputMode(previousMode) }
    }
    if (session.needsPlayerCount) {
        PlayerCountSetup(session::configurePlayerCount, modifier, session.minimumPlayerCount)
        return
    }
    val motion = remember { PrivacyQuestionMotion() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(uiState.aktuelleKarte.instanceId) {
        motion.questionBounds.keys.retainAll(uiState.aktuelleKarte.kartentexte.map { it.id }
            .toSet())
        if (motion.awaitingCardId != uiState.aktuelleKarte.instanceId) {
            motion.awaitingCardId = null
            motion.cardAlpha.animateTo(1f, tween(PRIVACY_CARD_FADE_MILLIS))
        }
    }
    val areaModifier = modifier
        .graphicsLayer { alpha = motion.cardAlpha.value }
        .onGloballyPositioned { motion.areaBounds = it.boundsInRoot() }

    if (session.selectingQuestion) {
        GamePlayArea(
            spielName = uiState.spielName,
            aktuelleKarte = uiState.aktuelleKarte,
            kategorien = uiState.kategorien,
            swipeControls = swipeControls,
            developerMode = developerMode,
            interactionsEnabled = motion.awaitingCardId == null,
            onKartentextBoundsChanged = { id, bounds -> motion.questionBounds[id] = bounds },
            cardTextActions = cardTextActions.copy(
                onKartentextManuallyPlayedStateChanged = { id, _ ->
                    motion.newQuestionId = id
                    session.selectQuestion(id, uiState.aktuelleKarte.instanceId)
                },
            ),
            modifier = areaModifier.padding(horizontal = gameContentHorizontalPadding),
        )
    } else {
        PrivacyQuestionStage(
            uiState = uiState,
            session = session,
            motion = motion,
            cardTextActions = cardTextActions,
            developerMode = developerMode,
            gameContentHorizontalPadding = gameContentHorizontalPadding,
            onNextRound = {
                if (!motion.advancing) {
                    motion.advancing = true
                    scope.launch {
                        motion.cardAlpha.animateTo(0f, tween(PRIVACY_CARD_FADE_MILLIS))
                        motion.awaitingCardId = uiState.aktuelleKarte.instanceId
                        session.startNextRound()
                        onNextCard()
                        motion.advancing = false
                    }
                }
            },
            modifier = areaModifier,
        )
    }
}

@Composable
private fun PrivacyQuestionStage(
    uiState: GameUiState,
    session: PrivacySession,
    motion: PrivacyQuestionMotion,
    cardTextActions: CardTextActions,
    developerMode: Boolean,
    gameContentHorizontalPadding: Dp,
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val question =
        uiState.aktuelleKarte.kartentexte.firstOrNull { it.id == session.selectedQuestionId }
            ?: return
    val focusManager = LocalFocusManager.current
    val index = uiState.aktuelleKarte.kartentexte.indexOf(question)
    val colors = uiState.aktuelleKarte.textPanelColors(
        uiState.kategorien, CategoryTabColors, FallbackTextPanelColor,
    )
    val progress =
        remember(question.id) { Animatable(if (motion.newQuestionId == question.id) 0f else 1f) }
    var returning by remember(question.id) { mutableStateOf(false) }
    LaunchedEffect(question.id, returning) {
        progress.animateTo(if (returning) 0f else 1f, tween(PRIVACY_QUESTION_MOVE_MILLIS))
        motion.newQuestionId = null
        if (returning) {
            session.reopenQuestionSelection()
        }
    }
    BoxWithConstraints(modifier) {
        val placement = questionPlacement(
            container = DpSize(maxWidth, maxHeight),
            questionOrigin = null,
            originBounds = motion.questionBounds[question.id],
            playAreaBounds = motion.areaBounds,
            cardTextCount = 1,
            density = LocalDensity.current,
            progress = progress.value,
        )
        PrivacyQuestionBackdrop(
            uiState,
            question.id,
            motion,
            progress.value,
            gameContentHorizontalPadding
        )
        PrivacyRoundBody(
            session, onNextRound, !motion.advancing, placement.targetHeight,
            progress.value >= 1f && !returning,
        )
        CardTextPanel(
            kartentext = question,
            index = index,
            kartentextCount = uiState.aktuelleKarte.kartentexte.size,
            textPanelColor = colors[index],
            interactionsEnabled = progress.value >= 1f && session.canChangeQuestion && !returning,
            markerInteractionsEnabled = progress.value >= 1f && !returning,
            developerMode = developerMode,
            cardTextActions = cardTextActions.copy(onKartentextManuallyPlayedStateChanged = { _, _ ->
                focusManager.clearFocus()
                returning = true
            }),
            modifier = Modifier.placePrivacyQuestion(placement, progress.value),
        )
    }
}

private fun Modifier.placePrivacyQuestion(placement: QuestionPlacement, progress: Float): Modifier =
    offset {
        IntOffset(
            placement.offset.x.roundToPx(),
            (placement.offset.y + PrivacyQuestionTopGap * progress).roundToPx(),
        )
    }.width(placement.size.width).height(placement.size.height)

@Composable
private fun PrivacyQuestionBackdrop(
    uiState: GameUiState,
    questionId: Int,
    motion: PrivacyQuestionMotion,
    progress: Float,
    gameContentHorizontalPadding: Dp,
) {
    if (progress < 1f || questionId !in motion.questionBounds) {
        GamePlayArea(
            spielName = uiState.spielName,
            aktuelleKarte = uiState.aktuelleKarte,
            kategorien = uiState.kategorien,
            interactionsEnabled = false,
            hiddenCardTextIds = setOf(questionId),
            onKartentextBoundsChanged = { id, bounds -> motion.questionBounds[id] = bounds },
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = gameContentHorizontalPadding)
                .graphicsLayer { alpha = 1f - progress },
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 820)
@Composable
private fun PrivacyQuestionBackdropPreview() {
    ImpulseTheme {
        PrivacyQuestionBackdrop(
            PreviewUiState,
            PreviewUiState.aktuelleKarte.kartentexte.first().id,
            remember { PrivacyQuestionMotion() },
            0f,
            52.dp
        )
    }
}

@Composable
private fun PrivacyRoundBody(
    session: PrivacySession,
    onNextRound: () -> Unit,
    nextRoundEnabled: Boolean,
    headerHeight: Dp = 0.dp,
    visible: Boolean = true,
) {
    if (!visible) return
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(top = headerHeight + PrivacyQuestionTopGap + 16.dp)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            when (session.phase) {
                PrivacyPhase.EnterAnswer -> PrivacyAnswerEntry(session, Modifier.fillMaxSize())
                PrivacyPhase.AwaitingReveal, PrivacyPhase.Revealing, PrivacyPhase.Complete -> PrivacyDiceReveal(
                    session = session,
                    onNextRound = onNextRound,
                    nextRoundEnabled = nextRoundEnabled,
                    modifier = Modifier.fillMaxSize(),
                )

                PrivacyPhase.SelectQuestion -> Unit
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 364, heightDp = 570)
@Composable
private fun PrivacyRoundBodyPreview() {
    ImpulseTheme { PrivacyRoundBody(remember { previewPrivacySession() }, {}, true) }
}

private class PrivacyQuestionMotion {
    val questionBounds = mutableStateMapOf<Int, Rect>()
    var areaBounds = Rect.Zero
    var newQuestionId: Int? = null
    val cardAlpha = Animatable(1f)
    var awaitingCardId by mutableStateOf<Long?>(null)
    var advancing by mutableStateOf(false)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 820)
@Composable
private fun PrivacyPlayAreaPreview() {
    ImpulseTheme {
        PrivacyPlayArea(PreviewUiState, remember { previewPrivacySession() })
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 820)
@Composable
private fun PrivacyQuestionStagePreview() {
    ImpulseTheme {
        PrivacyQuestionStage(
            uiState = PreviewUiState,
            session = remember { previewPrivacySession() },
            motion = remember { PrivacyQuestionMotion() },
            cardTextActions = CardTextActions(),
            developerMode = false,
            gameContentHorizontalPadding = 52.dp,
            onNextRound = {},
        )
    }
}

internal fun previewPrivacySession(playerCount: Int = 5): PrivacySession = PrivacySession().apply {
    configurePlayerCount(playerCount)
    selectQuestion(questionId = 101, instanceId = 0)
    draft.updateName("Alex")
}

private val PrivacyQuestionTopGap = 24.dp
private const val PRIVACY_QUESTION_MOVE_MILLIS = 480
private const val PRIVACY_CARD_FADE_MILLIS = 220
