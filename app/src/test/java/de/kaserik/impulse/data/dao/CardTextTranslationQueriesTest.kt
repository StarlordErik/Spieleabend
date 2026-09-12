package de.kaserik.impulse.data.dao

import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.model.Lokalisierung
import de.kaserik.impulse.domain.model.Translation
import java.sql.Connection
import java.sql.DriverManager
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CardTextTranslationQueriesTest {
    private lateinit var database: Connection

    @Before
    fun setUp() {
        database = DriverManager.getConnection("jdbc:sqlite::memory:")
        execute("CREATE TABLE translation (lokalisierung_id INTEGER, sprache TEXT, text TEXT, bearbeitet INTEGER, " +
            "PRIMARY KEY (lokalisierung_id, sprache))")
        execute("CREATE TABLE kartentext (lokalisierung_id INTEGER PRIMARY KEY, inaktiv INTEGER, " +
            "favorit INTEGER, gesehen INTEGER, gespielt INTEGER)")
        execute("CREATE TABLE kategorie_x_kartentext (kategorie_id INTEGER, kartentext_id INTEGER)")
        execute("CREATE TABLE spiel_x_kategorie (spiel_id INTEGER, kategorie_id INTEGER, inaktiv INTEGER)")
        execute("INSERT INTO spiel_x_kategorie VALUES (1, 10, 0), (1, 11, 1), (2, 20, 0)")
        execute("INSERT INTO kategorie_x_kartentext VALUES " +
            "(10, 101), (10, 102), (10, 103), (11, 101), (11, 104), (20, 201), (20, 202)")
        listOf(101, 102, 103, 104, 201, 202, 301).forEach { id ->
            execute("INSERT INTO kartentext VALUES ($id, 1, 1, 1, 1)")
        }
        listOf(1, 10, 101, 102, 103, 104, 201, 202, 301).forEach { id ->
            insertTranslation(id, Sprache.OG, "Original $id")
            insertTranslation(id, Sprache.DE, "Deutsch $id")
            if (id != 103) insertTranslation(id, Sprache.ERIK, "Erik $id")
        }
        listOf(1, 10, 102, 103, 201).forEach { id ->
            insertTranslation(id, Sprache.EIGENE_DE, "Eigener Text $id")
        }
        insertTranslation(102, Sprache.EIGENE_EN, "My text")
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun missingOnlyProtectsExistingEditsAndIncludesInactiveCardsWithoutDuplicates() {
        val importedIds = applyErikTranslations(gameId = 1, overwrite = false)

        assertEquals(listOf(101, 104), importedIds.sorted())
        assertEquals("Eigener Text 102", translation(102, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 103", translation(103, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 201", translation(201, Sprache.EIGENE_DE))
        assertEquals("My text", translation(102, Sprache.EIGENE_EN))
        assertEquals(emptyList<Int>(), applyErikTranslations(gameId = 1, overwrite = false))
    }

    @Test
    fun overwriteReplacesOnlyGermanEditsWithAnErikSourceInTheSelectedGame() {
        assertEquals(listOf(101, 102, 104), applyErikTranslations(gameId = 1, overwrite = true).sorted())

        assertEquals("Erik 102", translation(102, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 103", translation(103, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 201", translation(201, Sprache.EIGENE_DE))
        assertEquals("My text", translation(102, Sprache.EIGENE_EN))
        assertEquals("Deutsch 102", translation(102, Sprache.DE))
    }

    @Test
    fun globalMissingOnlyIncludesAllGamesAndUnlinkedCardTexts() {
        assertEquals(listOf(101, 104, 202, 301), applyErikTranslations(gameId = null, overwrite = false).sorted())
        assertEquals("Eigener Text 201", translation(201, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 1", translation(1, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 10", translation(10, Sprache.EIGENE_DE))
    }

    @Test
    fun globalOverwriteIncludesAllGamesWithoutTouchingSourcesOrCardStates() {
        val sources = sourceRows()
        assertEquals(listOf(101, 102, 104, 201, 202, 301),
            applyErikTranslations(gameId = null, overwrite = true).sorted())

        assertEquals("Erik 201", translation(201, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 103", translation(103, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 1", translation(1, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 10", translation(10, Sprache.EIGENE_DE))
        assertEquals(sources, sourceRows())
        assertCardStatesUnchanged()
    }

    @Test
    fun importedTextsAreMarkedAsEditedAndDisplayedAsOwnGermanTranslations() {
        applyErikTranslations(gameId = 1, overwrite = false)
        val localized = localization(101).lokalisierterText(Sprache.DE)

        assertEquals("Erik 101", localized.text)
        assertTrue(localized.eigeneLokalisierung)
        assertFalse(localized.uebersetzungFehlt)
        assertTrue(localization(101).translationen.single { it.sprache == Sprache.EIGENE_DE }.bearbeitet)
    }

    @Test
    fun resetGameRemovesBothOwnLanguagesAndRestoresGermanWithoutChangingOtherGames() {
        applyErikTranslations(gameId = null, overwrite = true)
        val sources = sourceRows()
        resetCustomTranslations(gameId = 1)

        assertEquals(emptyList<Translation>(), localization(102).translationen.filter {
            it.sprache == Sprache.EIGENE_DE || it.sprache == Sprache.EIGENE_EN
        })
        assertEquals("Deutsch 101", localization(101).text(Sprache.DE))
        assertFalse(localization(101).lokalisierterText(Sprache.DE).eigeneLokalisierung)
        assertEquals("Erik 201", translation(201, Sprache.EIGENE_DE))
        assertEquals("Erik 301", translation(301, Sprache.EIGENE_DE))
        assertEquals(sources, sourceRows())
        assertCardStatesUnchanged()
    }

    @Test
    fun globalResetRemovesAllCardEditsButKeepsSourcesAndNames() {
        applyErikTranslations(gameId = null, overwrite = true)
        val sources = sourceRows()
        resetCustomTranslations(gameId = null)
        resetCustomTranslations(gameId = null)

        assertEquals(listOf("1", "10"), rows("SELECT lokalisierung_id FROM translation " +
            "WHERE sprache IN ('EIGENE_DE', 'EIGENE_EN') ORDER BY lokalisierung_id"))
        assertEquals("Eigener Text 1", translation(1, Sprache.EIGENE_DE))
        assertEquals("Eigener Text 10", translation(10, Sprache.EIGENE_DE))
        assertEquals(sources, sourceRows())
        assertCardStatesUnchanged()
    }

    private fun applyErikTranslations(gameId: Int?, overwrite: Boolean): List<Int> {
        val query = scopedQuery(CardTextTranslationQueries.ERIK_TRANSLATIONS, gameId)
            .replace(":ueberschreiben", if (overwrite) "1" else "0")
        val translations = database.createStatement().use { statement ->
            statement.executeQuery(query).use { result ->
                buildList {
                    while (result.next()) {
                        assertEquals("EIGENE_DE", result.getString("sprache"))
                        assertEquals(1, result.getInt("bearbeitet"))
                        add(result.getInt("lokalisierung_id") to result.getString("text"))
                    }
                }
            }
        }
        translations.forEach { (id, text) -> insertTranslation(id, Sprache.EIGENE_DE, text) }
        return translations.map { it.first }
    }

    private fun resetCustomTranslations(gameId: Int?) {
        execute(scopedQuery(CardTextTranslationQueries.RESET_CUSTOM_TRANSLATIONS, gameId))
    }

    private fun scopedQuery(query: String, gameId: Int?): String =
        query.replace(":spielId", gameId?.toString() ?: "NULL")

    private fun insertTranslation(id: Int, language: Sprache, text: String) {
        database.prepareStatement("INSERT OR REPLACE INTO translation VALUES (?, ?, ?, ?)").use { statement ->
            statement.setInt(1, id)
            statement.setString(2, language.name)
            statement.setString(3, text)
            statement.setInt(4, if (language == Sprache.EIGENE_DE || language == Sprache.EIGENE_EN) 1 else 0)
            statement.executeUpdate()
        }
    }

    private fun localization(id: Int): Lokalisierung = database.createStatement().use { statement ->
        statement.executeQuery("SELECT * FROM translation WHERE lokalisierung_id = $id").use { result ->
            val translations = buildSet {
                while (result.next()) {
                    add(Translation(Sprache.valueOf(result.getString("sprache")), result.getString("text"),
                        bearbeitet = result.getInt("bearbeitet") == 1))
                }
            }
            Lokalisierung(id = id, translationen = translations, ogSprache = Sprache.DE)
        }
    }

    private fun translation(id: Int, language: Sprache): String =
        localization(id).translationen.single { it.sprache == language }.text

    private fun sourceRows(): List<String> = rows("SELECT lokalisierung_id || ':' || sprache || ':' || text || ':' " +
        "|| bearbeitet FROM translation WHERE sprache NOT IN ('EIGENE_DE', 'EIGENE_EN') " +
        "ORDER BY lokalisierung_id, sprache")

    private fun assertCardStatesUnchanged() {
        assertEquals(listOf("7"), rows("SELECT COUNT(*) FROM kartentext " +
            "WHERE inaktiv = 1 AND favorit = 1 AND gesehen = 1 AND gespielt = 1"))
    }

    private fun rows(query: String): List<String> = database.createStatement().use { statement ->
        statement.executeQuery(query).use { result ->
            buildList { while (result.next()) add(result.getString(1)) }
        }
    }

    private fun execute(sql: String) {
        database.createStatement().use { it.execute(sql) }
    }
}
