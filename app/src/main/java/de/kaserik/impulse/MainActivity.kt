package de.kaserik.impulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import de.kaserik.impulse.frontend.navigation.ImpulseApp
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(getColor(R.color.transparent)),
            navigationBarStyle = SystemBarStyle.dark(getColor(R.color.theme_background)),
        )

        setContent {
            ImpulseTheme {
                ImpulseApp()
            }
        }
    }
}
