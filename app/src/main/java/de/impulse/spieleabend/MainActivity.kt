package de.impulse.spieleabend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import de.impulse.spieleabend.frontend.hello.HelloScreen
import de.impulse.spieleabend.frontend.hello.HelloViewModel
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: HelloViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()

            SpieleabendTheme {
                HelloScreen(uiState.value)
            }
        }
    }
}
