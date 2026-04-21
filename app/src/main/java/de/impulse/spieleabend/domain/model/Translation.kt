package de.impulse.spieleabend.domain.model

data class Translation(
    val sprachCode: String,
    val text: String,
) {
    init {
        require(sprachCode.isNotBlank()) { "Der Sprachcode einer Translation darf nicht leer sein." }
        require(text.isNotBlank()) { "Der Text einer Translation darf nicht leer sein." }
    }

    internal fun istFuer(sprachCode: String): Boolean =
        this.sprachCode.normalisierterSprachCode() == sprachCode.normalisierterSprachCode()
}

internal fun String.normalisierterSprachCode(): String = trim().lowercase()
