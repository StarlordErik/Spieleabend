package de.impulse.spieleabend.data.entity

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
            parentColumns = ["lokalisierung_id"],
            childColumns = ["spiel_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = KategorieEntity::class,
            parentColumns = ["lokalisierung_id"],
            childColumns = ["kategorie_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["kategorie_id"]),
    ],
)
data class SpielXKategorieEntity(
    @ColumnInfo(name = "spiel_id") val spielId: Int,
    @ColumnInfo(name = "kategorie_id") val kategorieId: Int,
    val inaktiv: Boolean,
    @ColumnInfo(name = "selbst_erstellt") val selbstErstellt: Boolean,
)
