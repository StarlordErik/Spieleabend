package de.impulse.spieleabend.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.impulse.spieleabend.data.entity.LokalisierungEntity
import de.impulse.spieleabend.data.entity.TranslationEntity

@Dao
interface LokalisierungDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lokalisierung: LokalisierungEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(lokalisierungen: List<LokalisierungEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTranslationen(translationen: List<TranslationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTranslation(translation: TranslationEntity)

    @Query(
        """
        DELETE FROM translation
        WHERE lokalisierung_id = :lokalisierungId
          AND sprache = :sprache
        """,
    )
    suspend fun deleteTranslation(
        lokalisierungId: Int,
        sprache: de.impulse.spieleabend.common.Sprache,
    )

    @Query("SELECT * FROM lokalisierung WHERE id = :lokalisierungId LIMIT 1")
    suspend fun lokalisierung(lokalisierungId: Int): LokalisierungEntity?

    @Query(
        """
        SELECT *
        FROM translation
        WHERE lokalisierung_id = :lokalisierungId
        ORDER BY sprache
        """,
    )
    suspend fun translationenFuerLokalisierung(
        lokalisierungId: Int,
    ): List<TranslationEntity>
}
