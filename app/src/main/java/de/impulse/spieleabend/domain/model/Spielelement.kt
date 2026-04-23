package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache

abstract class Spielelement(
    open val lokalisierung: Lokalisierung,
    open val inaktiv: Boolean,
    open val selbstErstellt: Boolean,
    open val favorit: Boolean,
) {
    fun id(): Int = lokalisierung.id

    fun text(inSprache: Sprache): String = lokalisierung.text(inSprache)
}
