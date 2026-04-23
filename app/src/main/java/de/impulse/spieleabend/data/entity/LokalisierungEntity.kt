package de.impulse.spieleabend.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lokalisierung")
data class LokalisierungEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
