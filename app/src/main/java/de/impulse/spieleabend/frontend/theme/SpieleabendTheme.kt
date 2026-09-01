package de.impulse.spieleabend.frontend.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.impulse.spieleabend.R

private val DarkColors = darkColorScheme(
    onPrimary = Color(0xFF2D2108),
    primaryContainer = Color(0xFF4D3910),
    onPrimaryContainer = Color(0xFFFFDEA0),
    secondary = Color(0xFFA7D0C0),
    onSecondary = Color(0xFF10382E),
    secondaryContainer = Color(0xFF284E43),
    onSecondaryContainer = Color(0xFFC3ECDE),
    tertiary = Color(0xFFD8B8E4),
    onTertiary = Color(0xFF3C2944),
    tertiaryContainer = Color(0xFF543F5C),
    onTertiaryContainer = Color(0xFFF4D5FF),
    background = Color(0xFF0C0F15),
    onBackground = Color(0xFFE7EAF0),
    surface = Color(0xFF0C0F15),
    onSurface = Color(0xFFE7EAF0),
    surfaceVariant = Color(0xFF282E39),
    onSurfaceVariant = Color(0xFFC2C7D0),
    outline = Color(0xFF89909E),
    outlineVariant = Color(0xFF3D4450),
    inverseSurface = Color(0xFFE7EAF0),
    inverseOnSurface = Color(0xFF2A3039),
    inversePrimary = Color(0xFF755A18),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    scrim = Color.Black,
)

private val BaseTypography = Typography()
private val AppTypography = Typography(
    displaySmall = BaseTypography.displaySmall.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.4).sp,
    ),
    headlineLarge = BaseTypography.headlineLarge.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.35).sp,
    ),
    headlineMedium = BaseTypography.headlineMedium.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.25).sp,
    ),
    titleLarge = BaseTypography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = BaseTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = BaseTypography.labelLarge.copy(letterSpacing = 0.25.sp),
)

private val AppShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
)

@Composable
fun SpieleabendTheme(content: @Composable () -> Unit) {
    val accentGold = colorResource(R.color.spieleabend_gold)

    MaterialTheme(
        colorScheme = DarkColors.copy(
            primary = accentGold,
            surfaceTint = accentGold,
        ),
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun SpieleabendThemePreview() {
    SpieleabendTheme {
        Surface {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Spieleabend",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
