package de.impulse.spieleabend.data.mapper

import de.impulse.spieleabend.data.entity.KartentextEntity
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.KategorieXKartentextEntity
import de.impulse.spieleabend.data.entity.LokalisierungEntity
import de.impulse.spieleabend.data.entity.SpielEntity
import de.impulse.spieleabend.data.entity.SpielXKategorieEntity
import de.impulse.spieleabend.data.entity.TranslationEntity
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation

internal fun Spiel.toEntity(): SpielEntity =
    SpielEntity(
        id = id,
        lokalisierungId = lokalisierung.id,
        kartentexteProKarte = kartentexteProKarte,
    )

internal fun Kategorie.toEntity(): KategorieEntity =
    KategorieEntity(
        id = id,
        lokalisierungId = lokalisierung.id,
    )

internal fun Kartentext.toEntity(): KartentextEntity =
    KartentextEntity(
        id = id,
        lokalisierungId = lokalisierung.id,
    )

internal fun Lokalisierung.toEntity(): LokalisierungEntity =
    LokalisierungEntity(id = id)

internal fun Translation.toEntity(lokalisierungId: String): TranslationEntity =
    TranslationEntity(
        lokalisierungId = lokalisierungId,
        sprache = sprache,
        text = text,
    )

internal fun Spiel.toSpielXKategorieEntities(): List<SpielXKategorieEntity> =
    kategorien.mapIndexed { index, kategorie ->
        SpielXKategorieEntity(
            spielId = id,
            kategorieId = kategorie.id,
            position = index,
        )
    }

internal fun Kategorie.toKategorieXKartentextEntities(): List<KategorieXKartentextEntity> =
    kartentexte.mapIndexed { index, kartentext ->
        KategorieXKartentextEntity(
            kategorieId = id,
            kartentextId = kartentext.id,
            position = index,
        )
    }
