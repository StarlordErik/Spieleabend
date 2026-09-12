package de.kaserik.impulse.data.dao

internal object CardTextTranslationQueries {
    private const val CARD_TEXTS_IN_SCOPE = """
        SELECT kartentext.lokalisierung_id
        FROM kartentext
        WHERE :spielId IS NULL OR kartentext.lokalisierung_id IN (
            SELECT kategorie_x_kartentext.kartentext_id
            FROM kategorie_x_kartentext
            INNER JOIN spiel_x_kategorie
                ON spiel_x_kategorie.kategorie_id = kategorie_x_kartentext.kategorie_id
            WHERE spiel_x_kategorie.spiel_id = :spielId
        )
    """

    const val ERIK_TRANSLATIONS = """
        SELECT erik.lokalisierung_id, 'EIGENE_DE' AS sprache, erik.text, 1 AS bearbeitet
        FROM translation AS erik
        WHERE erik.sprache = 'ERIK'
          AND erik.lokalisierung_id IN (""" + CARD_TEXTS_IN_SCOPE + """)
          AND (:ueberschreiben OR NOT EXISTS (
              SELECT 1 FROM translation AS eigene
              WHERE eigene.lokalisierung_id = erik.lokalisierung_id
                AND eigene.sprache = 'EIGENE_DE'
          ))
    """

    const val RESET_CUSTOM_TRANSLATIONS = """
        DELETE FROM translation
        WHERE sprache IN ('EIGENE_DE', 'EIGENE_EN')
          AND lokalisierung_id IN (""" + CARD_TEXTS_IN_SCOPE + """)
    """
}
