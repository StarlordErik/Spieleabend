@file:Suppress("unused")

package de.kaserik.impulse.data.mapper

import de.kaserik.impulse.data.entity.KartentextEntity
import de.kaserik.impulse.data.entity.KategorieEntity
import de.kaserik.impulse.data.entity.KategorieXKartentextEntity
import de.kaserik.impulse.data.entity.LokalisierungEntity
import de.kaserik.impulse.data.entity.SpielEntity
import de.kaserik.impulse.data.entity.SpielXKategorieEntity
import de.kaserik.impulse.data.entity.TranslationEntity
import de.kaserik.impulse.domain.model.Kartentext
import de.kaserik.impulse.domain.model.Kategorie
import de.kaserik.impulse.domain.model.Lokalisierung
import de.kaserik.impulse.domain.model.Spiel
import de.kaserik.impulse.domain.model.Translation

internal fun Spiel.toEntity(): SpielEntity =
    SpielEntity(
        lokalisierungId = lokalisierung.id,
        inaktiv = inaktiv,
        selbstErstellt = selbstErstellt,
        favorit = favorit,
        bildDateiname = bildDateiname,
        texteProKarte = standardTexteProKarte,
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
            inaktiv =
                kategorie.id() in
                    inaktiveKategorien
                        .map { inaktiveKategorie -> inaktiveKategorie.id() }
                        .toSet(),
            selbstErstellt = false,
        )
    } + hinzugefuegteKategorien.map { kategorie ->
        SpielXKategorieEntity(
            spielId = id(),
            kategorieId = kategorie.id(),
            inaktiv =
                kategorie.id() in
                    inaktiveKategorien
                        .map { inaktiveKategorie -> inaktiveKategorie.id() }
                        .toSet(),
            selbstErstellt = true,
        )
    }

internal fun Kategorie.toKategorieXKartentextEntities(): List<KategorieXKartentextEntity> =
    originaleKartentexte.map { kartentext ->
        KategorieXKartentextEntity(
            kategorieId = id(),
            kartentextId = kartentext.id(),
            inaktiv =
                kartentext.id() in
                    inaktiveKartentexte
                        .map { inaktiverKartentext -> inaktiverKartentext.id() }
                        .toSet(),
            selbstErstellt = false,
        )
    } + hinzugefuegteKartentexte.map { kartentext ->
        KategorieXKartentextEntity(
            kategorieId = id(),
            kartentextId = kartentext.id(),
            inaktiv =
                kartentext.id() in
                    inaktiveKartentexte
                        .map { inaktiverKartentext -> inaktiverKartentext.id() }
                        .toSet(),
            selbstErstellt = true,
        )
    }
