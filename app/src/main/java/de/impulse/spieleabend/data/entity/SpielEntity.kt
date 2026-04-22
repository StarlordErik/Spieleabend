package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    indices = [
        Index(value = ["lokalisierung_id"], unique = true),
    ],
)
data class SpielEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "lokalisierung_id") val lokalisierungId: String,
    @ColumnInfo(name = "kartentexte_pro_karte") val kartentexteProKarte: Int,
)
