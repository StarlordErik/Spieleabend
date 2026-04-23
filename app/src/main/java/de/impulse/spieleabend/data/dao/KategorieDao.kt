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

    @Query("SELECT * FROM kategorie WHERE lokalisierung_id = :kategorieId LIMIT 1")
    suspend fun kategorie(kategorieId: Int): KategorieEntity?

    @Query(
        """
        SELECT kartentext.*
        FROM kartentext
        INNER JOIN kategorie_x_kartentext
            ON kategorie_x_kartentext.kartentext_id = kartentext.lokalisierung_id
        WHERE kategorie_x_kartentext.kategorie_id = :kategorieId
        ORDER BY kartentext.lokalisierung_id
        """,
    )
    suspend fun kartentexteFuerKategorie(kategorieId: Int): List<KartentextEntity>

    @Query(
        """
        SELECT *
        FROM kategorie_x_kartentext
        WHERE kategorie_id = :kategorieId
        ORDER BY kartentext_id
        """,
    )
    suspend fun kategorieXKartentexteFuerKategorie(
        kategorieId: Int,
    ): List<KategorieXKartentextEntity>
}
