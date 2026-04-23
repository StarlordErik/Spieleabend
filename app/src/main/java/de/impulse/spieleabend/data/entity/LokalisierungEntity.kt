package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.impulse.spieleabend.common.Sprache

@Entity(tableName = "lokalisierung")
data class LokalisierungEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "og_sprache") val ogSprache: Sprache,
)
