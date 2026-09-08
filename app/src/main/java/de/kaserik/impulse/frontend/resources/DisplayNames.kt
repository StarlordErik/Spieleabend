package de.kaserik.impulse.frontend.resources

import androidx.annotation.StringRes
import de.kaserik.impulse.R
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.model.BearbeiteteKartentexteModus
import de.kaserik.impulse.domain.model.FavoritenModus
import de.kaserik.impulse.domain.model.GeloeschteKartentexteModus

@StringRes
internal fun Sprache.displayNameRes(): Int = when (this) {
    Sprache.DE -> R.string.language_german
    Sprache.EN -> R.string.language_english
    Sprache.ERIK -> R.string.language_erik
    Sprache.OG -> R.string.language_original
    Sprache.EIGENE_DE -> R.string.language_own_german
    Sprache.EIGENE_EN -> R.string.language_own_english
}

@StringRes
internal fun GeloeschteKartentexteModus.displayNameRes(): Int = when (this) {
    GeloeschteKartentexteModus.AUSBLENDEN -> R.string.mode_hide
    GeloeschteKartentexteModus.ALS_LETZTE -> R.string.mode_show_last
    GeloeschteKartentexteModus.UNBEACHTET -> R.string.mode_show_regardless
    GeloeschteKartentexteModus.AUSSCHLIESSLICH -> R.string.mode_show_exclusively
}

@StringRes
internal fun FavoritenModus.displayNameRes(): Int = when (this) {
    FavoritenModus.UNBEACHTET -> R.string.mode_show_regardless
    FavoritenModus.GENAU_EINER_PRO_KARTE -> R.string.mode_one_favorite_per_card
    FavoritenModus.AUSSCHLIESSLICH -> R.string.mode_favorites_only
}

@StringRes
internal fun BearbeiteteKartentexteModus.displayNameRes(): Int = when (this) {
    BearbeiteteKartentexteModus.UNBEACHTET -> R.string.mode_show_regardless
    BearbeiteteKartentexteModus.AUSSCHLIESSLICH -> R.string.mode_edited_only
}
