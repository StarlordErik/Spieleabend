package de.impulse.spieleabend.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.impulse.spieleabend.data.entity.GezogeneKarteEntity
import de.impulse.spieleabend.data.entity.GezogenerKartentextEntity

@Dao
interface KartenverlaufDao {
    @Insert
    suspend fun insertKarte(karte: GezogeneKarteEntity): Long

    @Insert
    suspend fun insertKartentexte(kartentexte: List<GezogenerKartentextEntity>)

    @Query(
        """
        SELECT *
        FROM gezogene_karte
        WHERE spiel_id = :spielId
        ORDER BY id DESC
        LIMIT :limit
        """,
    )
    suspend fun neuesteKarten(
        spielId: Int,
        limit: Int,
    ): List<GezogeneKarteEntity>

    @Query(
        """
        SELECT *
        FROM gezogener_kartentext
        WHERE karte_id = :karteId
        ORDER BY position
        """,
    )
    suspend fun kartentexte(karteId: Long): List<GezogenerKartentextEntity>

    @Query("DELETE FROM gezogene_karte WHERE id = :karteId")
    suspend fun deleteKarte(karteId: Long)

    @Query(
        """
        SELECT id
        FROM gezogene_karte
        WHERE spiel_id = :spielId
        ORDER BY id DESC
        LIMIT -1 OFFSET :behalten
        """,
    )
    suspend fun aeltereKartenIds(
        spielId: Int,
        behalten: Int,
    ): List<Long>

    @Query("DELETE FROM gezogene_karte WHERE id IN (:kartenIds)")
    suspend fun deleteKarten(kartenIds: List<Long>)
}
