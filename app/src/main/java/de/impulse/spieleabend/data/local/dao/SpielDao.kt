package de.impulse.spieleabend.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.impulse.spieleabend.data.local.entity.KategorieEntity
import de.impulse.spieleabend.data.local.entity.SpielEntity
import de.impulse.spieleabend.data.local.entity.SpielXKategorieEntity

@Dao
interface SpielDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(spiel: SpielEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(spiele: List<SpielEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSpielXKategorien(spielXKategorien: List<SpielXKategorieEntity>)

    @Query("SELECT * FROM spiel WHERE id = :spielId LIMIT 1")
    suspend fun spiel(spielId: String): SpielEntity?

    @Query("SELECT * FROM spiel ORDER BY id LIMIT 1")
    suspend fun erstesSpiel(): SpielEntity?

    @Query(
        """
        SELECT kategorie.*
        FROM kategorie
        INNER JOIN spiel_x_kategorie
            ON spiel_x_kategorie.kategorie_id = kategorie.id
        WHERE spiel_x_kategorie.spiel_id = :spielId
        ORDER BY spiel_x_kategorie.position
        """,
    )
    suspend fun kategorienFuerSpiel(spielId: String): List<KategorieEntity>
}
