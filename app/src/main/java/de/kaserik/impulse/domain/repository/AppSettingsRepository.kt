package de.kaserik.impulse.domain.repository

import de.kaserik.impulse.common.Sprache
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val developerMode: Flow<Boolean>
    val funFactsModeEnabled: Flow<Boolean>
    val privacyModeEnabled: Flow<Boolean>
    val language: Flow<Sprache>

    suspend fun setDeveloperMode(enabled: Boolean)

    suspend fun setFunFactsModeEnabled(enabled: Boolean)

    suspend fun setPrivacyModeEnabled(enabled: Boolean)

    suspend fun setLanguage(language: Sprache)

    fun getFunFactsSession(): String?

    fun setFunFactsSession(serializedSession: String)

    fun getPrivacySession(): String?

    fun setPrivacySession(serializedSession: String)

    fun getLastDrawCategoryId(gameId: Int): Int?

    fun setLastDrawCategoryId(gameId: Int, categoryId: Int?)
}
