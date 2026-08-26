package de.impulse.spieleabend.frontend.cards

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardTextsUiStateTest {
    private val rows =
        listOf(
            CardTableRow(id = 1, text = "Zebra", seen = true, played = false),
            CardTableRow(id = 2, text = "Apfel", seen = false, played = true),
            CardTableRow(id = 3, text = "Birne", seen = false, played = false),
        )

    @Test
    fun changingColumnStartsAscendingAndSortsBooleansBeforeChecks() {
        val result = category().toggleSort(CardTableSortColumn.Seen)

        assertEquals(CardTableSortColumn.Seen, result.sortColumn)
        assertTrue(result.ascending)
        assertEquals(listOf(false, false, true), result.rows.map { row -> row.seen })
        assertEquals(listOf("Apfel", "Birne", "Zebra"), result.rows.map { row -> row.text })
    }

    @Test
    fun tappingSameHeaderTogglesDirection() {
        val ascending = category().toggleSort(CardTableSortColumn.Played)
        val descending = ascending.toggleSort(CardTableSortColumn.Played)

        assertFalse(descending.ascending)
        assertEquals(listOf(true, false, false), descending.rows.map { row -> row.played })
    }

    @Test
    fun sortingOneCategoryDoesNotRequireChangingAnotherCategory() {
        val first = category(id = 1).toggleSort(CardTableSortColumn.Seen)
        val second = category(id = 2)
        val categories = listOf(first, second)

        assertEquals(CardTableSortColumn.Seen, categories[0].sortColumn)
        assertEquals(CardTableSortColumn.Text, categories[1].sortColumn)
        assertEquals(rows, categories[1].rows)
    }

    private fun category(id: Int = 1): CardTableCategory =
        CardTableCategory(
            id = id,
            name = "Kategorie $id",
            rows = rows,
        )
}
