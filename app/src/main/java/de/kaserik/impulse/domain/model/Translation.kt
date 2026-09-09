package de.kaserik.impulse.domain.model

import de.kaserik.impulse.common.AppMessages
import de.kaserik.impulse.common.Sprache

data class Translation(
    val sprache: Sprache,
    val text: String,
    val bearbeitet: Boolean = false,
) {
    init {
        require(text.isNotBlank()) { AppMessages.EMPTY_TRANSLATION }
    }
}
