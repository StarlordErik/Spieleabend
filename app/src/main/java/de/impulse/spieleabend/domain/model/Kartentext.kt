package de.impulse.spieleabend.domain.model

data class Kartentext(
    val id: String,
    val lokalisierung: Lokalisierung,
) {
    init {
        require(id.isNotBlank()) { "Die ID eines Kartentextes darf nicht leer sein." }
    }
}
