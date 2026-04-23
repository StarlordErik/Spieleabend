package de.impulse.spieleabend.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.SpielEntity
import de.impulse.spieleabend.data.entity.SpielXKategorieEntity

@Dao
interface SpielDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(spiel: SpielEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(spiele: List<SpielEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSpielXKategorien(spielXKategorien: List<SpielXKategorieEntity>)

    @Query("SELECT * FROM spiel WHERE lokalisierung_id = :spielId LIMIT 1")
    suspend fun spiel(spielId: Int): SpielEntity?

    @Query("SELECT * FROM spiel ORDER BY lokalisierung_id LIMIT 1")
    suspend fun erstesSpiel(): SpielEntity?

    @Query(
        """
        SELECT kategorie.*
        FROM kategorie
        INNER JOIN spiel_x_kategorie
            ON spiel_x_kategorie.kategorie_id = kategorie.lokalisierung_id
        WHERE spiel_x_kategorie.spiel_id = :spielId
        ORDER BY kategorie.lokalisierung_id
        """,
    )
    suspend fun kategorienFuerSpiel(spielId: Int): List<KategorieEntity>
}
