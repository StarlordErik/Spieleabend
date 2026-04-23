package de.impulse.spieleabend.domain.model

data class Kartentext(
    val id: Int,
    val lokalisierung: Lokalisierung,
    val inaktiv: Boolean = false,
    val selbstErstellt: Boolean = false,
    val favorit: Boolean = false,
    val gesehen: Boolean = false,
    val gespielt: Boolean = false,
) {
    init {
        require(id > 0) { "Die ID eines Kartentextes muss positiv sein." }
    }
}
