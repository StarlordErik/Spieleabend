package de.impulse.spieleabend.data.local.entity

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
            parentColumns = ["id"],
            childColumns = ["kategorie_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = KartentextEntity::class,
            parentColumns = ["id"],
            childColumns = ["kartentext_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["kartentext_id"]),
        Index(value = ["kategorie_id", "position"], unique = true),
    ],
)
data class KategorieXKartentextEntity(
    @ColumnInfo(name = "kategorie_id") val kategorieId: String,
    @ColumnInfo(name = "kartentext_id") val kartentextId: String,
    val position: Int,
)
