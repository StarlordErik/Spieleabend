package de.impulse.spieleabend.data

import androidx.room.withTransaction
import de.impulse.spieleabend.data.entity.KartentextEntity
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.SpielEntity
import de.impulse.spieleabend.data.mapper.toDomain
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val database: SpieleabendDatabase,
) : GameRepository {
    override suspend fun getGames(): List<Spiel> =
        database.withTransaction {
            database.spielDao().spiele().map { spiel ->
                spiel.toDomain()
            }
        }

    override suspend fun getGame(gameId: Int): Spiel =
        database.withTransaction {
            spielEntity(gameId).toDomain()
        }

    private suspend fun spielEntity(gameId: Int): SpielEntity {
        val spielDao = database.spielDao()

        return requireNotNull(spielDao.spiel(gameId)) {
            "Das Spiel $gameId fehlt in der Datenbank."
        }
    }

    private suspend fun SpielEntity.toDomain(): Spiel {
        val kategorien = kategorien(lokalisierungId)

        return toDomain(
            lokalisierung = lokalisierung(lokalisierungId),
            originaleKategorien = kategorien.originale,
            hinzugefuegteKategorien = kategorien.hinzugefuegte,
            inaktiveKategorien = kategorien.inaktive,
        )
    }

    private suspend fun kategorien(spielId: Int): KategorienSets {
        val spielDao = database.spielDao()
        val spielXKategorien = spielDao.spielXKategorienFuerSpiel(spielId)
        val kategorienById =
            spielDao
                .kategorienFuerSpiel(spielId)
                .map { kategorie -> kategorie.toDomain() }
                .associateBy { kategorie -> kategorie.id() }

        return KategorienSets(
            originale = spielXKategorien
                .filterNot { spielXKategorie -> spielXKategorie.selbstErstellt }
                .map { spielXKategorie -> kategorie(kategorienById, spielXKategorie.kategorieId) }
                .toCollection(LinkedHashSet()),
            hinzugefuegte = spielXKategorien
                .filter { spielXKategorie -> spielXKategorie.selbstErstellt }
                .map { spielXKategorie -> kategorie(kategorienById, spielXKategorie.kategorieId) }
                .toCollection(LinkedHashSet()),
            inaktive = spielXKategorien
                .filter { spielXKategorie -> spielXKategorie.inaktiv }
                .map { spielXKategorie -> kategorie(kategorienById, spielXKategorie.kategorieId) }
                .toCollection(LinkedHashSet()),
        )
    }

    private suspend fun KategorieEntity.toDomain(): Kategorie =
        kartentexte(lokalisierungId).let { kartentexte ->
            toDomain(
                lokalisierung = lokalisierung(lokalisierungId),
                originaleKartentexte = kartentexte.originale,
                hinzugefuegteKartentexte = kartentexte.hinzugefuegte,
                inaktiveKartentexte = kartentexte.inaktive,
            )
        }

    private suspend fun kartentexte(kategorieId: Int): KartentexteSets {
        val kategorieDao = database.kategorieDao()
        val kategorieXKartentexte = kategorieDao.kategorieXKartentexteFuerKategorie(kategorieId)
        val kartentexteById =
            kategorieDao
                .kartentexteFuerKategorie(kategorieId)
                .map { kartentext -> kartentext.toDomain() }
                .associateBy { kartentext -> kartentext.id() }

        return KartentexteSets(
            originale = kategorieXKartentexte
                .filterNot { kategorieXKartentext -> kategorieXKartentext.selbstErstellt }
                .map { kategorieXKartentext -> kartentext(kartentexteById, kategorieXKartentext.kartentextId) }
                .toCollection(LinkedHashSet()),
            hinzugefuegte = kategorieXKartentexte
                .filter { kategorieXKartentext -> kategorieXKartentext.selbstErstellt }
                .map { kategorieXKartentext -> kartentext(kartentexteById, kategorieXKartentext.kartentextId) }
                .toCollection(LinkedHashSet()),
            inaktive = kategorieXKartentexte
                .filter { kategorieXKartentext -> kategorieXKartentext.inaktiv }
                .map { kategorieXKartentext -> kartentext(kartentexteById, kategorieXKartentext.kartentextId) }
                .toCollection(LinkedHashSet()),
        )
    }

    private suspend fun KartentextEntity.toDomain(): Kartentext =
        toDomain(lokalisierung = lokalisierung(lokalisierungId))

    private suspend fun lokalisierung(lokalisierungId: Int): Lokalisierung {
        val lokalisierungDao = database.lokalisierungDao()
        val lokalisierung = requireNotNull(lokalisierungDao.lokalisierung(lokalisierungId)) {
            "Die Lokalisierung $lokalisierungId fehlt in der Datenbank."
        }
        val translationen = lokalisierungDao
            .translationenFuerLokalisierung(lokalisierungId)
            .map { translation -> translation.toDomain() }
            .toCollection(LinkedHashSet())

        return lokalisierung.toDomain(translationen)
    }

    private fun kategorie(
        kategorienById: Map<Int, Kategorie>,
        kategorieId: Int,
    ): Kategorie =
        requireNotNull(kategorienById[kategorieId]) {
            "Die Kategorie $kategorieId fehlt für die Spiel-Verknüpfung."
        }

    private fun kartentext(
        kartentexteById: Map<Int, Kartentext>,
        kartentextId: Int,
    ): Kartentext =
        requireNotNull(kartentexteById[kartentextId]) {
            "Der Kartentext $kartentextId fehlt für die Kategorie-Verknüpfung."
        }

    private data class KategorienSets(
        val originale: Set<Kategorie>,
        val hinzugefuegte: Set<Kategorie>,
        val inaktive: Set<Kategorie>,
    )

    private data class KartentexteSets(
        val originale: Set<Kartentext>,
        val hinzugefuegte: Set<Kartentext>,
        val inaktive: Set<Kartentext>,
    )
}
