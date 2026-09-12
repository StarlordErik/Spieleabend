package de.kaserik.impulse.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.kaserik.impulse.common.AppMessages
import de.kaserik.impulse.common.PreferenceKeys.DEVELOPER_MODE_KEY
import de.kaserik.impulse.common.PreferenceKeys.FUN_FACTS_MODE_KEY
import de.kaserik.impulse.common.PreferenceKeys.FUN_FACTS_SESSION_KEY
import de.kaserik.impulse.common.PreferenceKeys.LANGUAGE_KEY
import de.kaserik.impulse.common.PreferenceKeys.LAST_DRAW_CATEGORY_PREFIX
import de.kaserik.impulse.common.PreferenceKeys.PREFERENCES_NAME
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.repository.AppSettingsRepository
import java.util.Locale
import java.util.Locale.ROOT
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class SharedPreferencesAppSettingsRepository @Inject constructor(
    @ApplicationContext context: Context,
) : AppSettingsRepository {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override val developerMode: Flow<Boolean> =
        booleanPreferenceFlow(DEVELOPER_MODE_KEY, defaultValue = false)

    override val funFactsModeEnabled: Flow<Boolean> =
        booleanPreferenceFlow(FUN_FACTS_MODE_KEY, defaultValue = true)

    override val language: Flow<Sprache> =
        callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
                if (changedKey == LANGUAGE_KEY) trySend(readLanguage())
            }
            trySend(readLanguage())
            preferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
        }.distinctUntilChanged()

    override suspend fun setDeveloperMode(enabled: Boolean) {
        preferences.edit { putBoolean(DEVELOPER_MODE_KEY, enabled) }
    }

    override suspend fun setFunFactsModeEnabled(enabled: Boolean) {
        preferences.edit { putBoolean(FUN_FACTS_MODE_KEY, enabled) }
    }

    override suspend fun setLanguage(language: Sprache) {
        require(language.auswaehlbar) { AppMessages.unsupportedLanguage(language) }
        preferences.edit { putString(LANGUAGE_KEY, language.name) }
    }

    override fun getFunFactsSession(): String? =
        preferences.getString(FUN_FACTS_SESSION_KEY, null)

    override fun setFunFactsSession(serializedSession: String) {
        preferences.edit { putString(FUN_FACTS_SESSION_KEY, serializedSession) }
    }

    override fun getLastDrawCategoryId(gameId: Int): Int? {
        val key = LAST_DRAW_CATEGORY_PREFIX + gameId
        return if (preferences.contains(key)) preferences.getInt(key, 0) else null
    }

    override fun setLastDrawCategoryId(gameId: Int, categoryId: Int?) {
        preferences.edit {
            val key = LAST_DRAW_CATEGORY_PREFIX + gameId
            if (categoryId == null) remove(key) else putInt(key, categoryId)
        }
    }

    private fun booleanPreferenceFlow(
        key: String,
        defaultValue: Boolean,
    ): Flow<Boolean> =
        callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, changedKey ->
                if (changedKey == key) trySend(sharedPreferences.getBoolean(key, defaultValue))
            }
            trySend(preferences.getBoolean(key, defaultValue))
            preferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
        }.distinctUntilChanged()

    private fun readLanguage(): Sprache {
        val storedLanguage = preferences.getString(LANGUAGE_KEY, null)
            ?.let { value -> Sprache.entries.firstOrNull { language -> language.name == value } }
        return when {
            storedLanguage == Sprache.ERIK -> Sprache.DE
            storedLanguage?.auswaehlbar == true -> storedLanguage
            else -> Sprache.AuswaehlbareSprachen.firstOrNull { language ->
                language.name == Locale.getDefault().language.uppercase(ROOT)
            } ?: Sprache.DE
        }
    }

}
