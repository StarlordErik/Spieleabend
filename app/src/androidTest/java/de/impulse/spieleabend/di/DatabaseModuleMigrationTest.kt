package de.impulse.spieleabend.di

import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseModuleMigrationTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @After
    fun tearDown() {
        context.deleteDatabase(DATABASE_NAME)
        context.getDatabasePath("$DATABASE_NAME-shm").delete()
        context.getDatabasePath("$DATABASE_NAME-wal").delete()
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
    }
}
