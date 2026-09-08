package de.kaserik.impulse.frontend.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.kaserik.impulse.R

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
fun ImpulseTheme(content: @Composable () -> Unit) {
    val darkColors = darkColorScheme(
        primary = colorResource(R.color.impulse_gold),
        surfaceTint = colorResource(R.color.impulse_gold),
        onPrimary = colorResource(R.color.theme_on_primary),
        primaryContainer = colorResource(R.color.theme_primary_container),
        onPrimaryContainer = colorResource(R.color.theme_on_primary_container),
        secondary = colorResource(R.color.theme_secondary),
        onSecondary = colorResource(R.color.theme_on_secondary),
        secondaryContainer = colorResource(R.color.theme_secondary_container),
        onSecondaryContainer = colorResource(R.color.theme_on_secondary_container),
        tertiary = colorResource(R.color.theme_tertiary),
        onTertiary = colorResource(R.color.theme_on_tertiary),
        tertiaryContainer = colorResource(R.color.theme_tertiary_container),
        onTertiaryContainer = colorResource(R.color.theme_on_tertiary_container),
        background = colorResource(R.color.theme_background),
        onBackground = colorResource(R.color.theme_on_background),
        surface = colorResource(R.color.theme_surface),
        onSurface = colorResource(R.color.theme_on_surface),
        surfaceVariant = colorResource(R.color.theme_surface_variant),
        onSurfaceVariant = colorResource(R.color.theme_on_surface_variant),
        outline = colorResource(R.color.theme_outline),
        outlineVariant = colorResource(R.color.theme_outline_variant),
        inverseSurface = colorResource(R.color.theme_inverse_surface),
        inverseOnSurface = colorResource(R.color.theme_inverse_on_surface),
        inversePrimary = colorResource(R.color.theme_inverse_primary),
        error = colorResource(R.color.theme_error),
        onError = colorResource(R.color.theme_on_error),
        errorContainer = colorResource(R.color.theme_error_container),
        onErrorContainer = colorResource(R.color.theme_on_error_container),
        surfaceBright = colorResource(R.color.theme_surface_bright),
        surfaceDim = colorResource(R.color.theme_surface_dim),
        surfaceContainer = colorResource(R.color.theme_surface_container),
        surfaceContainerHigh = colorResource(R.color.theme_surface_container_high),
        surfaceContainerHighest = colorResource(R.color.theme_surface_container_highest),
        surfaceContainerLow = colorResource(R.color.theme_surface_container_low),
        surfaceContainerLowest = colorResource(R.color.theme_surface_container_lowest),
        scrim = colorResource(R.color.black),
    )

    MaterialTheme(
        colorScheme = darkColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun ImpulseThemePreview() {
    ImpulseTheme {
        Surface {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
