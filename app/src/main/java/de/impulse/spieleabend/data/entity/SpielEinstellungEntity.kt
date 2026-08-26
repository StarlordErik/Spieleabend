package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "spiel_einstellung",
    primaryKeys = ["spiel_id"],
    foreignKeys = [
        ForeignKey(
            entity = SpielEntity::class,
            parentColumns = ["lokalisierung_id"],
            childColumns = ["spiel_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SpielEinstellungEntity(
    @ColumnInfo(name = "spiel_id") val spielId: Int,
    @ColumnInfo(name = "texte_pro_karte_override") val texteProKarteOverride: Int,
)
