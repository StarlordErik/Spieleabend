package de.impulse.spieleabend.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "spiel_x_kategorie",
    primaryKeys = ["spiel_id", "kategorie_id"],
    foreignKeys = [
        ForeignKey(
            entity = SpielEntity::class,
            parentColumns = ["id"],
            childColumns = ["spiel_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = KategorieEntity::class,
            parentColumns = ["id"],
            childColumns = ["kategorie_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["kategorie_id"]),
        Index(value = ["spiel_id", "position"], unique = true),
    ],
)
data class SpielXKategorieEntity(
    @ColumnInfo(name = "spiel_id") val spielId: String,
    @ColumnInfo(name = "kategorie_id") val kategorieId: String,
    val position: Int,
)
