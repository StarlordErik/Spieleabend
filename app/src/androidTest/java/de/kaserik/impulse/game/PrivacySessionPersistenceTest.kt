package de.kaserik.impulse.game

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.kaserik.impulse.common.PreferenceKeys
import de.kaserik.impulse.data.SharedPreferencesAppSettingsRepository
import de.kaserik.impulse.frontend.game.PrivacyPhase
import de.kaserik.impulse.frontend.game.PrivacySession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PrivacySessionPersistenceTest {
    private val context =
        object : ContextWrapper(InstrumentationRegistry.getInstrumentation().targetContext) {
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
                super.getSharedPreferences("${name}_privacy_test", mode)
        }

    @Before
    fun setUp() = clearPreferences()

    @After
    fun tearDown() = clearPreferences()

    @Test
    fun privacyModePreferenceIsIndependentOfFunFacts() = runBlocking {
        val settings = SharedPreferencesAppSettingsRepository(context)
        assertTrue(settings.privacyModeEnabled.first())
        settings.setPrivacyModeEnabled(false)
        val recreated = SharedPreferencesAppSettingsRepository(context)
        assertFalse(recreated.privacyModeEnabled.first())
        assertTrue(recreated.funFactsModeEnabled.first())
    }

    @Test
    fun restoredRevealAwardsPointsOnceAndPrefillsTheNextRound() {
        val settings = SharedPreferencesAppSettingsRepository(context)
        val session = PrivacySession().apply {
            selectQuestion(338, 1)
            draft.updateName("Alex")
            draft.chooseVote(true)
            nextPlayer()
            draft.updateName("Sam")
            draft.chooseVote(false)
            reveal()
        }
        settings.setPrivacySession(session.serialize())
        val recreated = SharedPreferencesAppSettingsRepository(context)
        val restored = PrivacySession.restore(recreated.getPrivacySession())
        assertEquals(PrivacyPhase.Revealing, restored.phase)
        restored.completeReveal()
        recreated.setPrivacySession(restored.serialize())
        val completed = PrivacySession.restore(settings.getPrivacySession())
        completed.completeReveal()
        assertTrue(completed.ranking.all { it.player.points == 3 })
        completed.startNextRound()
        settings.setPrivacySession(completed.serialize())
        assertEquals("Sam", PrivacySession.restore(recreated.getPrivacySession()).draftName)
    }

    private fun clearPreferences() {
        context.getSharedPreferences(PreferenceKeys.PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit { clear() }
    }
}
