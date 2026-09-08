package de.kaserik.impulse.frontend.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import de.kaserik.impulse.R

// Values live in res/values/colors.xml and are resolved for the current configuration.

internal val CardBackground: Color
    @Composable get() = colorResource(R.color.card_background)

internal val CardTextColor: Color
    @Composable get() = colorResource(R.color.card_text_color)

internal val TableBackground: Color
    @Composable get() = colorResource(R.color.table_background)

internal val TableBackgroundHighlight: Color
    @Composable get() = colorResource(R.color.table_background_highlight)

internal val TitleColor: Color
    @Composable get() = colorResource(R.color.title_color)

internal val FallbackTextPanelColor: Color
    @Composable get() = colorResource(R.color.fallback_text_panel_color)

internal val PreviousCardTabColor: Color
    @Composable get() = colorResource(R.color.previous_card_tab_color)

internal val CategoryTabContentColor: Color
    @Composable get() = colorResource(R.color.white)

internal val AssignedColorTabColor: Color
    @Composable get() = colorResource(R.color.assigned_color_tab_color)

internal val CardTextPanelColors: List<Color>
    @Composable get() = listOf(
        colorResource(R.color.card_text_panel_color_1),
        colorResource(R.color.card_text_panel_color_2),
        colorResource(R.color.card_text_panel_color_3),
        colorResource(R.color.card_text_panel_color_4),
    )

internal val CategoryTabColors: List<Color>
    @Composable get() = listOf(
        colorResource(R.color.category_tab_color_1),
        colorResource(R.color.category_tab_color_2),
        colorResource(R.color.category_tab_color_3),
        colorResource(R.color.category_tab_color_4),
        colorResource(R.color.category_tab_color_5),
    )

internal val SignColors: List<Color>
    @Composable get() = listOf(
        colorResource(R.color.sign_color_1),
        colorResource(R.color.sign_color_2),
        colorResource(R.color.sign_color_3),
        colorResource(R.color.sign_color_4),
        colorResource(R.color.sign_color_5),
        colorResource(R.color.sign_color_6),
        colorResource(R.color.sign_color_7),
        colorResource(R.color.sign_color_8),
        colorResource(R.color.sign_color_9),
        colorResource(R.color.sign_color_10),
    )

internal val GameTableBrush: Brush
    @Composable get() = Brush.verticalGradient(
        colors = listOf(TableBackgroundHighlight, TableBackground),
    )
