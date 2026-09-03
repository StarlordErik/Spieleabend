package de.impulse.spieleabend.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import de.impulse.spieleabend.domain.model.BearbeiteteKartentexteModus
import de.impulse.spieleabend.domain.model.FavoritenModus
import de.impulse.spieleabend.domain.model.GeloeschteKartentexteModus

@Entity(
    tableName = "spiel_zieh_einstellung",
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
data class SpielZiehEinstellungEntity(
    @ColumnInfo(name = "spiel_id") val spielId: Int,
    @ColumnInfo(name = "geloeschte_modus", defaultValue = "'ALS_LETZTE'")
    val geloeschteKartentexteModus: GeloeschteKartentexteModus = GeloeschteKartentexteModus.ALS_LETZTE,
    @ColumnInfo(name = "favoriten_modus", defaultValue = "'UNBEACHTET'")
    val favoritenModus: FavoritenModus = FavoritenModus.UNBEACHTET,
    @ColumnInfo(name = "bearbeitete_modus", defaultValue = "'UNBEACHTET'")
    val bearbeiteteKartentexteModus: BearbeiteteKartentexteModus = BearbeiteteKartentexteModus.UNBEACHTET,
)
