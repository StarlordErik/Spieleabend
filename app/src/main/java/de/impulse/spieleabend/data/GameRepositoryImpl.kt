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

        val placeholderGames = listOf(
            kneipenquiz(),
            pantomimeDuell(),
            wortsalat(),
            mutprobe(),
        ).associateBy { spiel -> spiel.id }

        fun kneipenquiz(): Spiel {
            val frage = kartentext(
                id = "frage",
                de = "Welche Stadt trägt den Spitznamen Big Apple?",
                en = "Which city is known as the Big Apple?",
            )
            val hinweis = kartentext(
                id = "hinweis",
                de = "Tipp: Sie liegt an der Ostküste.",
                en = "Hint: It is on the East Coast.",
            )
            val joker = kartentext(
                id = "joker",
                de = "Ein Team darf eine Zusatzfrage stellen.",
                en = "One team may ask a bonus question.",
            )

            return spiel(
                id = "placeholder-1",
                nameDe = "Kneipenquiz",
                nameEn = "Pub Quiz",
                kategorien = linkedSetOf(
                    kategorie("wissen", "Wissen", "Trivia", frage, hinweis),
                    kategorie("musik", "Musik", "Music", joker),
                    kategorie("film", "Film", "Movies", frage),
                    kategorie("finale", "Finale", "Finale", frage, hinweis, joker),
                ),
            )
        }

        fun pantomimeDuell(): Spiel {
            val aktion = kartentext(
                id = "aktion",
                de = "Stelle einen Roboter beim ersten Date dar.",
                en = "Act out a robot on a first date.",
            )
            val twist = kartentext(
                id = "twist",
                de = "Du darfst dabei nur Zeitlupe verwenden.",
                en = "You may only move in slow motion.",
            )

            return spiel(
                id = "placeholder-2",
                nameDe = "Pantomime Duell",
                nameEn = "Charades Duel",
                kategorien = linkedSetOf(
                    kategorie("begriffe", "Begriffe", "Prompts", aktion),
                    kategorie("tempo", "Tempo", "Speed", twist),
                    kategorie("team", "Team", "Team", aktion, twist),
                ),
            )
        }

        fun wortsalat(): Spiel {
            val wort = kartentext("wort", "Sommerregen", "Summer rain")
            val auftrag = kartentext(
                id = "auftrag",
                de = "Erkläre das Wort ohne die Buchstaben S und R.",
                en = "Explain it without using the letters S and R.",
            )
            val bonus = kartentext(
                id = "bonus",
                de = "Baue einen Reim auf Licht ein.",
                en = "Include a rhyme with light.",
            )
            val zeit = kartentext("zeit", "45 Sekunden", "45 seconds")

            return spiel(
                id = "placeholder-3",
                nameDe = "Wortsalat",
                nameEn = "Word Toss",
                kategorien = linkedSetOf(
                    kategorie("start", "Start", "Start", wort, auftrag),
                    kategorie("reim", "Reim", "Rhyme", bonus),
                    kategorie("verboten", "Verboten", "Banned", auftrag),
                    kategorie("bonus", "Bonus", "Bonus", bonus, zeit),
                ),
            )
        }

        fun mutprobe(): Spiel {
            val probe = kartentext(
                id = "probe",
                de = "Erfinde einen Toast auf den nächsten Würfelwurf.",
                en = "Invent a toast to the next dice roll.",
            )
            val regel = kartentext(
                id = "regel",
                de = "Alle am Tisch müssen ein Geräusch beisteuern.",
                en = "Everyone at the table adds a sound.",
            )
            val abschluss = kartentext(
                id = "abschluss",
                de = "Der kürzeste Beitrag gewinnt.",
                en = "The shortest contribution wins.",
            )

            return spiel(
                id = "placeholder-4",
                nameDe = "Mutprobe",
                nameEn = "Dare Deck",
                kategorien = linkedSetOf(
                    kategorie("leicht", "Leicht", "Easy", probe),
                    kategorie("chaos", "Chaos", "Chaos", regel),
                    kategorie("kreativ", "Kreativ", "Creative", abschluss, probe),
                ),
            )
        }

        fun spiel(
            id: String,
            nameDe: String,
            nameEn: String,
            kategorien: Set<Kategorie>,
        ): Spiel =
            Spiel(
                id = id,
                lokalisierung = lokalisierung(id = "$id-name", de = nameDe, en = nameEn),
                kategorien = kategorien,
            )

        fun kategorie(
            id: String,
            de: String,
            en: String,
            vararg kartentexte: Kartentext,
        ): Kategorie =
            Kategorie(
                id = id,
                lokalisierung = lokalisierung(id = "$id-name", de = de, en = en),
                kartentexte = kartentexte.toCollection(LinkedHashSet()),
            )

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
    }
}
