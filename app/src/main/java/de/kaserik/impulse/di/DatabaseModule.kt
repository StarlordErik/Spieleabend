package de.kaserik.impulse.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.kaserik.impulse.common.DatabaseFiles
import de.kaserik.impulse.data.ImpulseDatabase
import de.kaserik.impulse.data.dao.KartentextDao
import de.kaserik.impulse.data.dao.KartenverlaufDao
import de.kaserik.impulse.data.dao.KategorieDao
import de.kaserik.impulse.data.dao.LokalisierungDao
import de.kaserik.impulse.data.dao.SpielDao
import de.kaserik.impulse.data.dao.SpielEinstellungDao
import de.kaserik.impulse.data.dao.SpielZiehEinstellungDao
import de.kaserik.impulse.data.migration.Migration2To3
import de.kaserik.impulse.data.migration.Migration3To4
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideImpulseDatabase(
        @ApplicationContext context: Context,
    ): ImpulseDatabase =
        Room.databaseBuilder(
            context,
            ImpulseDatabase::class.java,
            DATABASE_NAME,
        )
            .createFromAsset(DATABASE_ASSET_PATH)
            .addMigrations(Migration2To3, Migration3To4)
            // This fallback also covers downgrades. Adding the downgrade-specific
            // fallback would require migrations for upgrades again and prevent
            // opening a database from version 1.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideSpielDao(database: ImpulseDatabase): SpielDao =
        database.spielDao()

    @Provides
    fun provideKategorieDao(database: ImpulseDatabase): KategorieDao =
        database.kategorieDao()

    @Provides
    fun provideKartentextDao(database: ImpulseDatabase): KartentextDao =
        database.kartentextDao()

    @Provides
    fun provideLokalisierungDao(database: ImpulseDatabase): LokalisierungDao =
        database.lokalisierungDao()

    @Provides
    fun provideKartenverlaufDao(database: ImpulseDatabase): KartenverlaufDao =
        database.kartenverlaufDao()

    @Provides
    fun provideSpielEinstellungDao(database: ImpulseDatabase): SpielEinstellungDao =
        database.spielEinstellungDao()

    @Provides
    fun provideSpielZiehEinstellungDao(database: ImpulseDatabase): SpielZiehEinstellungDao =
        database.spielZiehEinstellungDao()

    private const val DATABASE_NAME = DatabaseFiles.NAME
    private const val DATABASE_ASSET_PATH = DatabaseFiles.NAME
}
