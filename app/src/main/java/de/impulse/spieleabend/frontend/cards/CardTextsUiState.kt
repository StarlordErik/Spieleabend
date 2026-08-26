package de.impulse.spieleabend.frontend.cards

import androidx.compose.runtime.Immutable
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.domain.model.Spiel

@Immutable
sealed interface CardTextsUiState {
    @Immutable
    data object Loading : CardTextsUiState

    @Immutable
    data class Loaded(
        val gameName: String,
        val categories: List<CardTableCategory>,
    ) : CardTextsUiState
}

@Immutable
data class CardTableCategory(
    val id: Int,
    val name: String,
    val rows: List<CardTableRow>,
    val sortColumn: CardTableSortColumn = CardTableSortColumn.Text,
    val ascending: Boolean = true,
)

@Immutable
data class CardTableRow(
    val id: Int,
    val text: String,
    val seen: Boolean,
    val played: Boolean,
)

enum class CardTableSortColumn {
    Text,
    Seen,
    Played,
}

internal fun Spiel.toCardTextsUiState(sprache: Sprache): CardTextsUiState.Loaded =
    CardTextsUiState.Loaded(
        gameName = text(sprache),
        categories = (originaleKategorien + hinzugefuegteKategorien).map { category ->
            CardTableCategory(
                id = category.id(),
                name = category.text(sprache),
                rows = (category.originaleKartentexte + category.hinzugefuegteKartentexte).map { cardText ->
                    CardTableRow(
                        id = cardText.id(),
                        text = cardText.text(sprache),
                        seen = cardText.gesehen,
                        played = cardText.gespielt,
                    )
                },
            ).sorted(CardTableSortColumn.Text, ascending = true)
        },
    )

internal fun CardTableCategory.toggleSort(column: CardTableSortColumn): CardTableCategory {
    val nextAscending = if (sortColumn == column) !ascending else true
    return sorted(column, nextAscending)
}

private fun CardTableCategory.sorted(
    column: CardTableSortColumn,
    ascending: Boolean,
): CardTableCategory {
    val comparator = when (column) {
        CardTableSortColumn.Text -> compareBy<CardTableRow> { row -> row.text.lowercase() }
        CardTableSortColumn.Seen -> compareBy<CardTableRow> { row -> row.seen }
        CardTableSortColumn.Played -> compareBy<CardTableRow> { row -> row.played }
    }.thenBy { row -> row.text.lowercase() }
    val sortedRows = rows.sortedWith(if (ascending) comparator else comparator.reversed())

    return copy(
        rows = sortedRows,
        sortColumn = column,
        ascending = ascending,
    )
}
