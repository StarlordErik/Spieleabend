@file:Suppress("TooManyFunctions")

package de.impulse.spieleabend.frontend.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@Composable
fun CardTextsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardTextsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CardTextsContent(
        uiState = uiState,
        onBack = onBack,
        onSort = viewModel::sort,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun CardTextsScreenPreview() {
    SpieleabendTheme {
        CardTextsContent(uiState = PreviewCardTextsState, onBack = {}, onSort = { _, _ -> })
    }
}

@Composable
private fun CardTextsContent(
    uiState: CardTextsUiState,
    onBack: () -> Unit,
    onSort: (Int, CardTableSortColumn) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            CardTextsUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is CardTextsUiState.Loaded -> Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onBack) { Text("‹ Zurück") }
                    Text(
                        text = uiState.gameName,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    items(uiState.categories, key = { category -> category.id }) { category ->
                        Column {
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            CardTableHeader(category = category, onSort = { column -> onSort(category.id, column) })
                            category.rows.forEach { row ->
                                CardTableDataRow(row)
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardTextsContentPreview() {
    SpieleabendTheme {
        CardTextsContent(uiState = PreviewCardTextsState, onBack = {}, onSort = { _, _ -> })
    }
}

@Composable
private fun CardTableHeader(
    category: CardTableCategory,
    onSort: (CardTableSortColumn) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CardTableHeaderCell(
            "Text",
            CardTableSortColumn.Text,
            category,
            Modifier.weight(TEXT_COLUMN_WEIGHT),
            onSort,
        )
        CardTableHeaderCell(
            "Gesehen",
            CardTableSortColumn.Seen,
            category,
            Modifier.weight(STATUS_COLUMN_WEIGHT),
            onSort,
        )
        CardTableHeaderCell(
            "Gespielt",
            CardTableSortColumn.Played,
            category,
            Modifier.weight(STATUS_COLUMN_WEIGHT),
            onSort,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardTableHeaderPreview() {
    SpieleabendTheme { CardTableHeader(PreviewCardTextsState.categories.first(), {}) }
}

@Composable
private fun CardTableHeaderCell(
    label: String,
    column: CardTableSortColumn,
    category: CardTableCategory,
    modifier: Modifier,
    onSort: (CardTableSortColumn) -> Unit,
) {
    val arrow = if (category.sortColumn == column) if (category.ascending) " ↑" else " ↓" else ""
    Text(
        text = label + arrow,
        modifier = modifier
            .clickable { onSort(column) }
            .padding(8.dp),
        fontWeight = FontWeight.Bold,
    )
}

@Preview(showBackground = true)
@Composable
private fun CardTableHeaderCellPreview() {
    SpieleabendTheme {
        CardTableHeaderCell(
            label = "Text",
            column = CardTableSortColumn.Text,
            category = PreviewCardTextsState.categories.first(),
            modifier = Modifier.fillMaxWidth(),
            onSort = {},
        )
    }
}

@Composable
private fun CardTableDataRow(row: CardTableRow) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(row.text, modifier = Modifier.weight(TEXT_COLUMN_WEIGHT).padding(8.dp))
        StatusMark(row.seen, "Gesehen", Modifier.weight(STATUS_COLUMN_WEIGHT))
        StatusMark(row.played, "Gespielt", Modifier.weight(STATUS_COLUMN_WEIGHT))
    }
}

@Preview(showBackground = true)
@Composable
private fun CardTableDataRowPreview() {
    SpieleabendTheme { CardTableDataRow(PreviewCardTextsState.categories.first().rows.first()) }
}

@Composable
private fun StatusMark(
    value: Boolean,
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = if (value) "✓" else "✕",
        modifier = modifier
            .semantics { contentDescription = "$label: ${if (value) "ja" else "nein"}" }
            .padding(8.dp),
        color = if (value) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        style = MaterialTheme.typography.titleMedium,
    )
}

@Preview(showBackground = true)
@Composable
private fun StatusMarkPreview() {
    SpieleabendTheme { StatusMark(value = true, label = "Gesehen") }
}

private val PreviewCardTextsState = CardTextsUiState.Loaded(
    gameName = "Kneipenquiz",
    categories = listOf(
        CardTableCategory(
            id = 1,
            name = "Wissen",
            rows = listOf(
                CardTableRow(1, "Welche Stadt heißt Big Apple?", seen = true, played = false),
                CardTableRow(2, "Wie viele Planeten gibt es?", seen = true, played = true),
            ),
        ),
    ),
)

private const val TEXT_COLUMN_WEIGHT = 3f
private const val STATUS_COLUMN_WEIGHT = 1f
