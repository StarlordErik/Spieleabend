package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache

data class Kartentext(
    val id: Int,
    val lokalisierung: Lokalisierung,
    val inaktiv: Boolean = false,
    val selbstErstellt: Boolean = false,
    val favorit: Boolean = false,
    val gesehen: Boolean = false,
    val gespielt: Boolean = false,
) {
    fun text(inSprache: Sprache): String = lokalisierung.text(inSprache)
}
