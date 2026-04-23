package de.impulse.spieleabend.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.impulse.spieleabend.data.SpieleabendDatabase
import de.impulse.spieleabend.data.dao.KartentextDao
import de.impulse.spieleabend.data.dao.KategorieDao
import de.impulse.spieleabend.data.dao.LokalisierungDao
import de.impulse.spieleabend.data.dao.SpielDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideSpieleabendDatabase(
        @ApplicationContext context: Context,
    ): SpieleabendDatabase =
        Room.databaseBuilder(
            context,
            SpieleabendDatabase::class.java,
            DATABASE_NAME,
        )
            .createFromAsset(DATABASE_ASSET_PATH)
            // `fallbackToDestructiveMigration()` already covers downgrades.
            // Chaining `fallbackToDestructiveMigrationOnDowngrade()` afterwards would
            // re-enable required migrations for upgrades and break 1 -> 2 installs.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideSpielDao(database: SpieleabendDatabase): SpielDao =
        database.spielDao()

    @Provides
    fun provideKategorieDao(database: SpieleabendDatabase): KategorieDao =
        database.kategorieDao()

    @Provides
    fun provideKartentextDao(database: SpieleabendDatabase): KartentextDao =
        database.kartentextDao()

    @Provides
    fun provideLokalisierungDao(database: SpieleabendDatabase): LokalisierungDao =
        database.lokalisierungDao()

    private const val DATABASE_NAME = "spieleabend.db"
    private const val DATABASE_ASSET_PATH = "spieleabend.db"
}
