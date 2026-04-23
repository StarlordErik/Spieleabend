package de.impulse.spieleabend.domain.model

data class Kartentext(
    val id: Int,
    val lokalisierung: Lokalisierung,
) {
    init {
        require(id > 0) { "Die ID eines Kartentextes muss positiv sein." }
    }
}
