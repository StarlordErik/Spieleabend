package de.impulse.spieleabend.data

import androidx.room.withTransaction
import de.impulse.spieleabend.data.entity.KartentextEntity
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.SpielEntity
import de.impulse.spieleabend.data.mapper.toDomain
import de.impulse.spieleabend.data.seed.InitialGameData
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val database: SpieleabendDatabase,
) : GameRepository {
    override suspend fun getGame(gameId: String): Spiel =
        database.withTransaction {
            val spiel = spielEntity(gameId)
            spiel.toDomain(
                lokalisierung = lokalisierung(spiel.lokalisierungId),
                kategorien = kategorien(spiel.id),
            )
        }

    private suspend fun spielEntity(gameId: String): SpielEntity {
        val spielDao = database.spielDao()

        return spielDao.spiel(gameId)
            ?: spielDao.spiel(InitialGameData.DEFAULT_GAME_ID)
            ?: requireNotNull(spielDao.erstesSpiel()) {
                "Die Spieleabend-Datenbank enthaelt kein Spiel."
            }
    }

    private suspend fun kategorien(spielId: String): Set<Kategorie> =
        database
            .spielDao()
            .kategorienFuerSpiel(spielId)
            .map { kategorie -> kategorie.toDomain() }
            .toCollection(LinkedHashSet())

    private suspend fun KategorieEntity.toDomain(): Kategorie =
        toDomain(
            lokalisierung = lokalisierung(lokalisierungId),
            kartentexte = kartentexte(id),
        )

    private suspend fun kartentexte(kategorieId: String): Set<Kartentext> =
        database
            .kategorieDao()
            .kartentexteFuerKategorie(kategorieId)
            .map { kartentext -> kartentext.toDomain() }
            .toCollection(LinkedHashSet())

    private suspend fun KartentextEntity.toDomain(): Kartentext =
        toDomain(lokalisierung = lokalisierung(lokalisierungId))

    private suspend fun lokalisierung(lokalisierungId: String): Lokalisierung {
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
}
