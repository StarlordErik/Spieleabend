# Arbeitsanweisungen für KI-Agenten

Diese Datei richtet sich ausschließlich an KI-Agenten. Sie gilt für das gesamte
Projekt und ist bei jedem Prompt zu berücksichtigen.

## Normale Aufgabenbearbeitung

- Erledige zunächst die vom Nutzer beauftragte Aufgabe.
- Führe anschließend nur die dafür angemessenen Standardprüfungen aus, etwa die
  betroffenen Funktionstests und eine Build- oder Kompilierungsprüfung. Für Unit-
  und Integrationstests gelten die folgenden Laufzeitregeln.
- Behebe Fehler, die durch deine Änderungen entstehen oder den erfolgreichen
  Abschluss der beauftragten Aufgabe verhindern, direkt im Rahmen dieser Aufgabe.
- Prüfe konkret gemeldete Diagnosen gezielt mit dem meldenden Werkzeug und der
  betreffenden Regel nach. Ein erfolgreicher Build oder ein anderes Analysewerkzeug
  ersetzt diese Kontrolle nicht. Benenne eine nicht durchführbare Kontrolle klar.
- Starte ohne entsprechenden Auftrag keine zusätzliche umfassende Prüfung mit
  den unten genannten Analysewerkzeugen. Beginne auch keine projektweite
  Bereinigung sonstiger Warnungen und Fehler auf eigene Initiative.

## Konkrete Prüfwerkzeuge

| Werkzeug                                                           | Prüfbereich und Ausführung                                                                                                                                                                                                                       |
|--------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Android Lint                                                       | Android-Code, Ressourcen und Manifest; Gradle-Task `:app:lint`.                                                                                                                                                                                  |
| Detekt                                                             | Statische Kotlin-Codeanalyse mit `config/detekt/detekt.yml`; Gradle-Task `:app:detekt`.                                                                                                                                                          |
| Sonar Kotlin Analyzer aus dem SonarLint-Plugin (SonarQube for IDE) | Lokaler Hilfsprüfer für Kotlin-Syntax, kognitive Komplexität (höchstens 20) und Parameterzahl (höchstens 10).                                                                                                                                    |
| Sonar Python Analyzer aus dem SonarLint-Plugin (SonarQube for IDE) | Lokaler Hilfsprüfer für `python:S930` (Anzahl und Namen von Aufrufargumenten); keine vollständige Prüfung aller Python-Sonar-Regeln.                                                                                                             |
| Android Studio – JetBrains-IDE-Inspektionen                        | Kotlin-, Compose-, Gradle- und XML-Meldungen sowie Markdown-Formatierung und Grammatik nach dem IDE-Inspektionsprofil.                                                                                                                           |
| PyCharm – JetBrains-Python-Inspektionen                            | Python-Importe, Referenzen und Typen im eingerichteten Python-SDK.                                                                                                                                                                               |
| Pyright                                                            | Statische Python-Typprüfung für `scripts/generate_game_box_art.py` mit dem Projekt-Python-Interpreter.                                                                                                                                           |
| JUnit 4                                                            | Automatisierte Unit-Tests; Gradle-Task `:app:testDebugUnitTest`.                                                                                                                                                                                 |
| AndroidJUnitRunner und Room MigrationTestHelper                    | Instrumentierte Android-Integrationstests einschließlich Datenbankmigrationen; Gradle-Task `:app:connectedDebugAndroidTest`.                                                                                                                     |
| Kover                                                              | Ergänzende HTML- und XML-Berichte zur Abdeckung durch JVM-Tests, im Projekt an `:app:check` gekoppelt. Instrumentierte Tests auf Android-Geräten werden damit nicht erfasst. Derzeit sind keine Mindestwerte für die Testabdeckung konfiguriert. |

Die bisher eingesetzten Sonar-Hilfsprüfer liegen lokal außerhalb des Repositorys.
Ein vollständiger SonarScanner-Lauf ist im Projekt nicht eingerichtet. Weise den
tatsächlich geprüften Regelumfang aus; berichte die Hilfsprüfer nicht als
vollständige Sonar-Analyse.

Nenne die eingesetzten Programme und den Prüfbereich konkret im Ergebnisbericht.
Verwende in der Abschlussnachfrage ausschließlich die Kurzform „umfassende
Code-Analyse und Fehlererkennung“ anstelle der Werkzeugliste. Prüfe die
Verfügbarkeit der Werkzeuge; benenne fehlende Werkzeuge ausdrücklich.

## Unit- und Integrationstests

- Gradle führt die oben genannten Tests und die Kotlin-Kompilierung aus.
- Bei lokaler Arbeit laufen JVM-Tests und Kover auf dem Entwicklungsrechner.
  Instrumentierte Android-Tests laufen auf einem Emulator oder einem verbundenen
  Android-Gerät. Die KI startet die Werkzeuge und wertet Ausgaben, Berichte und
  bei Bedarf den betroffenen Code aus.
- Verwende gezielte Testtasks wie `:app:testDebugUnitTest` beziehungsweise
  `:app:connectedDebugAndroidTest`. Die Sammelaufgabe `:app:check` führt zusätzlich
  Analysewerkzeuge aus und erstellt Kover-Berichte; sie ist daher kein Standardtest
  ohne Nachfrage.
- Führe die zur Änderung passenden Unit- und Integrationstests ohne Nachfrage mit
  aus, sofern sie schnell laufen. Als Richtwert gelten insgesamt etwa zwei Minuten.
  Die Zeit für den erforderlichen Build und das Setup zählt dazu.
- Schätze die Laufzeit anhand vorhandener Testberichte, früherer Durchläufe und
  des benötigten Setups. Starte keine vollständige Testsuite nur zur Zeitmessung.
- Längere oder aufwendig einzurichtende Testläufe benötigen zuerst einen
  ausdrücklichen Auftrag oder eine einfache Bestätigung. Ist die Laufzeit unklar,
  führe nur verlässlich kurze, passende Teilprüfungen automatisch aus und biete
  den restlichen Testumfang zum Abschluss an.
- Wiederhole erfolgreiche Tests nur bei weiteren relevanten Änderungen,
  konkreten Hinweisen auf ein Problem oder wenn für die beauftragte Kover-Analyse
  noch Messdaten fehlen. Halte Testausgaben knapp und lies bei Fehlern gezielt die
  benötigten Details.
- Erstelle und bewerte Kover-Berichte im Rahmen der bestätigten umfassenden
  Prüfung oder eines ausdrücklichen Auftrags zur Testabdeckung. Nutze dafür die
  vorhandenen Tests und Messdaten. Berichte relevante Abdeckungslücken als
  Hinweise; fehlende Abdeckung allein gilt nicht als zu behebender Programmfehler.
- Kover setzt keine vollständige Testabdeckung voraus. Ergänze Tests gezielt zur
  Absicherung relevanter Änderungen oder konkreter Fehler. Ein projektweites
  Nachrüsten von Tests, neue Mindestwerte oder ein pauschales Ziel von 100 Prozent
  Testabdeckung gehören nur zu einem ausdrücklichen Auftrag dafür. Hohe Abdeckung
  allein belegt keine fachliche Korrektheit.
- Bei reinen Dokumentations- oder Anweisungsänderungen reicht die Prüfung der
  betroffenen Dateien; starte dafür keine Unit-, Integrations- oder App-Build-Tests.
- Berichte, welche Tests gelaufen sind und welche noch eine Bestätigung benötigen.

## Nachfrage zum Abschluss

Frage den Nutzer nach Abschluss der Aufgabe in deiner abschließenden Antwort:

> Soll ich jetzt die umfassende Code-Analyse und Fehlererkennung durchführen und alle gefundenen Warnungen und Fehler beheben?

Diese Kurzform umfasst die oben aufgeführten statischen Analysen, die passenden
JUnit- und instrumentierten Integrationstests sowie die Kover-Berichte. Die
Zustimmung schließt längere Testläufe in diesem Umfang ein. Nenne bei absehbar
langen Testläufen kurz den zusätzlichen Zeitbedarf, ohne die Werkzeugliste zu
wiederholen. Eine Zustimmung nur zu Tests beauftragt keine umfassende
Warnungsbereinigung.

Warte die Antwort ab. Eine einfache Bestätigung wie „Ja“ reicht aus; eine erneute
Freigabe oder ein gesonderter Prüfplan ist dafür nicht erforderlich.

Hat der Nutzer bestimmte Tests oder die umfassende Prüfung und Fehlerbehebung
bereits ausdrücklich beauftragt, gilt das als Zustimmung für diesen Umfang.
Führe diesen Auftrag direkt aus und frage für denselben Prüfauftrag nicht erneut
nach. Nach einer abgeschlossenen umfassenden Prüfung ist keine weitere Nachfrage
zur Wiederholung dieser Prüfung nötig.

## Nach Zustimmung zur umfassenden Prüfung

- Führe die bestätigten Analysen, Tests und Kover-Auswertungen mit dem angegebenen
  Prüfbereich aus. Verwende weiterhin gültige Ergebnisse bereits abgeschlossener
  Tests, soweit möglich.
- Behebe die gefundenen Warnungen und Fehler, einschließlich weiterer Meldungen,
  die erst nach den Korrekturen sichtbar werden.
- Wiederhole die betroffenen Prüfungen, bis die Meldungen behoben sind, und prüfe
  die Funktionsfähigkeit der Änderungen mit den passenden Standardtests.
- Unterdrücke Warnungen nicht pauschal und schalte keine Prüfregeln ab, nur um
  ein sauberes Ergebnis vorzutäuschen.
- Berichte über die Ergebnisse. Benenne nicht verfügbare Prüfwerkzeuge und
  verbleibende, nicht behebbare Meldungen ausdrücklich; stelle sie nicht als
  erfolgreich geprüft oder behoben dar.
