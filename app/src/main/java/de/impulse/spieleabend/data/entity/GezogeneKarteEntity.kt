package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gezogene_karte",
    foreignKeys = [
        ForeignKey(
            entity = SpielEntity::class,
            parentColumns = ["lokalisierung_id"],
            childColumns = ["spiel_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["spiel_id"]),
    ],
)
data class GezogeneKarteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "spiel_id") val spielId: Int,
)
