package de.kaserik.impulse.frontend.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.kaserik.impulse.common.GAME_ID_ARG
import de.kaserik.impulse.common.NavigationRoutes
import de.kaserik.impulse.frontend.cards.CardTextsScreen
import de.kaserik.impulse.frontend.game.GameScreen
import de.kaserik.impulse.frontend.settings.AppSettingsDialog
import de.kaserik.impulse.frontend.settings.AppSettingsViewModel
import de.kaserik.impulse.frontend.start.StartScreen
import de.kaserik.impulse.frontend.start.StartScreenUiState
import de.kaserik.impulse.frontend.start.StartViewModel
import de.kaserik.impulse.frontend.start.boardGameShelfItems
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@Composable
fun ImpulseApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val appSettingsViewModel: AppSettingsViewModel = hiltViewModel()
    val developerMode by appSettingsViewModel.developerMode.collectAsStateWithLifecycle()
    val language by appSettingsViewModel.language.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Start.route,
        modifier = modifier,
    ) {
        composable(AppDestination.Start.route) {
            val viewModel: StartViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            var showSettings by rememberSaveable { mutableStateOf(false) }

            StartScreen(
                games = when (val state = uiState) {
                    StartScreenUiState.Loading -> emptyList()
                    is StartScreenUiState.Loaded -> state.games
                },
                onGameClick = { gameId ->
                    navController.navigate(AppDestination.Game.createRoute(gameId))
                },
                onSettingsClick = { showSettings = true },
            )
            if (showSettings) {
                AppSettingsDialog(
                    developerMode = developerMode,
                    language = language,
                    onDeveloperModeChanged = appSettingsViewModel::setDeveloperMode,
                    onLanguageChanged = appSettingsViewModel::setLanguage,
                    onResetAllCards = appSettingsViewModel::resetAllCards,
                    onDismiss = { showSettings = false },
                )
            }
        }
        composable(AppDestination.Game.route) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString(GAME_ID_ARG)?.toIntOrNull() ?: 1
            GameScreen(
                developerMode = developerMode,
                onShowCards = { navController.navigate(AppDestination.Cards.createRoute(gameId)) },
            )
        }
        composable(AppDestination.Cards.route) {
            if (developerMode) {
                CardTextsScreen(onBack = { navController.popBackStack() })
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImpulseAppPreview() {
    ImpulseTheme {
        StartScreen(
            games = boardGameShelfItems,
            onGameClick = {},
        )
    }
}

private sealed interface AppDestination {
    val route: String

    data object Start : AppDestination {
        override val route = NavigationRoutes.START
    }

    data object Game : AppDestination {
        override val route = NavigationRoutes.GAME

        fun createRoute(gameId: Int): String = NavigationRoutes.GAME_PREFIX + Uri.encode(gameId.toString())
    }

    data object Cards : AppDestination {
        override val route = NavigationRoutes.CARDS

        fun createRoute(gameId: Int): String = NavigationRoutes.CARDS_PREFIX + Uri.encode(gameId.toString())
    }
}
