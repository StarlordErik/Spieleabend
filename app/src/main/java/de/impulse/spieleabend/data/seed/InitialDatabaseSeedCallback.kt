package de.impulse.spieleabend.data.seed

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.impulse.spieleabend.data.entity.KartentextEntity
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.KategorieXKartentextEntity
import de.impulse.spieleabend.data.entity.LokalisierungEntity
import de.impulse.spieleabend.data.entity.SpielEntity
import de.impulse.spieleabend.data.entity.SpielXKategorieEntity
import de.impulse.spieleabend.data.entity.TranslationEntity

internal class InitialDatabaseSeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        InitialGameData.spiele
            .toDatabaseSeed()
            .insertInto(db)
    }
}

private fun DatabaseSeed.insertInto(db: SupportSQLiteDatabase) {
    lokalisierungen.forEach { lokalisierung -> db.insert(lokalisierung) }
    translationen.forEach { translation -> db.insert(translation) }
    spiele.forEach { spiel -> db.insert(spiel) }
    kategorien.forEach { kategorie -> db.insert(kategorie) }
    kartentexte.forEach { kartentext -> db.insert(kartentext) }
    spielXKategorien.forEach { relation -> db.insert(relation) }
    kategorieXKartentexte.forEach { relation -> db.insert(relation) }
}

private fun SupportSQLiteDatabase.insert(lokalisierung: LokalisierungEntity) {
    execSQL(
        "INSERT OR REPLACE INTO lokalisierung (id) VALUES (?)",
        arrayOf<Any>(lokalisierung.id),
    )
}

private fun SupportSQLiteDatabase.insert(translation: TranslationEntity) {
    execSQL(
        """
        INSERT OR REPLACE INTO translation (lokalisierung_id, sprach_code, text)
        VALUES (?, ?, ?)
        """,
        arrayOf<Any>(
            translation.lokalisierungId,
            translation.sprachCode,
            translation.text,
        ),
    )
}

private fun SupportSQLiteDatabase.insert(spiel: SpielEntity) {
    execSQL(
        """
        INSERT OR REPLACE INTO spiel (id, lokalisierung_id, kartentexte_pro_karte)
        VALUES (?, ?, ?)
        """,
        arrayOf<Any>(
            spiel.id,
            spiel.lokalisierungId,
            spiel.kartentexteProKarte,
        ),
    )
}

private fun SupportSQLiteDatabase.insert(kategorie: KategorieEntity) {
    execSQL(
        "INSERT OR REPLACE INTO kategorie (id, lokalisierung_id) VALUES (?, ?)",
        arrayOf<Any>(kategorie.id, kategorie.lokalisierungId),
    )
}

private fun SupportSQLiteDatabase.insert(kartentext: KartentextEntity) {
    execSQL(
        "INSERT OR REPLACE INTO kartentext (id, lokalisierung_id) VALUES (?, ?)",
        arrayOf<Any>(kartentext.id, kartentext.lokalisierungId),
    )
}

private fun SupportSQLiteDatabase.insert(relation: SpielXKategorieEntity) {
    execSQL(
        """
        INSERT OR REPLACE INTO spiel_x_kategorie (spiel_id, kategorie_id, position)
        VALUES (?, ?, ?)
        """,
        arrayOf<Any>(
            relation.spielId,
            relation.kategorieId,
            relation.position,
        ),
    )
}

private fun SupportSQLiteDatabase.insert(relation: KategorieXKartentextEntity) {
    execSQL(
        """
        INSERT OR REPLACE INTO kategorie_x_kartentext (
            kategorie_id,
            kartentext_id,
            position
        )
        VALUES (?, ?, ?)
        """,
        arrayOf<Any>(
            relation.kategorieId,
            relation.kartentextId,
            relation.position,
        ),
    )
}
