package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import de.impulse.spieleabend.common.Sprache

@Entity(
    tableName = "translation",
    primaryKeys = ["lokalisierung_id", "sprache"],
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
    @ColumnInfo(name = "sprache") val sprache: Sprache,
    val text: String,
)
