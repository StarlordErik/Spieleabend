package de.kaserik.impulse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.kaserik.impulse.data.entity.SpielZiehEinstellungEntity

@Dao
interface SpielZiehEinstellungDao {
    @Query("SELECT * FROM spiel_zieh_einstellung WHERE spiel_id = :spielId LIMIT 1")
    suspend fun einstellung(spielId: Int): SpielZiehEinstellungEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(einstellung: SpielZiehEinstellungEntity)

    @Query("DELETE FROM spiel_zieh_einstellung WHERE spiel_id = :spielId")
    suspend fun delete(spielId: Int)
}
