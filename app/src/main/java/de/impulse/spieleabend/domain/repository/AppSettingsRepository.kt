package de.impulse.spieleabend.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val developerMode: Flow<Boolean>

    suspend fun setDeveloperMode(enabled: Boolean)
}
