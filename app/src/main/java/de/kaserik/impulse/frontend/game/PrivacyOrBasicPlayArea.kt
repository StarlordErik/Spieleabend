package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@Composable
internal fun PrivacyOrBasicPlayArea(
    uiState: GameUiState,
    privacyActive: Boolean,
    privacySession: PrivacySession,
    swipeControls: CardSwipeControls,
    cardTextActions: CardTextActions,
    developerMode: Boolean,
    onNextCard: () -> Unit,
    horizontalPadding: Dp,
) {
    if (privacyActive) {
        PrivacyPlayArea(
            uiState = uiState,
            session = privacySession,
            swipeControls = swipeControls,
            cardTextActions = cardTextActions,
            developerMode = developerMode,
            onNextCard = onNextCard,
            gameContentHorizontalPadding = horizontalPadding,
            modifier = Modifier.fillMaxSize().padding(top = 24.dp, bottom = 16.dp),
        )
    } else {
        GamePlayArea(
            spielName = uiState.spielName,
            aktuelleKarte = uiState.aktuelleKarte,
            kategorien = uiState.kategorien,
            swipeControls = swipeControls,
            cardTextActions = cardTextActions,
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
}

internal fun gameCategoryTabsVisible(
    privacyActive: Boolean,
    privacySession: PrivacySession,
    funFactsActive: Boolean,
    funFactsSession: FunFactsSession,
    funFactsTabsVisible: Boolean,
): Boolean = (!privacyActive || !privacySession.needsPlayerCount && privacySession.selectingQuestion) &&
        (!funFactsActive || !funFactsSession.needsPlayerCount &&
                (funFactsSession.selectingQuestion || funFactsTabsVisible))

@Preview(showBackground = true)
@Composable
private fun PrivacyOrBasicPlayAreaPreview() {
    ImpulseTheme {
        PrivacyOrBasicPlayArea(
            PreviewUiState, true, remember { previewPrivacySession() }, CardSwipeControls(),
            CardTextActions(), false, {}, 52.dp,
        )
    }
}
