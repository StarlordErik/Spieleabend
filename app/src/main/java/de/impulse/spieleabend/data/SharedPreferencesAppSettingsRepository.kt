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

    override suspend fun setDeveloperMode(enabled: Boolean) {
        preferences.edit { putBoolean(DeveloperModeKey, enabled) }
    }

    private companion object {
        const val PreferencesName = "app_settings"
        const val DeveloperModeKey = "developer_mode"
    }
}
