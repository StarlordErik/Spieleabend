package de.kaserik.impulse.frontend.game

internal data class CardTextActions(
    val onKartentextPlayedStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
    val onKartentextDeletedStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
    val onKartentextFavoriteStateChanged: (Int, Boolean) -> Unit = { _, _ -> },
    val onKartentextEditRequested: (Int) -> Unit = {},
)

internal data class CardSwipeControls(
    val swipeRegions: Collection<SwipeRegion> = emptyList(),
    val previousEnabled: Boolean = false,
    val swipeRequest: CardSwipeRequest? = null,
    val onSwipeRequestConsumed: (Long) -> Unit = {},
    val onHighlightedTargetChanged: (CardSwipeTarget?) -> Unit = {},
    val onInteractionStateChanged: (Boolean) -> Unit = {},
    val onSwipeTargetSelected: (CardSwipeTarget) -> Unit = {},
)

internal data class GameNavigationActions(
    val onKategorieSelected: (Int) -> Unit = {},
    val onRandomSelected: () -> Unit = {},
    val onPreviousSelected: () -> Unit = {},
) {
    fun select(target: CardSwipeTarget) {
        when (target) {
            CardSwipeTarget.Random -> onRandomSelected()
            CardSwipeTarget.Previous -> onPreviousSelected()
            is CardSwipeTarget.Category -> onKategorieSelected(target.id)
        }
    }
}
