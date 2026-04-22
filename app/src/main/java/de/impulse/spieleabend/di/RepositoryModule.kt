package de.impulse.spieleabend.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.impulse.spieleabend.data.GameRepositoryImpl
import de.impulse.spieleabend.domain.repository.GameRepository
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
}
