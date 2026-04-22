package de.impulse.spieleabend.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.impulse.spieleabend.data.entity.KartentextEntity
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.KategorieXKartentextEntity

@Dao
interface KategorieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(kategorie: KategorieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(kategorien: List<KategorieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertKategorieXKartentexte(
        kategorieXKartentexte: List<KategorieXKartentextEntity>,
    )

    @Query("SELECT * FROM kategorie WHERE id = :kategorieId LIMIT 1")
    suspend fun kategorie(kategorieId: String): KategorieEntity?

    @Query(
        """
        SELECT kartentext.*
        FROM kartentext
        INNER JOIN kategorie_x_kartentext
            ON kategorie_x_kartentext.kartentext_id = kartentext.id
        WHERE kategorie_x_kartentext.kategorie_id = :kategorieId
        ORDER BY kategorie_x_kartentext.position
        """,
    )
    suspend fun kartentexteFuerKategorie(kategorieId: String): List<KartentextEntity>
}
