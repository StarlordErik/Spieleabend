package de.impulse.spieleabend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import de.impulse.spieleabend.frontend.navigation.SpieleabendApp
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(DARK_SYSTEM_BAR_COLOR),
        )

        setContent {
            SpieleabendTheme {
                SpieleabendApp()
            }
        }
    }

    private companion object {
        const val DARK_SYSTEM_BAR_COLOR = 0xFF0C0F15.toInt()
    }
}
