package de.impulse.spieleabend.data

import androidx.room.Database
import androidx.room.RoomDatabase
import de.impulse.spieleabend.data.dao.KartentextDao
import de.impulse.spieleabend.data.dao.KategorieDao
import de.impulse.spieleabend.data.dao.LokalisierungDao
import de.impulse.spieleabend.data.dao.SpielDao
import de.impulse.spieleabend.data.entity.KartentextEntity
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.KategorieXKartentextEntity
import de.impulse.spieleabend.data.entity.LokalisierungEntity
import de.impulse.spieleabend.data.entity.SpielEntity
import de.impulse.spieleabend.data.entity.SpielXKategorieEntity
import de.impulse.spieleabend.data.entity.TranslationEntity

@Database(
    entities = [
        SpielEntity::class,
        KategorieEntity::class,
        KartentextEntity::class,
        LokalisierungEntity::class,
        TranslationEntity::class,
        SpielXKategorieEntity::class,
        KategorieXKartentextEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class SpieleabendDatabase : RoomDatabase() {
    abstract fun spielDao(): SpielDao

    abstract fun kategorieDao(): KategorieDao

    abstract fun kartentextDao(): KartentextDao

    abstract fun lokalisierungDao(): LokalisierungDao
}