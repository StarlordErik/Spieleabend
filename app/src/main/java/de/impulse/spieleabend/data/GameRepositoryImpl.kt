package de.impulse.spieleabend.data

import androidx.room.withTransaction
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.data.entity.GezogeneKarteEntity
import de.impulse.spieleabend.data.entity.GezogenerKartentextEntity
import de.impulse.spieleabend.data.entity.KartentextEntity
import de.impulse.spieleabend.data.entity.KategorieEntity
import de.impulse.spieleabend.data.entity.SpielEntity
import de.impulse.spieleabend.data.entity.SpielEinstellungEntity
import de.impulse.spieleabend.data.entity.SpielZiehEinstellungEntity
import de.impulse.spieleabend.data.entity.TranslationEntity
import de.impulse.spieleabend.data.mapper.toDomain
import de.impulse.spieleabend.domain.model.BearbeiteteKartentexteModus
import de.impulse.spieleabend.domain.model.CardHistoryState
import de.impulse.spieleabend.domain.model.FavoritenModus
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.GeloeschteKartentexteModus
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
        resetSeenCardTextIds: Set<Int>,
        resetSeenAndPlayedCardTextIds: Set<Int>,
        card: GezogeneKarte,
    ): CardHistoryState =
        database.withTransaction {
            val kartentextDao = database.kartentextDao()
            val seenOnlyResetCardTextIds = resetSeenCardTextIds - resetSeenAndPlayedCardTextIds

            if (resetSeenAndPlayedCardTextIds.isNotEmpty()) {
                kartentextDao.resetGesehenUndGespieltForKartentexte(
                    kartentextIds = resetSeenAndPlayedCardTextIds.toList(),
                )
            }

            if (seenOnlyResetCardTextIds.isNotEmpty()) {
                kartentextDao.resetGesehenForKartentexte(
                    kartentextIds = seenOnlyResetCardTextIds.toList(),
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

    override suspend fun setCardTextDeletedState(
        cardTextId: Int,
        deleted: Boolean,
    ) {
        database.withTransaction {
            require(database.kartentextDao().updateGeloescht(cardTextId, deleted) == 1) {
                "Der Kartentext $cardTextId fehlt in der Datenbank."
            }
        }
    }

    override suspend fun setCardTextFavoriteState(
        cardTextId: Int,
        favorite: Boolean,
    ) {
        database.withTransaction {
            require(database.kartentextDao().updateFavorit(cardTextId, favorite) == 1) {
                "Der Kartentext $cardTextId fehlt in der Datenbank."
            }
        }
    }

    override suspend fun setCustomCardTextTranslation(
        cardTextId: Int,
        language: Sprache,
        text: String?,
    ) {
        require(language.auswaehlbar) { "$language kann nicht bearbeitet werden." }
        require(text == null || text.isNotBlank()) { "Ein eigener Kartentext darf nicht leer sein." }

        database.withTransaction {
            requireNotNull(database.kartentextDao().kartentext(cardTextId)) {
                "Der Kartentext $cardTextId fehlt in der Datenbank."
            }
            val customLanguage = language.eigeneSprache()
            if (text == null) {
                database.lokalisierungDao().deleteTranslation(cardTextId, customLanguage)
            } else {
                database.lokalisierungDao().upsertTranslation(
                    TranslationEntity(
                        lokalisierungId = cardTextId,
                        sprache = customLanguage,
                        text = text,
                        bearbeitet = true,
                    ),
                )
            }
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

    override suspend fun setDeletedCardTextsMode(
        gameId: Int,
        mode: GeloeschteKartentexteModus,
    ) {
        updateDrawSettings(gameId) { settings ->
            settings.copy(geloeschteKartentexteModus = mode)
        }
    }

    override suspend fun setFavoritesMode(
        gameId: Int,
        mode: FavoritenModus,
    ) {
        updateDrawSettings(gameId) { settings ->
            settings.copy(favoritenModus = mode)
        }
    }

    override suspend fun setEditedCardTextsMode(
        gameId: Int,
        mode: BearbeiteteKartentexteModus,
    ) {
        updateDrawSettings(gameId) { settings ->
            settings.copy(bearbeiteteKartentexteModus = mode)
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
        val drawSettings = database.spielZiehEinstellungDao()
            .einstellung(lokalisierungId)
            ?: SpielZiehEinstellungEntity(spielId = lokalisierungId)

        return toDomain(
            lokalisierung = lokalisierung(lokalisierungId),
            originaleKategorien = kategorien.originale,
            hinzugefuegteKategorien = kategorien.hinzugefuegte,
            inaktiveKategorien = kategorien.inaktive,
            texteProKarteOverride = texteProKarteOverride,
            geloeschteKartentexteModus = drawSettings.geloeschteKartentexteModus,
            favoritenModus = drawSettings.favoritenModus,
            bearbeiteteKartentexteModus = drawSettings.bearbeiteteKartentexteModus,
        )
    }

    private suspend fun updateDrawSettings(
        gameId: Int,
        transform: (SpielZiehEinstellungEntity) -> SpielZiehEinstellungEntity,
    ) {
        database.withTransaction {
            spielEntity(gameId)
            val dao = database.spielZiehEinstellungDao()
            val current = dao.einstellung(gameId) ?: SpielZiehEinstellungEntity(spielId = gameId)
            dao.upsert(transform(current))
        }
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
