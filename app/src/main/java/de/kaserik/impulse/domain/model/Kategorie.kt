package de.kaserik.impulse.domain.model

import de.kaserik.impulse.common.AppMessages

data class Kategorie(
    override val lokalisierung: Lokalisierung,
    val originaleKartentexte: Set<Kartentext> = emptySet(),
    val hinzugefuegteKartentexte: Set<Kartentext> = emptySet(),
    val inaktiveKartentexte: Set<Kartentext> = emptySet(),
    override val inaktiv: Boolean = false,
    override val selbstErstellt: Boolean = false,
    override val favorit: Boolean = false,
) : Spielelement(lokalisierung, inaktiv, selbstErstellt, favorit) {
    val kartentexte: Set<Kartentext>
        get() {
            val inaktiveKartentextIds = inaktiveKartentexte.map { kartentext -> kartentext.id() }.toSet()

            return (originaleKartentexte + hinzugefuegteKartentexte)
                .filterNot { kartentext -> kartentext.id() in inaktiveKartentextIds }
                .toSet()
        }

    init {
        val originaleKartentextIds = originaleKartentexte.map { kartentext -> kartentext.id() }
        require(originaleKartentextIds.distinct().size == originaleKartentextIds.size) {
            AppMessages.DUPLICATE_ORIGINAL_CARD_TEXT
        }

        val hinzugefuegteKartentextIds = hinzugefuegteKartentexte.map { kartentext -> kartentext.id() }
        require(hinzugefuegteKartentextIds.distinct().size == hinzugefuegteKartentextIds.size) {
            AppMessages.DUPLICATE_ADDED_CARD_TEXT
        }

        val inaktiveKartentextIds = inaktiveKartentexte.map { kartentext -> kartentext.id() }
        require(inaktiveKartentextIds.distinct().size == inaktiveKartentextIds.size) {
            AppMessages.DUPLICATE_INACTIVE_CARD_TEXT
        }

        val doppelteKartentextIds = originaleKartentextIds.intersect(hinzugefuegteKartentextIds.toSet())
        require(doppelteKartentextIds.isEmpty()) {
            AppMessages.OVERLAPPING_CARD_TEXTS
        }

        val bekannteKartentextIds = (originaleKartentextIds + hinzugefuegteKartentextIds).toSet()
        require(inaktiveKartentextIds.all { kartentextId -> kartentextId in bekannteKartentextIds }) {
            AppMessages.UNKNOWN_INACTIVE_CARD_TEXT
        }

        val kartentextIds = bekannteKartentextIds.toList()
        require(kartentextIds.distinct().size == kartentextIds.size) {
            AppMessages.DUPLICATE_CARD_TEXT
        }
    }
}
