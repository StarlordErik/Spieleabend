package de.kaserik.impulse.data.mapper

import de.kaserik.impulse.data.entity.KartentextEntity
import de.kaserik.impulse.data.entity.KategorieEntity
import de.kaserik.impulse.data.entity.LokalisierungEntity
import de.kaserik.impulse.data.entity.SpielEntity
import de.kaserik.impulse.data.entity.TranslationEntity
import de.kaserik.impulse.domain.model.BearbeiteteKartentexteModus
import de.kaserik.impulse.domain.model.FavoritenModus
import de.kaserik.impulse.domain.model.GeloeschteKartentexteModus
import de.kaserik.impulse.domain.model.Kartentext
import de.kaserik.impulse.domain.model.Kategorie
import de.kaserik.impulse.domain.model.Lokalisierung
import de.kaserik.impulse.domain.model.Spiel
import de.kaserik.impulse.domain.model.Translation

internal fun SpielEntity.toDomain(
    lokalisierung: Lokalisierung,
    originaleKategorien: Set<Kategorie>,
    hinzugefuegteKategorien: Set<Kategorie>,
    inaktiveKategorien: Set<Kategorie>,
    texteProKarteOverride: Int? = null,
    geloeschteKartentexteModus: GeloeschteKartentexteModus = GeloeschteKartentexteModus.ALS_LETZTE,
    favoritenModus: FavoritenModus = FavoritenModus.UNBEACHTET,
    bearbeiteteKartentexteModus: BearbeiteteKartentexteModus = BearbeiteteKartentexteModus.UNBEACHTET,
): Spiel =
    Spiel(
        lokalisierung = lokalisierung,
        originaleKategorien = originaleKategorien,
        hinzugefuegteKategorien = hinzugefuegteKategorien,
        inaktiveKategorien = inaktiveKategorien,
        inaktiv = inaktiv,
        selbstErstellt = selbstErstellt,
        favorit = favorit,
        bildDateiname = bildDateiname,
        texteProKarte = texteProKarteOverride ?: texteProKarte,
        standardTexteProKarte = texteProKarte,
        geloeschteKartentexteModus = geloeschteKartentexteModus,
        favoritenModus = favoritenModus,
        bearbeiteteKartentexteModus = bearbeiteteKartentexteModus,
    )

internal fun KategorieEntity.toDomain(
    lokalisierung: Lokalisierung,
    originaleKartentexte: Set<Kartentext>,
    hinzugefuegteKartentexte: Set<Kartentext>,
    inaktiveKartentexte: Set<Kartentext>,
): Kategorie =
    Kategorie(
        lokalisierung = lokalisierung,
        originaleKartentexte = originaleKartentexte,
        hinzugefuegteKartentexte = hinzugefuegteKartentexte,
        inaktiveKartentexte = inaktiveKartentexte,
        inaktiv = inaktiv,
        selbstErstellt = selbstErstellt,
        favorit = favorit,
    )

internal fun KartentextEntity.toDomain(lokalisierung: Lokalisierung): Kartentext =
    Kartentext(
        lokalisierung = lokalisierung,
        inaktiv = inaktiv,
        selbstErstellt = selbstErstellt,
        favorit = favorit,
        gesehen = gesehen,
        gespielt = gespielt,
    )

internal fun LokalisierungEntity.toDomain(
    translationen: Set<Translation>,
): Lokalisierung =
    Lokalisierung(
        id = id,
        translationen = translationen,
        ogSprache = ogSprache,
    )

internal fun TranslationEntity.toDomain(): Translation =
    Translation(
        sprache = sprache,
        text = text,
        bearbeitet = bearbeitet,
    )
