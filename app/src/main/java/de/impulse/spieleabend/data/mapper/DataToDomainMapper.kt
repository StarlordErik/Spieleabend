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

internal fun SpielEntity.toDomain(
    lokalisierung: Lokalisierung,
    kategorien: Set<Kategorie>,
): Spiel =
    Spiel(
        id = lokalisierungId,
        lokalisierung = lokalisierung,
        kategorien = kategorien,
        inaktiv = inaktiv,
        selbstErstellt = selbstErstellt,
        favorit = favorit,
        bildDateiname = bildDateiname,
        texteProKarte = texteProKarte,
    )

internal fun KategorieEntity.toDomain(
    lokalisierung: Lokalisierung,
    kartentexte: Set<Kartentext>,
): Kategorie =
    Kategorie(
        id = lokalisierungId,
        lokalisierung = lokalisierung,
        kartentexte = kartentexte,
        inaktiv = inaktiv,
        selbstErstellt = selbstErstellt,
        favorit = favorit,
    )

internal fun KartentextEntity.toDomain(lokalisierung: Lokalisierung): Kartentext =
    Kartentext(
        id = lokalisierungId,
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
