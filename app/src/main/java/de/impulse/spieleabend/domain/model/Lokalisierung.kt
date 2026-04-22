package de.impulse.spieleabend.domain.model

data class Lokalisierung(
    val id: String,
    val translationen: Set<Translation>,
) {
    init {
        require(id.isNotBlank()) { "Die ID einer Lokalisierung darf nicht leer sein." }
        require(translationen.isNotEmpty()) { "Eine Lokalisierung braucht mindestens eine Translation." }

        val sprachCodes = translationen.map { it.sprachCode.normalisierterSprachCode() }
        require(sprachCodes.distinct().size == sprachCodes.size) {
            "Eine Lokalisierung darf pro Sprachcode nur eine Translation enthalten."
        }
    }

    fun textFuer(sprachCode: String): String? {
        val normalisierterSprachCode = sprachCode.normalisierterSprachCode()
        val normalisierteSprache = normalisierterSprachCode.sprache()

        return translationen.firstOrNull { translation ->
            translation.istFuer(normalisierterSprachCode)
        }?.text ?: translationen.firstOrNull { translation ->
            translation.sprachCode.normalisierterSprachCode().sprache() == normalisierteSprache
        }?.text
    }

    fun textFuer(
        sprachCode: String,
        fallbackSprachCode: String,
    ): String? = textFuer(sprachCode) ?: textFuer(fallbackSprachCode)
}

private fun String.sprache(): String = substringBefore('-')
