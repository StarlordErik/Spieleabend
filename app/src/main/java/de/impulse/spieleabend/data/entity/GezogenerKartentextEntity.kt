package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "gezogener_kartentext",
    primaryKeys = ["karte_id", "position"],
    foreignKeys = [
        ForeignKey(
            entity = GezogeneKarteEntity::class,
            parentColumns = ["id"],
            childColumns = ["karte_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = KartentextEntity::class,
            parentColumns = ["lokalisierung_id"],
            childColumns = ["kartentext_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = KategorieEntity::class,
            parentColumns = ["lokalisierung_id"],
            childColumns = ["kategorie_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["kartentext_id"]),
        Index(value = ["kategorie_id"]),
    ],
)
data class GezogenerKartentextEntity(
    @ColumnInfo(name = "karte_id") val karteId: Long,
    val position: Int,
    @ColumnInfo(name = "kartentext_id") val kartentextId: Int,
    @ColumnInfo(name = "kategorie_id") val kategorieId: Int,
)
