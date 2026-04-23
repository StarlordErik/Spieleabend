package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache

data class Translation(
    val sprache: Sprache,
    val text: String,
    val bearbeitet: Boolean = false,
) {
    init {
        require(text.isNotBlank()) { "Der Text einer Translation darf nicht leer sein." }
    }

    internal fun istFuer(sprache: Sprache): Boolean = this.sprache == sprache
}
