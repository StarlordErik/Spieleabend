package de.kaserik.impulse.common

enum class Sprache {
    OG,
    DE,
    EN,
    ERIK,
    EIGENE_DE,
    EIGENE_EN,
    ;

    val auswaehlbar: Boolean
        get() = this in AuswaehlbareSprachen

    fun eigeneSprache(): Sprache =
        when (this) {
            DE, ERIK -> EIGENE_DE
            EN -> EIGENE_EN
            OG, EIGENE_DE, EIGENE_EN -> error(AppMessages.unsupportedOwnLanguage(this))
        }

    companion object {
        val AuswaehlbareSprachen: List<Sprache> = listOf(DE, EN)
    }
}
