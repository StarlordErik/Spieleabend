package de.kaserik.impulse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.kaserik.impulse.data.entity.LokalisierungEntity
import de.kaserik.impulse.data.entity.TranslationEntity

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
        sprache: de.kaserik.impulse.common.Sprache,
    )

    @Query(CardTextTranslationQueries.ERIK_TRANSLATIONS)
    suspend fun erikCardTextTranslations(
        spielId: Int?,
        ueberschreiben: Boolean,
    ): List<TranslationEntity>

    @Query(CardTextTranslationQueries.RESET_CUSTOM_TRANSLATIONS)
    suspend fun resetCustomCardTextTranslations(spielId: Int?)

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
