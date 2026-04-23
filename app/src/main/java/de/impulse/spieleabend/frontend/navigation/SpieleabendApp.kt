package de.impulse.spieleabend.frontend.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.impulse.spieleabend.frontend.game.GAME_ID_ARG
import de.impulse.spieleabend.frontend.game.GameScreen
import de.impulse.spieleabend.frontend.start.StartScreen
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
            StartScreen(
                games = boardGameShelfItems,
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
        SpieleabendApp()
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
