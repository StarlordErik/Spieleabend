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
        lokalisierungId = lokalisierung.id,
        inaktiv = inaktiv,
        selbstErstellt = selbstErstellt,
        favorit = favorit,
        bildDateiname = bildDateiname,
        texteProKarte = texteProKarte,
    )

internal fun Kategorie.toEntity(): KategorieEntity =
    KategorieEntity(
        lokalisierungId = lokalisierung.id,
        inaktiv = inaktiv,
        selbstErstellt = selbstErstellt,
        favorit = favorit,
    )

internal fun Kartentext.toEntity(): KartentextEntity =
    KartentextEntity(
        lokalisierungId = lokalisierung.id,
        inaktiv = inaktiv,
        selbstErstellt = selbstErstellt,
        favorit = favorit,
        gesehen = gesehen,
        gespielt = gespielt,
    )

internal fun Lokalisierung.toEntity(): LokalisierungEntity =
    LokalisierungEntity(
        id = id,
        ogSprache = ogSprache,
    )

internal fun Translation.toEntity(lokalisierungId: Int): TranslationEntity =
    TranslationEntity(
        lokalisierungId = lokalisierungId,
        sprache = sprache,
        text = text,
        bearbeitet = bearbeitet,
    )

internal fun Spiel.toSpielXKategorieEntities(): List<SpielXKategorieEntity> =
    originaleKategorien.map { kategorie ->
        SpielXKategorieEntity(
            spielId = id(),
            kategorieId = kategorie.id(),
            inaktiv = kategorie.id() in inaktiveKategorien.map { inaktiveKategorie -> inaktiveKategorie.id() }.toSet(),
            selbstErstellt = false,
        )
    } + hinzugefuegteKategorien.map { kategorie ->
        SpielXKategorieEntity(
            spielId = id(),
            kategorieId = kategorie.id(),
            inaktiv = kategorie.id() in inaktiveKategorien.map { inaktiveKategorie -> inaktiveKategorie.id() }.toSet(),
            selbstErstellt = true,
        )
    }

internal fun Kategorie.toKategorieXKartentextEntities(): List<KategorieXKartentextEntity> =
    originaleKartentexte.map { kartentext ->
        KategorieXKartentextEntity(
            kategorieId = id(),
            kartentextId = kartentext.id(),
            inaktiv = kartentext.id() in inaktiveKartentexte.map { inaktiverKartentext -> inaktiverKartentext.id() }.toSet(),
            selbstErstellt = false,
        )
    } + hinzugefuegteKartentexte.map { kartentext ->
        KategorieXKartentextEntity(
            kategorieId = id(),
            kartentextId = kartentext.id(),
            inaktiv = kartentext.id() in inaktiveKartentexte.map { inaktiverKartentext -> inaktiverKartentext.id() }.toSet(),
            selbstErstellt = true,
        )
    }
