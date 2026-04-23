package de.impulse.spieleabend.domain.model

data class Kartentext(
    override val lokalisierung: Lokalisierung,
    override val inaktiv: Boolean = false,
    override val selbstErstellt: Boolean = false,
    override val favorit: Boolean = false,
    val gesehen: Boolean = false,
    val gespielt: Boolean = false,
) : Spielelement(lokalisierung, inaktiv, selbstErstellt, favorit)
