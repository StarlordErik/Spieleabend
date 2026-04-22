package de.impulse.spieleabend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import de.impulse.spieleabend.data.local.dao.KartentextDao
import de.impulse.spieleabend.data.local.dao.KategorieDao
import de.impulse.spieleabend.data.local.dao.LokalisierungDao
import de.impulse.spieleabend.data.local.dao.SpielDao
import de.impulse.spieleabend.data.local.entity.KartentextEntity
import de.impulse.spieleabend.data.local.entity.KategorieEntity
import de.impulse.spieleabend.data.local.entity.KategorieXKartentextEntity
import de.impulse.spieleabend.data.local.entity.LokalisierungEntity
import de.impulse.spieleabend.data.local.entity.SpielEntity
import de.impulse.spieleabend.data.local.entity.SpielXKategorieEntity
import de.impulse.spieleabend.data.local.entity.TranslationEntity

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
