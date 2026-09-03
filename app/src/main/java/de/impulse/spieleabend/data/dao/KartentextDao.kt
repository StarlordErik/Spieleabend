package de.impulse.spieleabend.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.impulse.spieleabend.data.entity.KartentextEntity

@Dao
@Suppress("TooManyFunctions")
interface KartentextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(kartentext: KartentextEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(kartentexte: List<KartentextEntity>)

    @Query("SELECT * FROM kartentext WHERE lokalisierung_id = :kartentextId LIMIT 1")
    suspend fun kartentext(kartentextId: Int): KartentextEntity?

    @Query("UPDATE kartentext SET inaktiv = :geloescht WHERE lokalisierung_id = :kartentextId")
    suspend fun updateGeloescht(
        kartentextId: Int,
        geloescht: Boolean,
    ): Int

    @Query("UPDATE kartentext SET favorit = :favorit WHERE lokalisierung_id = :kartentextId")
    suspend fun updateFavorit(
        kartentextId: Int,
        favorit: Boolean,
    ): Int

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
        SET gesehen = 0
        WHERE lokalisierung_id IN (:kartentextIds)
          AND gespielt = 0
        """,
    )
    suspend fun resetGesehenForKartentexte(kartentextIds: List<Int>)

    @Query(
        """
        UPDATE kartentext
        SET gesehen = 0,
            gespielt = 0
        WHERE lokalisierung_id IN (:kartentextIds)
        """,
    )
    suspend fun resetGesehenUndGespieltForKartentexte(kartentextIds: List<Int>)

    @Query(
        """
        UPDATE kartentext
        SET gesehen = :gesehen
        WHERE lokalisierung_id IN (
            SELECT kartentext_id
            FROM kategorie_x_kartentext
            WHERE kategorie_id IN (:kategorieIds)
        )
          AND gespielt = 0
        """,
    )
    suspend fun updateGesehenForNichtGespielteKategorien(
        kategorieIds: List<Int>,
        gesehen: Boolean,
    )

    @Query(
        """
        UPDATE kartentext
        SET gespielt = :gespielt
        WHERE lokalisierung_id IN (:kartentextIds)
        """,
    )
    suspend fun updateGespieltForKartentexte(
        kartentextIds: List<Int>,
        gespielt: Boolean,
    )

    @Query(
        """
        UPDATE kartentext
        SET gesehen = :gesehen,
            gespielt = :gespielt
        WHERE lokalisierung_id IN (
            SELECT kartentext_id
            FROM kategorie_x_kartentext
            WHERE kategorie_id IN (:kategorieIds)
        )
        """,
    )
    suspend fun updateGesehenUndGespieltForKategorien(
        kategorieIds: List<Int>,
        gesehen: Boolean,
        gespielt: Boolean,
    )

    @Query(
        """
        UPDATE kartentext
        SET gesehen = 0
        WHERE gespielt = 0
          AND lokalisierung_id IN (
              SELECT kategorie_x_kartentext.kartentext_id
              FROM kategorie_x_kartentext
              INNER JOIN spiel_x_kategorie
                  ON spiel_x_kategorie.kategorie_id = kategorie_x_kartentext.kategorie_id
              WHERE spiel_x_kategorie.spiel_id = :spielId
          )
        """,
    )
    suspend fun resetGesehenFuerSpiel(spielId: Int)

    @Query(
        """
        UPDATE kartentext
        SET gesehen = 0,
            gespielt = 0
        WHERE lokalisierung_id IN (
              SELECT kategorie_x_kartentext.kartentext_id
              FROM kategorie_x_kartentext
              INNER JOIN spiel_x_kategorie
                  ON spiel_x_kategorie.kategorie_id = kategorie_x_kartentext.kategorie_id
              WHERE spiel_x_kategorie.spiel_id = :spielId
          )
        """,
    )
    suspend fun resetAlleFuerSpiel(spielId: Int)

    @Query(
        """
        UPDATE kartentext
        SET gesehen = 0,
            gespielt = 0
        """,
    )
    suspend fun resetAlle()
}
