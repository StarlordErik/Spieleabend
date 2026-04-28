package de.impulse.spieleabend.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.impulse.spieleabend.data.entity.KartentextEntity

@Dao
interface KartentextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(kartentext: KartentextEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(kartentexte: List<KartentextEntity>)

    @Query("SELECT * FROM kartentext WHERE lokalisierung_id = :kartentextId LIMIT 1")
    suspend fun kartentext(kartentextId: Int): KartentextEntity?

    @Query(
        """
        UPDATE kartentext
        SET gesehen = :gesehen
        WHERE lokalisierung_id IN (:kartentextIds)
        """,
    )
    suspend fun updateGesehenForKartentexte(
        kartentextIds: List<Int>,
        gesehen: Boolean,
    )

    @Query(
        """
        UPDATE kartentext
        SET gesehen = :gesehen
        WHERE lokalisierung_id IN (
            SELECT kartentext_id
            FROM kategorie_x_kartentext
            WHERE kategorie_id IN (:kategorieIds)
        )
        """,
    )
    suspend fun updateGesehenForKategorien(
        kategorieIds: List<Int>,
        gesehen: Boolean,
    )
}
