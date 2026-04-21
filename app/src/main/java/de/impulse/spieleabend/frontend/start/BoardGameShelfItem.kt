@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.start

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class BoardGameShelfItem(
    val id: String,
    val title: String,
    val category: String,
    val accentColor: Color,
)

val placeholderBoardGames = listOf(
    BoardGameShelfItem(
        id = "placeholder",
        title = "Platzhalter",
        category = "Brettspiel",
        accentColor = Color(0xFF3F6F5D),
    ),
)
