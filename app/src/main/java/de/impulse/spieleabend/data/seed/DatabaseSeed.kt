package de.impulse.spieleabend.data.seed

import de.impulse.spieleabend.data.local.entity.KartentextEntity
import de.impulse.spieleabend.data.local.entity.KategorieEntity
import de.impulse.spieleabend.data.local.entity.KategorieXKartentextEntity
import de.impulse.spieleabend.data.local.entity.LokalisierungEntity
import de.impulse.spieleabend.data.local.entity.SpielEntity
import de.impulse.spieleabend.data.local.entity.SpielXKategorieEntity
import de.impulse.spieleabend.data.local.entity.TranslationEntity
import de.impulse.spieleabend.data.mapper.toEntity
import de.impulse.spieleabend.data.mapper.toKategorieXKartentextEntities
import de.impulse.spieleabend.data.mapper.toSpielXKategorieEntities
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel

internal data class DatabaseSeed(
    val spiele: List<SpielEntity>,
    val kategorien: List<KategorieEntity>,
    val kartentexte: List<KartentextEntity>,
    val lokalisierungen: List<LokalisierungEntity>,
    val translationen: List<TranslationEntity>,
    val spielXKategorien: List<SpielXKategorieEntity>,
    val kategorieXKartentexte: List<KategorieXKartentextEntity>,
)

internal fun List<Spiel>.toDatabaseSeed(): DatabaseSeed {
    val spiele = LinkedHashMap<String, SpielEntity>()
    val kategorien = LinkedHashMap<String, KategorieEntity>()
    val kartentexte = LinkedHashMap<String, KartentextEntity>()
    val lokalisierungen = LinkedHashMap<String, LokalisierungEntity>()
    val translationen = LinkedHashMap<Pair<String, String>, TranslationEntity>()
    val spielXKategorien = LinkedHashMap<Pair<String, String>, SpielXKategorieEntity>()
    val kategorieXKartentexte =
        LinkedHashMap<Pair<String, String>, KategorieXKartentextEntity>()

    fun addLokalisierung(lokalisierung: Lokalisierung) {
        lokalisierungen[lokalisierung.id] = lokalisierung.toEntity()
        lokalisierung.translationen
            .map { translation -> translation.toEntity(lokalisierung.id) }
            .forEach { translation -> translationen[translation.key] = translation }
    }

    forEach { spiel ->
        spiele[spiel.id] = spiel.toEntity()
        addLokalisierung(spiel.lokalisierung)
        spiel.toSpielXKategorieEntities().forEach { relation ->
            spielXKategorien[relation.key] = relation
        }

        spiel.kategorien.forEach { kategorie ->
            kategorien[kategorie.id] = kategorie.toEntity()
            addLokalisierung(kategorie.lokalisierung)
            kategorie.toKategorieXKartentextEntities().forEach { relation ->
                kategorieXKartentexte[relation.key] = relation
            }

            kategorie.kartentexte.forEach { kartentext ->
                kartentexte[kartentext.id] = kartentext.toEntity()
                addLokalisierung(kartentext.lokalisierung)
            }
        }
    }

    return DatabaseSeed(
        spiele = spiele.values.toList(),
        kategorien = kategorien.values.toList(),
        kartentexte = kartentexte.values.toList(),
        lokalisierungen = lokalisierungen.values.toList(),
        translationen = translationen.values.toList(),
        spielXKategorien = spielXKategorien.values.toList(),
        kategorieXKartentexte = kategorieXKartentexte.values.toList(),
    )
}

private val TranslationEntity.key: Pair<String, String>
    get() = lokalisierungId to sprachCode

private val SpielXKategorieEntity.key: Pair<String, String>
    get() = spielId to kategorieId

private val KategorieXKartentextEntity.key: Pair<String, String>
    get() = kategorieId to kartentextId
