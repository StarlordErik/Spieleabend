package de.impulse.spieleabend.data

import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation
import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor() : GameRepository {
    override fun getGame(gameId: String): Spiel =
        placeholderGames[gameId] ?: requireNotNull(placeholderGames[DEFAULT_GAME_ID])

    private companion object {
        const val DEFAULT_GAME_ID = "placeholder-1"

        val categories = listOf(
            kategorie(
                id = "wissen",
                de = "Wissen",
                en = "Trivia",
                textDe = listOf(
                    "Nenne eine Hauptstadt in Europa.",
                    "Was ist groesser: Mond oder Merkur?",
                    "Welche Farbe entsteht aus Blau und Gelb?",
                    "Nenne ein Tier, das Eier legt.",
                    "Welche Jahreszeit folgt auf den Sommer?",
                    "Wie viele Minuten hat eine Stunde?",
                ),
                textEn = listOf(
                    "Name a capital city in Europe.",
                    "Which is larger: the Moon or Mercury?",
                    "Which color is made from blue and yellow?",
                    "Name an animal that lays eggs.",
                    "Which season comes after summer?",
                    "How many minutes are in an hour?",
                ),
            ),
            kategorie(
                id = "musik",
                de = "Musik",
                en = "Music",
                textDe = listOf(
                    "Summe einen Song aus den 90ern.",
                    "Nenne eine Band mit genau vier Mitgliedern.",
                    "Klatsche einen einfachen Rhythmus vor.",
                    "Nenne ein Instrument mit Saiten.",
                    "Singe eine Zeile ohne den Titel zu verraten.",
                    "Erfinde einen Bandnamen fuer den Tisch.",
                ),
                textEn = listOf(
                    "Hum a song from the 90s.",
                    "Name a band with exactly four members.",
                    "Clap a simple rhythm.",
                    "Name a string instrument.",
                    "Sing one line without revealing the title.",
                    "Invent a band name for the table.",
                ),
            ),
            kategorie(
                id = "film",
                de = "Film",
                en = "Movies",
                textDe = listOf(
                    "Beschreibe einen Film nur mit drei Worten.",
                    "Spiele eine beruehmte Filmszene stumm nach.",
                    "Nenne einen Film mit einer Reise.",
                    "Erfinde einen schlechten Filmtitel.",
                    "Nenne eine Figur mit Hut.",
                    "Beschreibe ein Filmende ohne Namen zu nennen.",
                ),
                textEn = listOf(
                    "Describe a movie using only three words.",
                    "Act out a famous movie scene silently.",
                    "Name a movie involving a journey.",
                    "Invent a bad movie title.",
                    "Name a character with a hat.",
                    "Describe a movie ending without names.",
                ),
            ),
            kategorie(
                id = "aktion",
                de = "Aktion",
                en = "Action",
                textDe = listOf(
                    "Stelle eine Statue fuer zehn Sekunden dar.",
                    "Zeige einen Jubel ohne Worte.",
                    "Mache eine Zeitlupenbewegung.",
                    "Tu so, als wuerdest du balancieren.",
                    "Spiele einen Roboter beim Aufraeumen.",
                    "Erfinde eine geheime Team-Geste.",
                ),
                textEn = listOf(
                    "Pose as a statue for ten seconds.",
                    "Show a celebration without words.",
                    "Move in slow motion.",
                    "Pretend to balance on something.",
                    "Act like a robot cleaning up.",
                    "Invent a secret team gesture.",
                ),
            ),
            kategorie(
                id = "kreativ",
                de = "Kreativ",
                en = "Creative",
                textDe = listOf(
                    "Erfinde einen Werbeslogan fuer Wasser.",
                    "Male mit dem Finger eine unsichtbare Form.",
                    "Erzaehle einen Satz wie ein Nachrichtensprecher.",
                    "Gib einem Alltagsgegenstand einen Namen.",
                    "Baue einen Reim auf Licht.",
                    "Beschreibe den Abend als Wetterbericht.",
                ),
                textEn = listOf(
                    "Invent an ad slogan for water.",
                    "Draw an invisible shape with your finger.",
                    "Say one sentence like a news anchor.",
                    "Give an everyday object a name.",
                    "Make a rhyme with light.",
                    "Describe the evening as a weather report.",
                ),
            ),
        )

        val placeholderGames = (1..4)
            .map { gameNumber ->
                spiel(
                    id = "placeholder-$gameNumber",
                    nameDe = "Spiel $gameNumber",
                    nameEn = "Game $gameNumber",
                    kartentexteProKarte = gameNumber,
                    kategorien = categories.take(gameNumber + 1).toCollection(LinkedHashSet()),
                )
            }
            .associateBy { spiel -> spiel.id }

        fun spiel(
            id: String,
            nameDe: String,
            nameEn: String,
            kartentexteProKarte: Int,
            kategorien: Set<Kategorie>,
        ): Spiel =
            Spiel(
                id = id,
                lokalisierung = lokalisierung(id = "$id-name", de = nameDe, en = nameEn),
                kategorien = kategorien,
                kartentexteProKarte = kartentexteProKarte,
            )

        fun kategorie(
            id: String,
            de: String,
            en: String,
            textDe: List<String>,
            textEn: List<String>,
        ): Kategorie {
            require(textDe.size == CATEGORY_TEXT_COUNT)
            require(textEn.size == CATEGORY_TEXT_COUNT)

            return Kategorie(
                id = id,
                lokalisierung = lokalisierung(id = "$id-name", de = de, en = en),
                kartentexte = textDe
                    .zip(textEn)
                    .mapIndexed { index, translations ->
                        kartentext(
                            id = "$id-${index + 1}",
                            de = translations.first,
                            en = translations.second,
                        )
                    }
                    .toCollection(LinkedHashSet()),
            )
        }

        fun kartentext(
            id: String,
            de: String,
            en: String,
        ): Kartentext =
            Kartentext(
                id = id,
                lokalisierung = lokalisierung(id = "$id-text", de = de, en = en),
            )

        fun lokalisierung(
            id: String,
            de: String,
            en: String,
        ): Lokalisierung =
            Lokalisierung(
                id = id,
                translationen = linkedSetOf(
                    Translation(sprachCode = "de", text = de),
                    Translation(sprachCode = "en", text = en),
                ),
            )

        const val CATEGORY_TEXT_COUNT = 6
    }
}
