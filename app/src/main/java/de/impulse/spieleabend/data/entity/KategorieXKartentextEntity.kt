package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "kategorie_x_kartentext",
    primaryKeys = ["kategorie_id", "kartentext_id"],
    foreignKeys = [
        ForeignKey(
            entity = KategorieEntity::class,
            parentColumns = ["lokalisierung_id"],
            childColumns = ["kategorie_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = KartentextEntity::class,
            parentColumns = ["lokalisierung_id"],
            childColumns = ["kartentext_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["kartentext_id"]),
    ],
)
data class KategorieXKartentextEntity(
    @ColumnInfo(name = "kategorie_id") val kategorieId: Int,
    @ColumnInfo(name = "kartentext_id") val kartentextId: Int,
    val inaktiv: Boolean,
    @ColumnInfo(name = "selbst_erstellt") val selbstErstellt: Boolean,
)
