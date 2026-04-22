package de.impulse.spieleabend.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "kategorie",
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
data class KategorieEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "lokalisierung_id") val lokalisierungId: String,
)
