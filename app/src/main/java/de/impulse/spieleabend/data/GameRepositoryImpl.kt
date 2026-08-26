package de.impulse.spieleabend.data

import androidx.room.withTransaction
import de.impulse.spieleabend.data.entity.GezogeneKarteEntity
import de.impulse.spieleabend.data.entity.GezogenerKartentextEntity
import de.impulse.spieleabend.data.entity.KartentextEntity
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.SpielEntity
import de.impulse.spieleabend.data.entity.SpielEinstellungEntity
import de.impulse.spieleabend.data.mapper.toDomain
import de.impulse.spieleabend.domain.model.CardHistoryState
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

@Suppress("TooManyFunctions")
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

    override suspend fun commitCardDraw(
        gameId: Int,
        resetSeenCategoryIds: Set<Int>,
        resetSeenAndPlayedCategoryIds: Set<Int>,
        card: GezogeneKarte,
    ): CardHistoryState =
        database.withTransaction {
            val kartentextDao = database.kartentextDao()
            val seenOnlyResetCategoryIds = resetSeenCategoryIds - resetSeenAndPlayedCategoryIds

            if (resetSeenAndPlayedCategoryIds.isNotEmpty()) {
                kartentextDao.updateGesehenUndGespieltForKategorien(
                    kategorieIds = resetSeenAndPlayedCategoryIds.toList(),
                    gesehen = false,
                    gespielt = false,
                )
            }

            if (seenOnlyResetCategoryIds.isNotEmpty()) {
                kartentextDao.updateGesehenForNichtGespielteKategorien(
                    kategorieIds = seenOnlyResetCategoryIds.toList(),
                    gesehen = false,
                )
            }

            val seenCardTextIds = card.kartentexte
                .map { gezogenerKartentext -> gezogenerKartentext.kartentext.id() }
                .toSet()
            if (seenCardTextIds.isNotEmpty()) {
                kartentextDao.updateGesehenForKartentexte(
                    kartentextIds = seenCardTextIds.toList(),
                    gesehen = true,
                )
            }

            val kartenverlaufDao = database.kartenverlaufDao()
            val cardInstanceId = kartenverlaufDao.insertKarte(GezogeneKarteEntity(spielId = gameId))
            kartenverlaufDao.insertKartentexte(
                card.kartentexte.mapIndexed { position, gezogenerKartentext ->
                    GezogenerKartentextEntity(
                        karteId = cardInstanceId,
                        position = position,
                        kartentextId = gezogenerKartentext.kartentext.id(),
                        kategorieId = gezogenerKartentext.kategorieId,
                    )
                },
            )
            trimCardHistory(gameId)

            CardHistoryState(
                card = card,
                instanceId = cardInstanceId,
                hasPrevious = kartenverlaufDao.neuesteKarten(gameId, PreviousCheckLimit).size > 1,
            )
        }

    override suspend fun getCurrentCard(gameId: Int): CardHistoryState? =
        database.withTransaction {
            val cards = database.kartenverlaufDao().neuesteKarten(gameId, PreviousCheckLimit)
            val currentCard = cards.firstOrNull() ?: return@withTransaction null
            currentCard.toHistoryState(
                spiel = spielEntity(gameId).toDomain(),
                hasPrevious = cards.size > 1,
            )
        }

    override suspend fun popCurrentCard(gameId: Int): CardHistoryState? =
        database.withTransaction {
            val kartenverlaufDao = database.kartenverlaufDao()
            val cards = kartenverlaufDao.neuesteKarten(gameId, PreviousCheckLimit)
            if (cards.size < PreviousCheckLimit) {
                return@withTransaction null
            }

            kartenverlaufDao.deleteKarte(cards.first().id)
            val remainingCards = kartenverlaufDao.neuesteKarten(gameId, PreviousCheckLimit)
            remainingCards.first().toHistoryState(
                spiel = spielEntity(gameId).toDomain(),
                hasPrevious = remainingCards.size > 1,
            )
        }

    override suspend fun setCardTextsPlayedState(
        cardTextIds: Set<Int>,
        gespielt: Boolean,
    ) {
        if (cardTextIds.isEmpty()) {
            return
        }

        database.withTransaction {
            database.kartentextDao().updateGespieltForKartentexte(
                kartentextIds = cardTextIds.toList(),
                gespielt = gespielt,
            )
        }
    }

    override suspend fun resetSeenCards(gameId: Int) {
        database.withTransaction {
            spielEntity(gameId)
            database.kartentextDao().resetGesehenFuerSpiel(gameId)
        }
    }

    override suspend fun resetAllCards(gameId: Int) {
        database.withTransaction {
            spielEntity(gameId)
            database.kartentextDao().resetAlleFuerSpiel(gameId)
        }
    }

    override suspend fun resetAllCardsForAllGames() {
        database.withTransaction {
            database.kartentextDao().resetAlle()
        }
    }

    override suspend fun setTextsPerCardOverride(
        gameId: Int,
        value: Int?,
    ) {
        require(value == null || value in AllowedTextsPerCard) {
            "Die Anzahl der Kartentexte pro Karte muss zwischen 1 und 5 liegen."
        }

        database.withTransaction {
            spielEntity(gameId)
            if (value == null) {
                database.spielEinstellungDao().delete(gameId)
            } else {
                database.spielEinstellungDao().upsert(
                    SpielEinstellungEntity(
                        spielId = gameId,
                        texteProKarteOverride = value,
                    ),
                )
            }
        }
    }

    private suspend fun spielEntity(gameId: Int): SpielEntity {
        val spielDao = database.spielDao()

        return requireNotNull(spielDao.spiel(gameId)) {
            "Das Spiel $gameId fehlt in der Datenbank."
        }
    }

    private suspend fun SpielEntity.toDomain(): Spiel {
        val kategorien = kategorien(lokalisierungId)
        val texteProKarteOverride = database.spielEinstellungDao()
            .einstellung(lokalisierungId)
            ?.texteProKarteOverride

        return toDomain(
            lokalisierung = lokalisierung(lokalisierungId),
            originaleKategorien = kategorien.originale,
            hinzugefuegteKategorien = kategorien.hinzugefuegte,
            inaktiveKategorien = kategorien.inaktive,
            texteProKarteOverride = texteProKarteOverride,
        )
    }

    private suspend fun GezogeneKarteEntity.toHistoryState(
        spiel: Spiel,
        hasPrevious: Boolean,
    ): CardHistoryState {
        val kategorienById =
            (spiel.originaleKategorien + spiel.hinzugefuegteKategorien)
                .associateBy { kategorie -> kategorie.id() }
        val card =
            GezogeneKarte(
                database.kartenverlaufDao().kartentexte(id).map { cardTextEntity ->
                    val kategorie = requireNotNull(kategorienById[cardTextEntity.kategorieId]) {
                        "Die Kategorie ${cardTextEntity.kategorieId} fehlt für die gespeicherte Karte $id."
                    }
                    val kartentext = requireNotNull(
                        (kategorie.originaleKartentexte + kategorie.hinzugefuegteKartentexte)
                            .firstOrNull { kartentext -> kartentext.id() == cardTextEntity.kartentextId },
                    ) {
                        "Der Kartentext ${cardTextEntity.kartentextId} fehlt für die gespeicherte Karte $id."
                    }

                    GezogenerKartentext(
                        kartentext = kartentext,
                        kategorieId = cardTextEntity.kategorieId,
                    )
                },
            )

        return CardHistoryState(
            card = card,
            instanceId = id,
            hasPrevious = hasPrevious,
        )
    }

    private suspend fun trimCardHistory(gameId: Int) {
        val kartenverlaufDao = database.kartenverlaufDao()
        val oldCardIds = kartenverlaufDao.aeltereKartenIds(
            spielId = gameId,
            behalten = MaxStoredCards,
        )
        if (oldCardIds.isNotEmpty()) {
            kartenverlaufDao.deleteKarten(oldCardIds)
        }
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

    private companion object {
        const val MaxStoredCards = 11
        const val PreviousCheckLimit = 2
        val AllowedTextsPerCard = 1..5
    }
}
