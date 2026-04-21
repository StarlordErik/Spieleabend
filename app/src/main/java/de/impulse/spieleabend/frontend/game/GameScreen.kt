package de.impulse.spieleabend.frontend.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenContent(
        uiState = uiState,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    SpieleabendTheme {
        GameScreenContent(
            uiState = GameUiState(message = "Hello World!"),
        )
    }
}

@Composable
private fun GameScreenContent(
    uiState: GameUiState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = uiState.message,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenContentPreview() {
    SpieleabendTheme {
        GameScreenContent(
            uiState = GameUiState(message = "Hello World!"),
        )
    }
}
