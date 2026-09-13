package de.kaserik.impulse.frontend.game

import androidx.compose.ui.geometry.Rect

internal data class CardTextActions(
    val onKartentextPlayedStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
    val onKartentextManuallyPlayedStateChanged: (Int, Boolean) -> Unit = onKartentextPlayedStateChanged,
    val onKartentextDeletedStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
    val onKartentextFavoriteStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
    val onKartentextEditRequested: (Int) -> Unit = {},
)

internal fun CardTextActions.withPlayedStateHandler(handler: (Int, Boolean) -> Unit): CardTextActions =
    copy(onKartentextPlayedStateChanged = handler, onKartentextManuallyPlayedStateChanged = handler)

internal data class CardSwipeControls(
    val swipeRegions: Collection<SwipeRegion> = emptyList(),
    val screenBounds: Rect = Rect.Zero,
    val previousCard: GameCardUiModel? = null,
    val nextDrawTarget: CardSwipeTarget = CardSwipeTarget.Random,
    val gestureInput: CardSwipeGestureInput = CardSwipeGestureInput(),
    val swipeRequest: CardSwipeRequest? = null,
    val onSwipeRequestConsumed: (Long) -> Unit = {},
    val onHighlightedTargetChanged: (CardSwipeTarget?) -> Unit = {},
    val onInteractionStateChanged: (Boolean) -> Unit = {},
    val navigationActions: GameNavigationActions = GameNavigationActions(),
) {
    val previousEnabled: Boolean get() = previousCard != null
}

internal data class PreparedCardSwipe(
    val card: GameCardUiModel,
    val commit: suspend () -> Unit,
)

internal data class GameNavigationActions(
    val onKategorieSelected: (Int) -> Unit = {},
    val onRandomSelected: () -> Unit = {},
    val onPreviousSelected: () -> Unit = {},
    val onNextSelected: () -> Unit = onRandomSelected,
    val prepareNextCard: suspend (CardSwipeTarget) -> PreparedCardSwipe? = { null },
) {
    fun select(target: CardSwipeTarget) {
        when (target) {
            CardSwipeTarget.Random -> onRandomSelected()
            CardSwipeTarget.Previous -> onPreviousSelected()
            is CardSwipeTarget.Category -> onKategorieSelected(target.id)
        }
    }
}
