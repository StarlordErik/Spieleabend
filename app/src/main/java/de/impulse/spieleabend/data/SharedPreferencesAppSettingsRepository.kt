package de.impulse.spieleabend.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.impulse.spieleabend.domain.repository.AppSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class SharedPreferencesAppSettingsRepository @Inject constructor(
    @ApplicationContext context: Context,
) : AppSettingsRepository {
    private val preferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)

    override val developerMode: Flow<Boolean> =
        callbackFlow {
            val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                    if (key == DeveloperModeKey) {
                        trySend(sharedPreferences.getBoolean(DeveloperModeKey, false))
                    }
                }

            trySend(preferences.getBoolean(DeveloperModeKey, false))
            preferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose {
                preferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }.distinctUntilChanged()

    override val funFactsModeEnabled: Flow<Boolean> =
        booleanPreferenceFlow(FunFactsModeKey, defaultValue = true)

    override suspend fun setDeveloperMode(enabled: Boolean) {
        preferences.edit { putBoolean(DeveloperModeKey, enabled) }
    }

    override suspend fun setFunFactsModeEnabled(enabled: Boolean) {
        preferences.edit { putBoolean(FunFactsModeKey, enabled) }
    }

    override fun getFunFactsSession(): String? =
        preferences.getString(FunFactsSessionKey, null)

    override fun setFunFactsSession(serializedSession: String) {
        preferences.edit { putString(FunFactsSessionKey, serializedSession) }
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

    private companion object {
        const val PreferencesName = "app_settings"
        const val DeveloperModeKey = "developer_mode"
        const val FunFactsModeKey = "fun_facts_mode_enabled"
        const val FunFactsSessionKey = "fun_facts_session"
    }
}
