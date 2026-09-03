package de.impulse.spieleabend.data.mapper

import de.impulse.spieleabend.data.entity.KartentextEntity
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.LokalisierungEntity
import de.impulse.spieleabend.data.entity.SpielEntity
import de.impulse.spieleabend.data.entity.TranslationEntity
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation
import de.impulse.spieleabend.domain.model.BearbeiteteKartentexteModus
import de.impulse.spieleabend.domain.model.FavoritenModus
import de.impulse.spieleabend.domain.model.GeloeschteKartentexteModus

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
