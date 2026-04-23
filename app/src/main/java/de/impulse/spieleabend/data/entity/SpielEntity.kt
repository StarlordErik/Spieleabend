package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "spiel",
    foreignKeys = [
        ForeignKey(
            entity = LokalisierungEntity::class,
            parentColumns = ["id"],
            childColumns = ["lokalisierung_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SpielEntity(
    @PrimaryKey
    @ColumnInfo(name = "lokalisierung_id")
    val lokalisierungId: Int,
    @ColumnInfo(name = "kartentexte_pro_karte") val kartentexteProKarte: Int,
)
