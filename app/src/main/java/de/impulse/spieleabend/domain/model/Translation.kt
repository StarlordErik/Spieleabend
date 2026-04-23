package de.impulse.spieleabend.domain.model

import de.impulse.spieleabend.common.Sprache

data class Translation(
    val sprache: Sprache,
    val text: String,
) {
    constructor(
        sprache: String,
        text: String,
    ) : this(
        sprache =
            requireNotNull(Sprache.fromCode(sprache)) {
                "Die Sprache einer Translation ist ungueltig: '$sprache'."
            },
        text = text,
    )

    init {
        require(text.isNotBlank()) { "Der Text einer Translation darf nicht leer sein." }
    }

    internal fun istFuer(sprache: Sprache): Boolean = this.sprache == sprache

    internal fun istFuer(sprache: String): Boolean =
        Sprache.fromCode(sprache)?.let(::istFuer) ?: false
}
