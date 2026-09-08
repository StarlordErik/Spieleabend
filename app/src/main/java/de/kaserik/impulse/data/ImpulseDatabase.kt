package de.kaserik.impulse.data

import androidx.room.Database
import androidx.room.RoomDatabase
import de.kaserik.impulse.data.dao.KartentextDao
import de.kaserik.impulse.data.dao.KartenverlaufDao
import de.kaserik.impulse.data.dao.KategorieDao
import de.kaserik.impulse.data.dao.LokalisierungDao
import de.kaserik.impulse.data.dao.SpielDao
import de.kaserik.impulse.data.dao.SpielEinstellungDao
import de.kaserik.impulse.data.dao.SpielZiehEinstellungDao
import de.kaserik.impulse.data.entity.GezogeneKarteEntity
import de.kaserik.impulse.data.entity.GezogenerKartentextEntity
import de.kaserik.impulse.data.entity.KartentextEntity
import de.kaserik.impulse.data.entity.KategorieEntity
import de.kaserik.impulse.data.entity.KategorieXKartentextEntity
import de.kaserik.impulse.data.entity.LokalisierungEntity
import de.kaserik.impulse.data.entity.SpielEinstellungEntity
import de.kaserik.impulse.data.entity.SpielEntity
import de.kaserik.impulse.data.entity.SpielXKategorieEntity
import de.kaserik.impulse.data.entity.SpielZiehEinstellungEntity
import de.kaserik.impulse.data.entity.TranslationEntity

@Database(
    entities = [
        SpielEntity::class,
        KategorieEntity::class,
        KartentextEntity::class,
        LokalisierungEntity::class,
        TranslationEntity::class,
        SpielXKategorieEntity::class,
        KategorieXKartentextEntity::class,
        GezogeneKarteEntity::class,
        GezogenerKartentextEntity::class,
        SpielEinstellungEntity::class,
        SpielZiehEinstellungEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
abstract class ImpulseDatabase : RoomDatabase() {
    abstract fun spielDao(): SpielDao

    abstract fun kategorieDao(): KategorieDao

    abstract fun kartentextDao(): KartentextDao

    abstract fun lokalisierungDao(): LokalisierungDao

    abstract fun kartenverlaufDao(): KartenverlaufDao

    abstract fun spielEinstellungDao(): SpielEinstellungDao

    abstract fun spielZiehEinstellungDao(): SpielZiehEinstellungDao
}
