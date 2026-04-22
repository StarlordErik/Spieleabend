package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "translation",
    primaryKeys = ["lokalisierung_id", "sprach_code"],
    foreignKeys = [
        ForeignKey(
            entity = LokalisierungEntity::class,
            parentColumns = ["id"],
            childColumns = ["lokalisierung_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class TranslationEntity(
    @ColumnInfo(name = "lokalisierung_id") val lokalisierungId: String,
    @ColumnInfo(name = "sprach_code") val sprachCode: String,
    val text: String,
)
