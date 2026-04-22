package de.impulse.spieleabend.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.impulse.spieleabend.data.local.entity.LokalisierungEntity
import de.impulse.spieleabend.data.local.entity.TranslationEntity

@Dao
interface LokalisierungDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lokalisierung: LokalisierungEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(lokalisierungen: List<LokalisierungEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTranslationen(translationen: List<TranslationEntity>)

    @Query("SELECT * FROM lokalisierung WHERE id = :lokalisierungId LIMIT 1")
    suspend fun lokalisierung(lokalisierungId: String): LokalisierungEntity?

    @Query(
        """
        SELECT *
        FROM translation
        WHERE lokalisierung_id = :lokalisierungId
        ORDER BY sprach_code
        """,
    )
    suspend fun translationenFuerLokalisierung(
        lokalisierungId: String,
    ): List<TranslationEntity>
}
