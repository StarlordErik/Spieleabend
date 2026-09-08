package de.kaserik.impulse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kaserik.impulse.data.GameRepositoryImpl
import de.kaserik.impulse.data.SharedPreferencesAppSettingsRepository
import de.kaserik.impulse.domain.repository.AppSettingsRepository
import de.kaserik.impulse.domain.repository.GameRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused", "kotlin:S6517")
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindGameRepository(
        implementation: GameRepositoryImpl,
    ): GameRepository

    @Binds
    @Singleton
    fun bindAppSettingsRepository(
        implementation: SharedPreferencesAppSettingsRepository,
    ): AppSettingsRepository
}
