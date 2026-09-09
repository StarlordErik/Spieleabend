package de.kaserik.impulse.common

// Diagnostic messages stay usable by the domain model without an Android Context.
internal object AppMessages {
    const val EMPTY_TRANSLATION =
        "Der Text einer Translation darf nicht leer sein."
    const val EMPTY_OWN_CARD_TEXT =
        "Ein eigener Kartentext darf nicht leer sein."
    const val INVALID_TEXT_COUNT =
        "Die Anzahl der Kartentexte pro Karte muss zwischen 1 und 5 liegen."
    const val MISSING_ORIGINAL_TRANSLATION =
        "Eine Lokalisierung braucht eine OG-Translation."
    const val DUPLICATE_TRANSLATION_LANGUAGE =
        "Eine Lokalisierung darf pro Sprache nur eine Translation enthalten."
    const val EMPTY_CARD =
        "Ein Spiel muss mindestens einen Kartentext pro Karte anzeigen."
    const val EMPTY_DEFAULT_CARD =
        "Der Standard eines Spiels muss mindestens einen Kartentext pro Karte anzeigen."
    const val DUPLICATE_ORIGINAL_CATEGORY =
        "Ein Spiel darf eine originale Kategorie nur einmal referenzieren."
    const val DUPLICATE_ADDED_CATEGORY =
        "Ein Spiel darf eine hinzugefügte Kategorie nur einmal referenzieren."
    const val DUPLICATE_INACTIVE_CATEGORY =
        "Ein Spiel darf eine inaktive Kategorie nur einmal referenzieren."
    const val OVERLAPPING_CATEGORIES =
        "Ein Spiel darf eine Kategorie nicht gleichzeitig original und hinzugefügt referenzieren."
    const val UNKNOWN_INACTIVE_CATEGORY =
        "Ein Spiel darf nur referenzierte Kategorien als inaktiv markieren."
    const val DUPLICATE_CATEGORY =
        "Ein Spiel darf eine Kategorie nur einmal referenzieren."
    const val DUPLICATE_ORIGINAL_CARD_TEXT =
        "Eine Kategorie darf einen originalen Kartentext nur einmal referenzieren."
    const val DUPLICATE_ADDED_CARD_TEXT =
        "Eine Kategorie darf einen hinzugefügten Kartentext nur einmal referenzieren."
    const val DUPLICATE_INACTIVE_CARD_TEXT =
        "Eine Kategorie darf einen inaktiven Kartentext nur einmal referenzieren."
    const val OVERLAPPING_CARD_TEXTS =
        "Eine Kategorie darf einen Kartentext nicht gleichzeitig original und hinzugefügt referenzieren."
    const val UNKNOWN_INACTIVE_CARD_TEXT =
        "Eine Kategorie darf nur referenzierte Kartentexte als inaktiv markieren."
    const val DUPLICATE_CARD_TEXT =
        "Eine Kategorie darf einen Kartentext nur einmal referenzieren."

    fun unsupportedOwnLanguage(language: Sprache): String =
        "Für $language gibt es keine auswählbare eigene Sprache."

    fun missingCardText(id: Int): String =
        "Der Kartentext $id fehlt in der Datenbank."

    fun uneditableLanguage(language: Sprache): String =
        "$language kann nicht bearbeitet werden."

    fun unsupportedLanguage(language: Sprache): String =
        "$language kann nicht als App-Sprache verwendet werden."

    fun missingGame(id: Int): String =
        "Das Spiel $id fehlt in der Datenbank."

    fun missingHistoryCategory(categoryId: Int, cardId: Long): String =
        "Die Kategorie $categoryId fehlt für die gespeicherte Karte $cardId."

    fun missingHistoryCardText(textId: Int, cardId: Long): String =
        "Der Kartentext $textId fehlt für die gespeicherte Karte $cardId."

    fun missingLocalization(id: Int): String =
        "Die Lokalisierung $id fehlt in der Datenbank."

    fun missingLinkedCategory(id: Int): String =
        "Die Kategorie $id fehlt für die Spiel-Verknüpfung."

    fun missingLinkedCardText(id: Int): String =
        "Der Kartentext $id fehlt für die Kategorie-Verknüpfung."

    fun missingGameCategory(gameId: Int, categoryId: Int): String =
        "Das Spiel $gameId enthält keine Kategorie mit der ID $categoryId."
}
