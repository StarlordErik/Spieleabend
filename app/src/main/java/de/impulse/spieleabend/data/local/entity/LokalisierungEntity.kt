package de.impulse.spieleabend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lokalisierung")
data class LokalisierungEntity(
    @PrimaryKey val id: String,
)
