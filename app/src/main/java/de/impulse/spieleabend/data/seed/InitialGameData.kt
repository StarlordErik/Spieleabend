@file:Suppress("LargeClass", "LongMethod", "MagicNumber", "MaxLineLength")

package de.impulse.spieleabend.data.seed

import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Lokalisierung
import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.model.Translation

internal object InitialGameData {
    const val DEFAULT_GAME_ID = "erzaehlt-euch-mehr"

    val spiele: List<Spiel> = listOf(
        spiel(
            id = "erzaehlt-euch-mehr",
            name = "Erz\u00E4hlt euch mehr",
            kartentexteProKarte = 1,
            kategorien = linkedSetOf(
                kategorie(
                    id = "erzaehlt-euch-mehr-gedankenspiel",
                    name = "Gedankenspiel",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du kannst einem Menschen auf dieser Welt einen Herzenswunsch erf\u00FCllen.\n\nWem erf\u00FCllst du welchen Wunsch?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du kannst auf eine pers\u00F6nliche Hygieneroutine verzichten, ohne dass es sich negativ auswirkt.\n\nWelche Routine w\u00E4hlst du aus?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Du kannst auf eine pers\u00F6nliche Hygieneroutine verzichten, ohne, dass es sich negativ auswirkt.\n\nWelche Routine w\u00E4hlst du aus?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du kannst einen Tag in deinem Leben nochmal leben.\n\nWelchen Tag w\u00E4hlst du aus?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du bekommst die M\u00F6glichkeit, (noch) einmal zu studieren.\n\nF\u00FCr welchen Studiengang entscheidest du dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du kannst f\u00FCr einen Tag die Darstellung des Google-Logos bestimmen.\n\nWie w\u00FCrde es aussehen?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Du kannst f\u00FCr einen Tag die Darstellung des Google Logos bestimmen.\n\nWie w\u00FCrde es aussehen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Die Biografie welches deiner Verwandten w\u00FCrdest du am liebsten lesen und warum?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du kannst der weltweit f\u00FChrende Experte* in einer bestimmten Nische sein.\n\nWelche Nische w\u00E4hlst du?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Du kannst der weltweit f\u00FChrende Experte in einer bestimmten Nische sein.\n\nWelche Nische w\u00E4hlst du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Von all deinen Besitzt\u00FCmern darfst du lediglich 5 behalten.\n\nF\u00FCr welche entscheidest du dich?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Von all deinen Besitzt\u00FCmern darfst du lediglich f\u00FCnf behalten.\n\nF\u00FCr welche entscheidest du dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du hast eine 5-min\u00FCtige Audienz beim Papst.\n\nWor\u00FCber sprichst du mit ihm?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Du hast eine f\u00FCnfmin\u00FCtige Audienz beim Papst.\n\nWor\u00FCber sprichst du mit ihm?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du bist sowohl geografisch als auch finanziell unabh\u00E4ngig.\n\nWie und wo lebst du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du findest heraus, dass dein gesamtes Leben ein einziger Traum ist. Du kannst jetzt entscheiden, ob du aufwachst oder in deinem Traum weiterlebst.\n\nWas tust du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du kannst genau einen Umstand an der Art, wie du aufgewachsen bist, \u00E4ndern.\n\nWof\u00FCr entscheidest du dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du kannst deine t\u00E4gliche Schlafzeit problemlos halbieren.\n\nWof\u00FCr nutzt du die neu gewonnene Zeit?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du bist Bildungsminister* und erarbeitest die Schulinhalte f\u00FCr die n\u00E4chsten Jahre.\n\nWas muss zwingend auf die Lehrpl\u00E4ne?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Du bist Bildungsminister und erarbeitest die Schulinhalte f\u00FCr die n\u00E4chsten Jahre.\n\nWas muss zwingend auf die Lehrpl\u00E4ne?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-015",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du wirst f\u00FCr eine Woche lang mit einer Superkraft deiner Wahl ausgestattet.\n\nF\u00FCr welche Superkraft entscheidest du dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-016",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du bekommst 100.000\u20AC, um einen Raum in deinem bestehenden Zuhause nach deinen W\u00FCnschen umzugestalten.\n\nWelchen Raum w\u00E4hlst du und was \u00E4nderst du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-017",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du bist mit einem seltenen Virus infiziert. \u00C4rzte* teilen dir mit, dass du nur noch eine Woche zu leben hast. Du bist mobil und frei beweglich.\n\nWie gestaltest du deine letzte Woche?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Du bist mit einem seltenen Virus infiziert. \u00C4rzte teilen dir mit, dass du nur noch eine Woche zu leben hast. Du bist mobil und frei beweglich.\n\nWie gestaltest du deine letzte Woche?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-018",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du reist in die Vergangenheit und hast ein 5-min\u00FCtiges Gespr\u00E4ch mit deinem 10-j\u00E4hrigen Ich.\n\nWas erz\u00E4hlst du deinem j\u00FCngeren Ich?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Du reist in die Vergangenheit und hast ein f\u00FCnfmin\u00FCtiges Gespr\u00E4ch mit deinem zehnj\u00E4hrigen Ich.\n\nWas erz\u00E4hlst du ihm?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-019",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du hast einen Freifahrtschein in der Sch\u00F6nheitsklinik. W\u00FCrdest du etwas \u00E4ndern lassen?\n\nFalls ja, was?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-020",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Du wirst nach deinem Ableben einmalig wiedergeboren und kannst komplett frei bestimmen als was.\n\nWof\u00FCr entscheidest du dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-gedankenspiel-021",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Unter s\u00E4mtlichen Menschen dieser Welt darfst du 3 zum Abendessen einladen. Sie werden sicher erscheinen.\n\nWen l\u00E4dst du ein?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Unter s\u00E4mtlichen Menschen dieser Welt darfst du drei zum Abendessen einladen. Sie werden sicher erscheinen.\n\nWen l\u00E4dst du ein?",
                                ),
                            ),
                        ),
                    ),
                ),
                kategorie(
                    id = "erzaehlt-euch-mehr-kreuzverhoer",
                    name = "Kreuzverh\u00F6r",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "W\u00FCrdest du lieber ...\n\n ... das Weltall oder den Ozean erkunden?\n ... den ganzen Tag lang Anzug oder Jogginganzug tragen?\n ... alle Sprachen dieser Welt oder alle Instrumente dieser Welt beherrschen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wann bevorzugst du ...\n\n ... Podcast- oder H\u00F6rbuch-H\u00F6ren?\n ... Online Shopping oder in Gesch\u00E4fte Gehen?\n ... Nachrichten-Schreiben oder Telefonieren?\n ... Sport-Machen oder Sport-Gucken?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Bevorzugst du ...\n\n ... Podcast oder H\u00F6rbuch h\u00F6ren?\n ... Online Shopping oder in Gesch\u00E4fte gehen?\n ... Nachrichten schreiben oder telefonieren?\n ... Sport machen oder Sport gucken?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie informierst du dich? Nenne jeweils 3 ...\n\n ... Websites\n ... Printerzeugnisse\n ... TV Formate\n ... Podcasts",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Teile je Stichwort eine Kindheitserinnerung:\n\n- S\u00FC\u00DFigkeit\n- TV Serie\n- Gesellschaftsspiel\n- bester Freund*",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Teile je Stichwort eine Kindheitserinnerung:\n\n-S\u00FC\u00DFigkeit\n-TV Serie\n-Gesellschaftsspiel\n-beste(r) Freund(in)",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Dein Lieblingsgetr\u00E4nk ...\n\n ... beim Fr\u00FChst\u00FCck?\n ... im Kino?\n ... im Club?\n ... auf der Arbeit? (oder Schule/Uni)",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Dein Lieblingsgetr\u00E4nk ...\n\n ... beim Fr\u00FChst\u00FCck?\n ... im Kino?\n ... im Club?\n ... auf der Arbeit?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Bist du eher ...\n\n ... ein Fluss,\n ... ein See,\n ... ein Meer oder\n ... ein Wasserfall?\n\nWarum?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Verzichtest du eher auf ...\n\n ... Kaffee oder Alkohol?\n ... Fleisch oder Fisch?\n ... die F\u00E4higkeit zu schreiben oder die F\u00E4higkeit zu lesen?\n ... 1,5 Monatsgeh\u00E4lter oder deinen Jahresurlaub?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Regnerischer Spielnachmittag, aber wie?\n\n- Karten- oder Brettspiel?\n- Konsole oder PC?\n- Fifa oder Mario Kart?\n- Hei\u00DFer Kakao oder Bier?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Regnerischer Spielnachmittag, aber wie?\n\n-Karten oder Brettspiel?\n-Wii oder Playstation?\n-Fifa oder Mario Kart?\n-Hei\u00DFer Kakao oder Bier?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du hast eine eigene Minibar im Haus. Was darf nicht fehlen?\n\n- Wein oder Bier?\n- Klarer oder Kr\u00E4uterschnaps?\n- Coca-Cola oder Red Bull?\n- Schokolade oder Erdn\u00FCsse?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Du hast eine eigene Minibar im Haus. Was darf nicht fehlen?\n\n-Wein oder Bier?\n-Klarer oder Kr\u00E4uterschnaps?\n-Coca-Cola oder Red Bull?\n-Schokolade oder Erdn\u00FCsse?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-kreuzverhoer-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "- Wieviel Bargeld hast du in diesem Moment bei dir?\n\n- Was war deine letzte Anschaffung unter 100\u20AC?\n\n- Was war deine letzte Anschaffung \u00FCber 100\u20AC?\n\n- Wof\u00FCr sparst du gerade?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Wieviel Bargeld hast du in diesem Moment bei dir?\n\nWas war deine letzte Anschaffung < 100\u20AC?\n\nWas war deine letzte Anschaffung > 100\u20AC?\n\nWof\u00FCr sparst du gerade?",
                                ),
                            ),
                        ),
                    ),
                ),
                kategorie(
                    id = "erzaehlt-euch-mehr-selbstreflexion",
                    name = "Selbstreflexion",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Worauf freust du dich momentan ganz besonders?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was war dein pers\u00F6nlich gr\u00F6\u00DFter Erfolg in deinem Leben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wof\u00FCr bewunderst du andere Menschen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wo m\u00F6chtest du im Leben stehen, wenn du deinen n\u00E4chsten runden Geburtstag erreichst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was war dein Berufswunsch als Kind und wie denkst du heute dar\u00FCber?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Gibt es etwas, woran du gerade voller Passion arbeitest?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was ist dein Lieblingsgeruch und was verbindest du mit ihm?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was hast du irgendwann einmal getan, wof\u00FCr du dich heute ernsthaft sch\u00E4mst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wor\u00FCber hast du das letzte Mal so sehr gelacht, dass deine Augen anfingen zu tr\u00E4nen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was ist, deiner Meinung nach, der gr\u00F6\u00DFte Unterschied zwischen uns beiden?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welche Entscheidung, die du getroffen hast, hat dein Leben am st\u00E4rksten beeinflusst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welche Eigenschaft anderer Menschen macht dich wahnsinnig?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wof\u00FCr gibst du gerne Geld aus und schaust auch nicht zwingend auf das Preisschild?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wann und in welcher Situation hast du das letzte Mal jemanden oder etwas aufgegeben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-015",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was tust du, um dich selbst gl\u00FCcklich zu machen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-016",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was tust du regelm\u00E4\u00DFig und immer wieder, obwohl es dir absolut nicht gef\u00E4llt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-017",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was ist momentan deine gr\u00F6\u00DFte Herausforderung?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-018",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welcher Mensch hat dich besonders inspiriert und warum?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-019",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was ist das Beste daran, du zu sein?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-020",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was tust du, um andere Menschen gl\u00FCcklich zu machen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-021",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was ist dein wichtigstes Ziel f\u00FCr die n\u00E4chsten 6 Monate?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-022",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was haben wir deiner Meinung nach gemeinsam?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-023",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Warst du jemals sehr beunruhigt oder \u00E4ngstlich wegen einer Sache, die sich im Nachgang als halb so wild entpuppte?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-024",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welches Kompliment ist dir besonders in Erinnerung geblieben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-025",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Beschreibe, wie ein Buch und ein Film dich besonders beeinflusst haben.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-026",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wann hast du das letzte Mal in Gegenwart einer anderen Person geweint und warum?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-027",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welches Ereignis oder welche Person hat zuletzt deine Sichtweise auf ein bestimmtes Thema signifikant ge\u00E4ndert?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-028",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was war das Hilfreichste, das du in letzter Zeit gelernt hast?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-029",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie sieht dein perfekter Sonntagabend aus?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-030",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was ist deine aktuell gr\u00F6\u00DFte Sorge?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-031",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was war dein stolzester Moment in den letzten 12 Monaten?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-032",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was verbessert deine Laune schlagartig?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-033",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Beschreibe deine Morgenroutine.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-034",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Was bedeutet es f\u00FCr dich, ein gl\u00FCckliches Leben zu f\u00FChren?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Was bedeutet es f\u00FCr dich ein gl\u00FCckliches Leben zu f\u00FChren?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-035",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was denken viele Leute \u00FCber dich, ist deiner Meinung nach aber nicht zutreffend?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-036",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Erkl\u00E4re den Einfluss deiner Kinderstube an der Art, wie du ...\n... Urlaub machst.\n... Weihnachten feierst.\n... in den Tag startest.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-037",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wann hast du dir das letzte Mal einen Rat oder eine Meinung eingeholt?\n\nZu wem gehst du in solchen F\u00E4llen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-038",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wann war dein letzter richtig mieser Tag?\n\nWas ist geschehen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-selbstreflexion-039",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Gibt es etwas, von dem du schon lange tr\u00E4umst, es zu tun?\n\nWas hielt dich bisher davon ab, es zu tun?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Gibt es etwas, von dem du schon lange tr\u00E4umst es zu tun?\n\nWas hielt dich bisher davon ab, es zu tun?",
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ),
        spiel(
            id = "erzaehlt-euch-mehr-fuer-paare",
            name = "Erz\u00E4hlt euch mehr - f\u00FCr Paare",
            kartentexteProKarte = 1,
            kategorien = linkedSetOf(
                kategorie(
                    id = "erzaehlt-euch-mehr-fuer-paare-erinnerung",
                    name = "Erinnerung",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie und wo haben wir uns kennengelernt?\n\nWelche Erinnerungen verbindest du damit?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Was ist, deiner Meinung nach, unser sch\u00F6nster, gemeinsamer Moment der letzten 6 Monate?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Was ist, deiner Meinung nach, unser sch\u00F6nster gemeinsamer Moment der letzten 6 Monate?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Mit welcher gemeinsamen Reise oder welchem Ort, den wir zusammen besucht haben, verbindest du besondere Erinnerungen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was ist f\u00FCr dich der witzigste Moment, den wir gemeinsam erlebt haben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "An welche gemeinsame Anschaffung denkst du gerne zur\u00FCck und warum?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Gibt es einen Film, der dich immer an mich oder uns als Paar erinnert?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was hat dich an dieser Beziehung am meisten \u00FCberrascht?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was waren, deiner Meinung nach, Hoch- und Tiefpunkt unserer Beziehung?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "An welchem Jahreswechsel mit mir denkst du besonders gerne?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was war das Kitschigste, das ich je f\u00FCr dich gemacht habe?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was war besonders sch\u00F6n und was war besonders schwierig, als wir zusammengezogen sind?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Erz\u00E4hl mir von einer Herausforderung, die wir gemeinsam gemeistert haben.\n\nWie hast du dich in dieser Situation gef\u00FChlt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie hat dein Umfeld auf mich, als deinen neuen Partner*, reagiert und wie hast du dich dabei gef\u00FChlt?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie hat dein Umfeld auf mich, als deinen neuen Partner, reagiert und wie hast du dich dabei gef\u00FChlt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "\u00DCber welches meiner Geschenke hast du dich am meisten gefreut?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-015",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "An welchem Punkt hast du realisiert, dass du in mich verliebt bist?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-016",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wann konnten wir uns vor Lachen kaum noch halten?\n\nWarum?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-017",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Gibt es ein Lied, das dich immer an mich als Person oder uns als Paar erinnert?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-018",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Erinnerst du dich an unseren ersten richtig gro\u00DFen Streit?\n\nWorum ging es?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-019",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was haben wir in unserer Kennenlernphase unternommen und an welches Date erinnerst du dich noch ganz besonders?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-erinnerung-020",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie empfandest du unser erstes \"Ich liebe dich\"?",
                                ),
                            ),
                        ),
                    ),
                ),
                kategorie(
                    id = "erzaehlt-euch-mehr-fuer-paare-kompliment",
                    name = "Kompliment",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welche kleine Angewohnheit von mir zaubert dir regelm\u00E4\u00DFig ein L\u00E4cheln auf die Lippen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "In welchem Outfit gefalle ich dir besonders gut?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was magst du an meinem Tanzstil?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was haben wir zusammen gemeistert, was du alleine nie geschafft h\u00E4ttest?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welche meiner Errungenschaften oder Leistungen hat dich stolz gemacht?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was macht mich so einzigartig f\u00FCr dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wof\u00FCr bist du mir dankbar?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie habe ich dir das letzte Mal helfen k\u00F6nnen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was von dem, was ich tue, macht dich am meisten an?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was hast du von mir lernen k\u00F6nnen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welche meiner Eigenschaften gef\u00E4llt dir am besten?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was an meinem K\u00F6rper gef\u00E4llt dir besonders?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was kann ich besonders gut?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-kompliment-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "In welchem Punkt habe ich dein Denken ver\u00E4ndert?",
                                ),
                            ),
                        ),
                    ),
                ),
                kategorie(
                    id = "erzaehlt-euch-mehr-fuer-paare-reflexion",
                    name = "Reflexion",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welchen Aspekt unserer Beziehung m\u00F6chtest du anderen Paaren gerne zur Nachahmung empfehlen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was m\u00F6chtest du in unserer Beziehung gerne anders machen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie habe ich mich, in deinen Augen, im Laufe unserer Beziehung ver\u00E4ndert?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Findest du unseren Umgang mit Geld ausgeglichen oder muss einer \u00F6fter in die Tasche greifen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "\u00DCber welches Thema sprichst du nur ungern mit mir?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was tust du ganz aktiv f\u00FCr diese Beziehung?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was hast du getan, was sich als nicht gut f\u00FCr unsere Beziehung herausgestellt hat?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was in unserer Beziehung macht dir ein ungutes Bauchgef\u00FChl?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was glaubst du ist unsere gr\u00F6\u00DFte gemeinsame Schw\u00E4che?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was unterscheidet unsere Beziehung von deinen vorherigen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Gibt es etwas in unserer gemeinsamen Zeit, das du bereust?",
                                ),
                                Translation(
                                    sprachCode = "og",
                                    text = "Gibt es etwas in unserer gemeinamen Zeit, das du bereust?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Beschreibe, wie es ist, mit mir in einer Beziehung zu sein.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "In welchen gemeinsamen Augenblicken bist du besonders gl\u00FCcklich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was ist deine Lieblingsbesch\u00E4ftigung f\u00FCr uns beide?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-015",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wieviele Tage ohne Sex sind zu viel f\u00FCr dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-016",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welche Erwartungen hast du an unsere Beziehung?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-017",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Vertraust du mir immer deine innersten Gedanken an?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-018",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Hast du sexuelle Fantasien, denen wir bisher keine Aufmerksamkeit geschenkt haben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-019",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was glaubst du ist unsere gr\u00F6\u00DFte gemeinsame St\u00E4rke?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-020",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Worin bin ich meiner Mutter und worin meinem Vater \u00E4hnlich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-021",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie stellst du dir unser Leben in 5 Jahren vor?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-022",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie m\u00F6chtest du, dass ich dir meine Liebe zeige?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-023",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Geben wir unserer Liebe im Alltag genug Ausdruck?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-024",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie gef\u00E4llt dir der Kontakt zu unseren Familien?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-025",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was macht uns als Paar kompatibel?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-026",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was w\u00FCnschst du dir von mir f\u00FCr unsere Zukunft?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-027",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was w\u00FCnschst du dir f\u00FCr unsere Kommunikation?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-028",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie sieht deine Vorstellung von einem perfekten Date aus?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-029",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wieviel Raum brauchst du f\u00FCr dich allein?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-030",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was tue ich, das dich verletzt?\n\nWarum verletzt es dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-031",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Gibt es etwas, das du von mir brauchst, aber derzeit nicht bekommst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-032",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie zeigst du mir deine Liebe?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-033",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Welche Angewohnheiten von mir empfindest du als st\u00F6rend?\n\nAus welchen Gr\u00FCnden ist das so?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-034",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "In welcher Situation war ich dir peinlich?\n\nWarum?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-035",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Wie hast du die Beziehung deiner Eltern wahrgenommen?\n\nHaben sie etwas besonders richtig oder falsch gemacht?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "erzaehlt-euch-mehr-fuer-paare-reflexion-036",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "og",
                                    text = "Was sollte ich niemals zu dir sagen? - egal, wie w\u00FCtend ich bin.",
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ),
        spiel(
            id = "fun-facts",
            name = "Fun Facts",
            kartentexteProKarte = 1,
            kategorien = linkedSetOf(
                kategorie(
                    id = "fun-facts-freitextfragen",
                    name = "Freitextfragen",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "fun-facts-freitextfragen-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viel Eintritt w\u00FCrdest du h\u00F6chstens f\u00FCr einen Freizeitpark mit echten, lebenden Dinosauriern bezahlen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Zimmer hat dein Zuhause?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Freunde hast du auf Facebook?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gro\u00DF w\u00E4rst du, wenn du die Wahl h\u00E4ttest?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie teuer war das teuerste Objekt, dass du jemals kaputt gemacht hast?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Stunden schl\u00E4fst du idealerweise nachts?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Sportarten hast du schon mindestens ein Jahr lang betrieben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Vornamen hast du (also auch zweite, dritte ...)?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wann wachst du durchschnittlich am Wochenende auf?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Flaschen mit hochprozentigen Spirituosen besitzt du zurzeit?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Minuten nach dem ersten Klingeln des Weckers stehst du durchschnittlich endg\u00FCltig auf?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie alt f\u00FChlst du dich innerlich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Gl\u00E4ser alkoholischer Getr\u00E4nke trinkst du durchschnittlich pro Woche?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft hast du den Film \"Titanic\" gesehen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-015",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie lange ist dein perfektes Nickerchen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-016",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wenn es keinerlei Einschr\u00E4nkungen g\u00E4be, wie viele Haustiere h\u00E4ttest du dann gerne?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-017",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft hast du in deinem Leben schon Reifen gewechselt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-018",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele verschiedene Parf\u00FCms besitzt du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-019",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele der Personen hier am Tisch w\u00FCrdest du beim Armdr\u00FCcken besiegen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-020",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viel w\u00E4rst du maximal bereit, in einem Restaurant der Extraklasse zu bezahlen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-021",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Jahre deines Lebens w\u00FCrdest du hergeben, um dich ab sofort beliebig auf teleportieren zu k\u00F6nnen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-022",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie lange dauert oder dauert deine l\u00E4ngste Freundschaft?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-023",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Stunden verbringst du bei einem Tagesausflug an die K\u00FCste idealerweise direkt am Strand?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-024",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "In wie vielen Sprachen kennst du Schimpfw\u00F6rter?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-025",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Tattoos hast du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-026",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Welche Schuhgr\u00F6\u00DFe hast du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-027",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Was denkst du, wie alt du wirst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-028",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Bei durchschnittlich wie vielen Mahlzeiten pro Woche isst du Fleisch?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-029",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Apps hast du auf deinem Smartphone installiert?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-030",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Paar Schuhe besitzt du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-031",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele deiner bisherigen romantischen Beziehungen haben l\u00E4nger als ein Jahr gedauert?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-032",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Schl\u00FCssel hast du an deinem Schl\u00FCsselring?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-033",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Um wie viel Uhr gehst du normalerweise unter der Woche ins Bett?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-034",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viel w\u00FCrdest du h\u00F6chstens f\u00FCr ein Konzert deiner Lieblingsband bei dir zu Hause bezahlen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-035",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie lange brauchst du, um zur Arbeit oder Schule zu gehen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-036",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Jahre deines Lebens w\u00FCrdest du hergeben, um jetzt sofort zum Milliard\u00E4r zu werden?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-037",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Quadratmeter h\u00E4tte dein ideales Zuhause (ohne Garten)?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-038",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Jahre deines Lebens w\u00FCrdest du hergeben, um alle Sprachen der Welt perfekt zu sprechen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-039",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele H\u00FCte, Kappen, M\u00FCtzen etc. besitzt du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-040",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "F\u00FCr wie viele Sekunden k\u00F6nntest du den Atem anhalten?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-041",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wann wachst du normalerweise unter der Woche morgens auf?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-042",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele der Personen hier am Tisch k\u00F6nntest du bei Scrabble besiegen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-043",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft im Jahr w\u00FCrdest du Disneyland Paris besuchen, wenn Reise und Eintritt umsonst w\u00E4ren?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-044",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele B\u00FCcher liest du durchschnittlich in einem Jahr?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-045",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du hast eine Reise zum Mond gewonnen. Wie lange soll sie dauern?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-046",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Welches Jahr in der Weltgeschichte findest du am faszinierendsten?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-047",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "In wie vielen Sprachen kannst du \"Ich liebe dich\" sagen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-048",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Liegest\u00FCtze hintereinander k\u00F6nntest du schaffen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-049",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele der Personen hier am Tisch w\u00FCrdest du im 100-Meter-Lauf besiegen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-050",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele N\u00E4chte nacheinander hast du bisher maximal in einem Zelt \u00FCbernachtet?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-051",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Welches Jahr w\u00FCrdest du w\u00E4hlen, wenn du in der Zeit reisen und einen Tag in einem anderen Jahr verbringen k\u00F6nntest?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-052",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Cousins und Cousinen hast du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-053",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Minuten wartest du maximal an einem Restaurant auf einem freien Tisch?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-054",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viel Bargeld hast du normalerweise bei dir?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-055",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Topfpflanzen stehen bei dir zu Hause?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-056",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Kontinente hast du schon betreten?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-057",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Sekunden h\u00E4ltst du aus, ohne zu zwinkern?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-058",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft isst du durchschnittlich im Monat Fastfood?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-059",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft gehst du durchschnittlich jeden Tag ins Bad?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-060",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wenn du bis zum Lebensende immer gleich alt sein m\u00FCsstest und du das festlegen d\u00FCrftest, wie alt w\u00E4rst du dann gerne?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-061",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele M&M's Schokolinsen k\u00F6nntest du auf einmal in deinen Mund bekommen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-062",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Minuten brauchst du durchschnittlich morgens f\u00FCr dein \u00E4u\u00DFeres Erscheinungsbild?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-063",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Minuten vorher erscheinst du normalerweise vor Ort zu einem wichtigen Termin?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-064",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie lange \u00FCbst du schon dein liebstes Hobby aus?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-065",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie lange dauerte oder dauert deine l\u00E4ngste romantische Beziehung?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-066",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie lange w\u00FCrde deine ideale Mittelmeerkreuzfahrt dauern?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-067",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft im Jahr gehst du normalerweise zum Friseur?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-068",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft warst du schon Skifahren oder Snowboarden?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-069",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Jahre deines Lebens w\u00FCrdest du hergeben, um ein Musikinstrument deiner Wahl sofort perfekt spielen zu k\u00F6nnen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-070",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Sammlungen irgendwelcher Gegenst\u00E4nde hast du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-071",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Tage dauerte deine l\u00E4ngste Reise bisher?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-072",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Welches Jahrzehnt des 20. Jahrhunderts ist dir musikalisch gesehen das liebste?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-073",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Von allen Einschr\u00E4nkungen, wie viele Kinder w\u00FCrdest du am liebsten irgendwann einmal haben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-074",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Was w\u00E4re die ideale Anzahl von Urlaubstagen im Jahr f\u00FCr dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-075",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Stell dir vor, du gewinnst ein All-Inclusive-Urlaub auf einer privaten Insel. Wie lange w\u00FCrdest du dort bleiben wollen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-076",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Filme siehst du durchschnittlich im Monat?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-077",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft gehst du normalerweise im Monat essen? In ein Restaurant, also kein Fastfood oder Lieferservice?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-078",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie lang k\u00F6nntest du die Balance halten, wenn du nur auf einem Bein stehst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-079",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "In welchem Alter bist du in eine eigene Wohnung gezogen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-080",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft gehst du durchschnittlich in der Woche Lebensmittel einkaufen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-081",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft trainierst du durchschnittlich im Monat?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-082",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie alt warst du in deiner fr\u00FChesten Kindheitserinnerung?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-083",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft bist du in deinem Leben schon umgezogen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-084",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie alt warst du, als du deinen ersten regelm\u00E4\u00DFigen Job angefangen hast?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-085",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft kannst du fehlerfrei nacheinander \"Hans hackt Holz hinterm Haus\" sagen, ohne dich zu verhaspeln?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-086",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Tage mit Schnee im Jahr g\u00E4be es, wenn es nach dir ginge?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-087",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Museen hast du in den letzten zw\u00F6lf Monaten besucht?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-088",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie lange lebst du schon in deinem jetzigen Zuhause?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-089",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Minuten duschst du durchschnittlich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-090",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Tassen Kaffee und/oder Tee hast du heute schon getrunken?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-091",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wenn du in dieser Sekunde 50.000 Euro in bar bekommen w\u00FCrdest, wie lange br\u00E4uchtest du, um alles auszugeben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-092",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viel w\u00FCrdest du maximal f\u00FCr einen einw\u00F6chigen Aufenthalt im Weltall ausgeben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-093",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viele Gesellschaftsspiele besitzt du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-freitextfragen-094",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie hoch m\u00FCsste dein Jahresgehalt sein, wenn du ein Jahr lang auf einer Forschungsstation in der Antarktis arbeiten solltest?",
                                ),
                            ),
                        ),
                    ),
                ),
                kategorie(
                    id = "fun-facts-von-0-bis-100",
                    name = "von 0 bis 100",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "fun-facts-von-0-bis-100-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sch\u00FCchtern bist du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne w\u00FCrdest du Geheimagent werden wollen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne w\u00FCrdest du auf der ersten Marskolonie leben (f\u00FCr 5 Jahre, einschlie\u00DFlich der Reise)?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne kochst du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie wichtig ist dir Familie?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du das Land, in dem du aktuell lebst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gelenkig bist du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gut ist dein Ged\u00E4chtnis?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie zufrieden bist du bis jetzt mit deinem heutigen Tag?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr bist du jemand, der Dinge \u00FCberst\u00FCrzt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne gehst du zu Konzerten oder Musikfestivals?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr k\u00FCmmert es dich, was andere Menschen \u00FCber dich denken?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Musicals?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie wichtig ist dir Essen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-015",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne liest du, w\u00E4hrend du auf der Toilette sitzt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-016",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gro\u00DF ist dein Interesse, an einer Reality-Show im Fernsehen teilzunehmen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-017",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gro\u00DF ist deine H\u00F6henangst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-018",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gro\u00DF ist dein Interesse, den Papst pers\u00F6nlich zu treffen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-019",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne w\u00FCrdest du einmal Bungee springen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-020",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne tanzt du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-021",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie faul bist du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-022",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie erwachsen f\u00FChlst du dich?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-023",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne feierst du deinen Geburtstag?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-024",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du deine momentane Frisur?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-025",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du deinen Vornamen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-026",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie schick bist du heute deiner Meinung nach angezogen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-027",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne trainierst du deine Fitness?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-028",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr hast du Angst vor der Dunkelheit?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-029",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gut kannst du ohne Rezept kochen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-030",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie schlecht kannst du verlieren?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-031",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Familienzusammenk\u00FCnfte?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-032",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie schnell f\u00FChlst du dich beleidigt, angegriffen, verletzt ...?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-033",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie wichtig sind dir Traditionen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-034",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Umarmungen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-035",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viel Fantasie hast du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-036",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Tiere?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-037",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gern w\u00FCrdest du einmal Fallschirmspringen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-038",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr bist du jemand, der Dinge kurz vor knapp in letzter Minute erledigt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-039",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Disney-Filme?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-040",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne erstellst du Listen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-041",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie abergl\u00E4ubisch bist du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-042",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gro\u00DF ist deine Angst vor Spinnen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-043",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gern w\u00E4rst du Bundeskanzler oder Bundeskanzlerin?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-044",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sorgf\u00E4ltig trennst du deinen M\u00FCll?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-045",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne w\u00FCrdest du in einem Hollywood-Film mitspielen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-046",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr liegt dir Gartenarbeit?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-047",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie wohl f\u00FChlst du dich, wenn du vor gr\u00F6\u00DFerem Publikum sprichst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-048",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gut kannst du mit Kindern umgehen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-049",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne erz\u00E4hlst du Witze?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-050",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne bist du unter Menschen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-051",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie verf\u00FChrerisch bist du deiner Meinung nach?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-052",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr beeinflusst das Wetter deine Stimmung?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-053",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Comics?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-054",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie wohl f\u00FChlst du dich im allgemeinen Bezug auf Nacktheit?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-055",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie oft bist du f\u00FCr neue Erfahrungen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-056",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Dokumentarfilme?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-057",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne sprichst du mit fremden Menschen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-058",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie feministisch bist du eingestellt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-059",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Karaoke?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-060",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr musst du immer das letzte Wort haben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-061",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Du siehst ein niedliches, kleines K\u00E4tzchen. Wie entz\u00FCckt rufst du \"ohhh\"?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-062",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viel Angst hast du vor dem Zahnarzt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-063",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gesund ern\u00E4hrst du dich normalerweise?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-064",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viel l\u00E4ge dir daran, ber\u00FChmt zu sein?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-065",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr bist du bereit, dein Essen mit anderen zu teilen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-066",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Horrorfilme?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-067",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gut w\u00E4rst du darin, eine Zombie-Apokalypse zu \u00FCberleben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-068",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne spielst du Video- oder Computerspiele?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-069",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr f\u00E4hrst du auf die aktuell neuesten technischen Ger\u00E4ten ab?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-070",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie wichtig w\u00E4re es dir, ein Portr\u00E4t von dir in einem Museum h\u00E4ngen zu haben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-071",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne f\u00FChrst du Telefongespr\u00E4che?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-072",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie m\u00FCrrisch wirst du, wenn du viel zu wenig geschlafen hast?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-073",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du abstrakte Kunst?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-074",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr magst du Kost\u00FCmpartys?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-075",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne bist du im Wasser?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-076",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie viel besser war alles deiner Meinung nach fr\u00FCher?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-077",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie ungeschickt bist du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-078",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Was denkst du, wie mutig du bist?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-079",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne w\u00FCrdest du in eine Geheimgesellschaft eintreten?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-080",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie w\u00E4hlerisch bist du beim Essen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-081",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie ehrgeizig bist du im Berufsleben?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-082",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie m\u00FCrrisch wirst du, wenn du Hunger hast?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-083",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne w\u00FCrdest du in Hogwarts zur Schule gehen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-084",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne gehst du shoppen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-085",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr befolgst du geltende Regeln?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-086",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie kreativ bist du?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-087",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gut kannst du mit alten Menschen umgehen?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-088",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr liegt dir Teamarbeit?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-089",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie gerne gibst du Partys bei dir zu Hause?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-090",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie schwer f\u00E4llt es dir, etwas wegzuwerfen, \"weil man es ja vielleicht noch brauchen kann\"?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-091",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr macht es dir Spa\u00DF, Dinge selbst zu reparieren?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "fun-facts-von-0-bis-100-092",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "de",
                                    text = "Wie sehr stimmst du mit dem Spruch \"Gl\u00FCck kann man kaufen\" \u00FCberein?",
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ),
        spiel(
            id = "were-not-really-strangers",
            name = "We're not really strangers",
            kartentexteProKarte = 1,
            kategorien = linkedSetOf(
                kategorie(
                    id = "were-not-really-strangers-level-1-perception",
                    name = "Level 1 .. Perception",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do I look kind? Explain.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What is my body language telling you right now?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do I seem like a coffee or tea person? Sweetened or unsweetened?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do you think I've ever been fired from a job? If so, what for?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Close your eyes. What color are my eyes?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What about me is most strange or unfamiliar to you?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do I seem like a morning person or a night owl? Why?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do you think I've ever checked an ex's phone for evidence?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do I seem like more of creative or analytical type? Explain.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "How likely am I to go camping? How high maintenance is my set up?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Write down something others would never guess about you just by looking at you. Compare.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Make an assumption about me.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "How many speeding tickets do you think I've gotten in my life?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Draw a portrait of each other to the best of your ability. After 1 Minute, exchange.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-015",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think is the hardest part of what I do for a living?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-016",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "As a child, what do you think I wanted to be?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-017",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "On a scale of 1-10, how messy do you think my car is? 1 being cleanest, 10 a complete disaster. Explain.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-018",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What subject do you think I thrived in at school? Did I fail any?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-019",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "If you were to buy me a present, knowing nothing about me other than what I look like, what would it be?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-020",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What does my Instagram tell you about me?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-021",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Think of your favorite brand of cereal. On the count of three, say your answers out loud!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-022",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Think of your favorite childhood TV show of all time. On the count of three, say it out loud!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-023",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Ask and answer the next question in a different accent.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-024",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Reminder: Let go of your attachment of the outcome.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-025",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do I seem like a cat or dog person?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-026",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Maintain eye contact for thirty seconds. What did you notice?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-027",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do my shoes tell you about me?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-028",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Rate your dancing skills on a scale of 1-10. On the count of three, say your answers out loud!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-029",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Draw a picture together. (30 seconds)",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-030",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think my celebrity crush is?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-031",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What fast food restaurant do you think I'm most likely to drive through? What's my order?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-032",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think my go to karaoke song is?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-033",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Finish the sentence: just by looking at you I'd think ___.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-034",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What reality show do you think I'm most likely to binge watch? Explain?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-035",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do you think plants thrive or die in my care. Explain.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-036",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What about me intrigues you?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-037",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Close your eyes. What color is my shirt?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-038",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think I'm most likely to splurge on?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-039",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do you think I was popular in school? Explain.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-040",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What compliment do you think I hear the most?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-041",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What does my phone wallpaper tell about me?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-042",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do you think I intimidate others? Why or why not?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-043",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "If Myspace were still a thing; what would my profile song be?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-044",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What character would I play in a movie?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-045",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do you think I'm usually early, on time, or late to events? Explain.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-046",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do I seem like someone who would get a name tattooed on myself? Why or why not?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-047",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What was your first impression of me?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-048",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do I remind you of anyone?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-049",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do you think I fall in love easily? Why or why not?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-1-perception-050",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What's the first thing you noticed about me?",
                                ),
                            ),
                        ),
                    ),
                ),
                kategorie(
                    id = "were-not-really-strangers-level-2-connection",
                    name = "Level 2 .. Connection",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Describe your perfect day!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Reminder: Be more interested in understanding others than being understood.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Are you lying to yourself about anything?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Is there a feeling you miss?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What is a dream you've let go off?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Admit something.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Create a secret handshake!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Have you ever told someone I love you but didn't mean it? If so, why?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Are you missing anyone right now? Do you think they are missing you too?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Staring contest. First to blink must reveal a personal problem and ask your partner for advice on how they might handle it.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Both players write an embarrassing fun fact about yourselves. Play a game of rock, paper, scissors. Lost must reveal!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What's your mother's name? And the most beautiful thing about her?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do you think the image you have of yourself matches the image people see you as?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What\u00B4s your father\u00B4s name? And tell me one thing about him.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-015",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "If you have, when was the moment you realized you weren't invincible?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-016",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "How can you become a better person?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-017",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "If you could get to know someone in your life on a deeper level, who would it be and why?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-018",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "How would you describe the feeling of being in love in one word?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-019",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What's the most pain you've ever been in that wasn't physical?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-020",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What\u00B4s been your happiest memory this past year?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-021",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What is your 1st love's name and the reason you fell in love with him/her?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-022",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you crave more of?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-023",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What would your younger self not believe about your life today?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-024",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Write the three most important things in life to you. After 30 seconds, compare.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-025",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Draw your current mood. Then compare.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-026",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What is something you wouldn't want to change about yourself?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-027",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Questions are an art form. Create your own question.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-028",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What was the last time you surprised yourself?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-029",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "When you're asked how are you, hof often do you answer truthfully?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-030",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Have you changed your mind about anything recently?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-031",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "How are you, really?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-032",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Has a stranger ever changed your life?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-033",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What's been the best compliment a stranger has ever given you?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-034",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What is a compliment you wish you received more frequently?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-035",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What title would you give this chapter in your life?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-036",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Press shuffle on your music library. Explain the first song that comes up.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-037",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What part of your life works? What part of your life hurts?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-038",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Call someone you admire and tell them why you appreciate them! (put on speaker phone)",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-039",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Show your first photo in your camera roll. Explain.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-040",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Swap seats with your partner.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-041",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Sing the chorus of your favorite song of all time. Get into it!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-042",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "If you could have it your way: Who would you be with? Where would you be? & What would you be doing?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-043",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What lesson took you the longest to unlearn?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-044",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Finish the sentences: Strangers would describe me as ___. Only I know that I am ___.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-045",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What is the most unexplainable thing that's ever happened to you?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-046",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What is the last thing you lied to your mother about?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-047",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What are you still trying to prove to yourself?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-048",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What are you more afraid of, failure or success? And why?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-049",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Think of something you strongly dislike that most people love. On the count of three say it out loud!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-050",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What questions are you trying to answer most in your life right now?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-051",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What has been your earliest recollection of happiness?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-052",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Ask a question you'd be too afraid to ask. Something you wouldn't dare to ask.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-2-connection-053",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Final Card of the deck: Each player write a message to the other. Fold and exchange. Open only once you two have parted.",
                                ),
                            ),
                        ),
                    ),
                ),
                kategorie(
                    id = "were-not-really-strangers-level-3-reflection",
                    name = "Level 3 .. Reflection",
                    kartentexte = linkedSetOf(
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-001",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "In one word, how would you describe our conversation?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-002",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Give your partner a compliment you don't think the hear enough.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-003",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Admit something.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-004",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you recommend I let go of, if anything?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-005",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "How does one earn your vulnerability? Have I earned it? How can I earn more?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-006",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think my weakness is?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-007",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What can I help you with?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-008",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What about me most surprised you?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-009",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Play a round of rock paper scissors. Winner can ask their partner anything. Loser must answer.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-010",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Write down one thing you want to let go of this year. Read out loud, then rip up together.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-011",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Write down a goal for this year. Fold & exchange. Hold each other accountable.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-012",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "If we were in a band, what would our name be?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-013",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think our most important similarity is?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-014",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Give each other nicknames!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-015",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Do you believe everyone has a calling? If so, do you think I've found mine?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-016",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "When this game is over, what will you remember about me?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-017",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "When in this game did you fell most connected to me?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-018",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Dare your partner to do something outside of their comfort zone in the next week.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-019",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What question were you most afraid of to answer?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-020",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Take a selfie together.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-021",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Scroll through each other's Instagrams. Find the picture you feel best represents your partner's essence and comment why you chose that image.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-022",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Based on what you learned about me, what book would you recommend I read?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-023",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "How would you describe me to a stranger.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-024",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you admire most about me?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-025",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think I should know about myself that perhaps I'm unaware of?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-026",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Why do you think we met?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-027",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Write a song about your partner in 30 seconds. Then sing it out loud. Get into it!",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-028",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What answer of mine made you light up?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-029",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "How do our personalities complement each other?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-030",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think my superpower is?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-031",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What would be the perfect gift for me?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-032",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What has this conversation taught you about yourself?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-033",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What parts of yourself do you see in me?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-034",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do I need to hear right now?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-035",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard - both players: Swap a song suggestion your partner may enjoy.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-036",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Create your own question. Make it count.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-037",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Based on what you learned about me, does my social media accurately reflect who I am? Why or why not?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-038",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Both players share something you're most grateful for in this current moment.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-039",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "In one word, describe how you feel right now.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-040",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Give your partner a hug. Not the crappy kind. A warm fluffy one.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-041",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What is a lesson you will take away form our conversation?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-042",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Based on what you know about me, do you have any Netflix recommendations?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-043",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What about me is hardest for you to understand?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-044",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What am I most qualified to give advice about?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-045",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "If you could prescribe me one thing to do for the rest of this month, what would it be and why?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-046",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "Wildcard: Both players write a note to your younger selves in 1 minute. Option to compare.",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-047",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think my defining characteristic is?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-048",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What do you think I fear the most?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-049",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What can we create together?",
                                ),
                            ),
                        ),
                        kartentext(
                            id = "were-not-really-strangers-level-3-reflection-050",
                            translationen = linkedSetOf(
                                Translation(
                                    sprachCode = "en",
                                    text = "What would make you feel closer to me?",
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ),
    )

    private fun spiel(
        id: String,
        name: String,
        kartentexteProKarte: Int,
        kategorien: Set<Kategorie>,
    ): Spiel =
        Spiel(
            id = id,
            lokalisierung = lokalisierung(
                id = "$id-name",
                translationen = linkedSetOf(Translation(sprachCode = "de", text = name)),
            ),
            kategorien = kategorien,
            kartentexteProKarte = kartentexteProKarte,
        )

    private fun kategorie(
        id: String,
        name: String,
        kartentexte: Set<Kartentext>,
    ): Kategorie =
        Kategorie(
            id = id,
            lokalisierung = lokalisierung(
                id = "$id-name",
                translationen = linkedSetOf(Translation(sprachCode = "de", text = name)),
            ),
            kartentexte = kartentexte,
        )

    private fun kartentext(
        id: String,
        translationen: Set<Translation>,
    ): Kartentext =
        Kartentext(
            id = id,
            lokalisierung = lokalisierung(
                id = "$id-text",
                translationen = translationen,
            ),
        )

    private fun lokalisierung(
        id: String,
        translationen: Set<Translation>,
    ): Lokalisierung =
        Lokalisierung(
            id = id,
            translationen = translationen,
        )
}
