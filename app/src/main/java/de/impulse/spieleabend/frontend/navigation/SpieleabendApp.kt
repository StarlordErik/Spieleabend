package de.impulse.spieleabend.frontend.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.impulse.spieleabend.frontend.game.GAME_ID_ARG
import de.impulse.spieleabend.frontend.game.GameScreen
import de.impulse.spieleabend.frontend.start.StartScreen
import de.impulse.spieleabend.frontend.start.StartScreenUiState
import de.impulse.spieleabend.frontend.start.StartViewModel
import de.impulse.spieleabend.frontend.start.boardGameShelfItems
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@Composable
fun SpieleabendApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Start.route,
        modifier = modifier,
    ) {
        composable(AppDestination.Start.route) {
            val viewModel: StartViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            StartScreen(
                games = when (val state = uiState) {
                    StartScreenUiState.Loading -> emptyList()
                    is StartScreenUiState.Loaded -> state.games
                },
                onGameClick = { gameId ->
                    navController.navigate(AppDestination.Game.createRoute(gameId))
                },
            )
        }
        composable(AppDestination.Game.route) {
            GameScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpieleabendAppPreview() {
    SpieleabendTheme {
        StartScreen(
            games = boardGameShelfItems,
            onGameClick = {},
        )
    }
}

private sealed interface AppDestination {
    val route: String

    data object Start : AppDestination {
        override val route = "start"
    }

    data object Game : AppDestination {
        override val route = "game/{$GAME_ID_ARG}"

        fun createRoute(gameId: Int): String = "game/${Uri.encode(gameId.toString())}"
    }
}
