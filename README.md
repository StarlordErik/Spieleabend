# Impulse

Alle möglichen Brett- &amp; Karten- &amp; Redespiele als einfache App!

Android-Paket und Application-ID: `de.kaserik.impulse`.

Oberflächentexte, Beschriftungen für Barrierefreiheit und Vorschautexte stehen in
`app/src/main/res/values/strings.xml`, alle Farbwerte in `colors.xml` im selben Ordner.
Auch `scripts/generate_game_box_art.py` liest seine Texte und Farben aus diesen Ressourcen.
Spielinhalte bleiben in der Datenbank `app/src/main/assets/impulse.db`, die aus `rohdaten` erzeugt wird.

Technische Kennungen und Diagnosemeldungen sind unter
`app/src/main/java/de/kaserik/impulse/common` in `AppConstants.kt` und `AppMessages.kt` gebündelt.

Einrichtung und Ausf?hrung des Grafikgenerators: [scripts/README.md](scripts/README.md).
