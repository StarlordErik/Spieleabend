package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "kartentext",
    foreignKeys = [
        ForeignKey(
            entity = LokalisierungEntity::class,
            parentColumns = ["id"],
            childColumns = ["lokalisierung_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class KartentextEntity(
    @PrimaryKey
    @ColumnInfo(name = "lokalisierung_id")
    val lokalisierungId: Int,
    val inaktiv: Boolean,
    @ColumnInfo(name = "selbst_erstellt") val selbstErstellt: Boolean,
    val favorit: Boolean,
    val gesehen: Boolean,
    val gespielt: Boolean,
)
