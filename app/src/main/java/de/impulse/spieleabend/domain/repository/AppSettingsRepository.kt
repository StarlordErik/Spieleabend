package de.impulse.spieleabend.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val developerMode: Flow<Boolean>
    val funFactsModeEnabled: Flow<Boolean>

    suspend fun setDeveloperMode(enabled: Boolean)

    suspend fun setFunFactsModeEnabled(enabled: Boolean)

    fun getFunFactsSession(): String?

    fun setFunFactsSession(serializedSession: String)
}
