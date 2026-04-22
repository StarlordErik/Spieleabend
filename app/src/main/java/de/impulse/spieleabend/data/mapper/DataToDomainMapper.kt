package de.impulse.spieleabend.data.mapper

import de.impulse.spieleabend.data.local.entity.KartentextEntity
import de.impulse.spieleabend.data.local.entity.KategorieEntity
import de.impulse.spieleabend.data.local.entity.LokalisierungEntity
import de.impulse.spieleabend.data.local.entity.SpielEntity
import de.impulse.spieleabend.data.local.entity.TranslationEntity
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
        id = id,
        lokalisierung = lokalisierung,
        kategorien = kategorien,
        kartentexteProKarte = kartentexteProKarte,
    )

internal fun KategorieEntity.toDomain(
    lokalisierung: Lokalisierung,
    kartentexte: Set<Kartentext>,
): Kategorie =
    Kategorie(
        id = id,
        lokalisierung = lokalisierung,
        kartentexte = kartentexte,
    )

internal fun KartentextEntity.toDomain(lokalisierung: Lokalisierung): Kartentext =
    Kartentext(
        id = id,
        lokalisierung = lokalisierung,
    )

internal fun LokalisierungEntity.toDomain(
    translationen: Set<Translation>,
): Lokalisierung =
    Lokalisierung(
        id = id,
        translationen = translationen,
    )

internal fun TranslationEntity.toDomain(): Translation =
    Translation(
        sprachCode = sprachCode,
        text = text,
    )
