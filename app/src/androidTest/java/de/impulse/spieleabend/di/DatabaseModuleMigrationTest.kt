package de.impulse.spieleabend.di

import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.impulse.spieleabend.data.SpieleabendDatabase
import de.impulse.spieleabend.data.migration.Migration2To3
import de.impulse.spieleabend.data.migration.Migration3To4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseModuleMigrationTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val migrationHelper =
        MigrationTestHelper(
            instrumentation = InstrumentationRegistry.getInstrumentation(),
            databaseClass = SpieleabendDatabase::class.java,
        )

    @After
    fun tearDown() {
        listOf(DATABASE_NAME, MIGRATION_DATABASE_NAME).forEach { databaseName ->
            context.deleteDatabase(databaseName)
            context.getDatabasePath("$databaseName-shm").delete()
            context.getDatabasePath("$databaseName-wal").delete()
        }
    }

    @Test
    fun opensAndReplacesExistingVersion1Database() {
        createVersion1Database()

        val database = DatabaseModule.provideSpieleabendDatabase(context)
        try {
            runBlocking {
                assertEquals(5, database.spielDao().spiele().size)
            }
        } finally {
            database.close()
        }
    }

    @Test
    fun migration2To3PreservesCardStatesAndCreatesNewTables() {
        migrationHelper.createDatabase(MIGRATION_DATABASE_NAME, 2).apply {
            execSQL("INSERT INTO lokalisierung (id, og_sprache) VALUES (1, 'DE')")
            execSQL(
                """
                INSERT INTO kartentext (
                    lokalisierung_id, inaktiv, selbst_erstellt, favorit, gesehen, gespielt
                ) VALUES (1, 0, 0, 0, 1, 1)
                """.trimIndent(),
            )
            close()
        }

        migrationHelper.runMigrationsAndValidate(
            name = MIGRATION_DATABASE_NAME,
            version = 3,
            validateDroppedTables = true,
            Migration2To3,
        ).use { database ->
            database.query(
                "SELECT gesehen, gespielt FROM kartentext WHERE lokalisierung_id = 1",
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
                assertEquals(1, cursor.getInt(1))
            }

            val newTableCount = database.query(
                """
                SELECT COUNT(*)
                FROM sqlite_master
                WHERE type = 'table'
                  AND name IN ('gezogene_karte', 'gezogener_kartentext', 'spiel_einstellung')
                """.trimIndent(),
            ).use { cursor ->
                cursor.moveToFirst()
                cursor.getInt(0)
            }
            assertEquals(3, newTableCount)
        }
    }

    @Test
    fun migration3To4PreservesExistingSettingsAndAddsDrawModes() {
        migrationHelper.createDatabase(MIGRATION_DATABASE_NAME, 3).apply {
            execSQL("INSERT INTO lokalisierung (id, og_sprache) VALUES (1, 'DE')")
            execSQL("INSERT INTO lokalisierung (id, og_sprache) VALUES (2, 'DE')")
            execSQL(
                """
                INSERT INTO spiel (
                    lokalisierung_id, inaktiv, selbst_erstellt, favorit, bild_dateiname, texte_pro_karte
                ) VALUES (1, 0, 0, 0, NULL, 2)
                """.trimIndent(),
            )
            execSQL(
                "INSERT INTO spiel_einstellung (spiel_id, texte_pro_karte_override) VALUES (1, 4)",
            )
            execSQL(
                """
                INSERT INTO kartentext (
                    lokalisierung_id, inaktiv, selbst_erstellt, favorit, gesehen, gespielt
                ) VALUES (2, 1, 0, 1, 1, 0)
                """.trimIndent(),
            )
            execSQL(
                """
                INSERT INTO translation (lokalisierung_id, sprache, text, bearbeitet)
                VALUES (2, 'DE', 'Bearbeitet', 1)
                """.trimIndent(),
            )
            close()
        }

        migrationHelper.runMigrationsAndValidate(
            name = MIGRATION_DATABASE_NAME,
            version = 4,
            validateDroppedTables = true,
            Migration3To4,
        ).use { database ->
            database.query(
                "SELECT texte_pro_karte_override FROM spiel_einstellung WHERE spiel_id = 1",
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(4, cursor.getInt(0))
            }
            database.query(
                "SELECT inaktiv, favorit, gesehen, gespielt FROM kartentext WHERE lokalisierung_id = 2",
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
                assertEquals(1, cursor.getInt(1))
                assertEquals(1, cursor.getInt(2))
                assertEquals(0, cursor.getInt(3))
            }
            database.query(
                "SELECT text, bearbeitet FROM translation WHERE lokalisierung_id = 2 AND sprache = 'DE'",
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("Bearbeitet", cursor.getString(0))
                assertEquals(1, cursor.getInt(1))
            }

            database.execSQL("INSERT INTO spiel_zieh_einstellung (spiel_id) VALUES (1)")
            database.query(
                """
                SELECT geloeschte_modus, favoriten_modus, bearbeitete_modus
                FROM spiel_zieh_einstellung
                WHERE spiel_id = 1
                """.trimIndent(),
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("ALS_LETZTE", cursor.getString(0))
                assertEquals("UNBEACHTET", cursor.getString(1))
                assertEquals("UNBEACHTET", cursor.getString(2))
            }
        }
    }

    private fun createVersion1Database() {
        tearDown()

        val databaseFile = context.getDatabasePath(DATABASE_NAME)
        databaseFile.parentFile?.mkdirs()

        SQLiteDatabase.openOrCreateDatabase(databaseFile, null).use { database ->
            database.execSQL("CREATE TABLE legacy_placeholder (id INTEGER PRIMARY KEY)")
            database.version = 1
        }
    }

    private companion object {
        private const val DATABASE_NAME = "spieleabend.db"
        private const val MIGRATION_DATABASE_NAME = "migration-2-to-3.db"
    }
}
