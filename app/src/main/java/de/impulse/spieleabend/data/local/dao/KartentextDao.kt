package de.impulse.spieleabend.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.impulse.spieleabend.data.local.entity.KartentextEntity

@Dao
interface KartentextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(kartentext: KartentextEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(kartentexte: List<KartentextEntity>)

    @Query("SELECT * FROM kartentext WHERE id = :kartentextId LIMIT 1")
    suspend fun kartentext(kartentextId: String): KartentextEntity?
}
