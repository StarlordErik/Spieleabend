# Spielschachtel-Grafiken

Der Generator benötigt Python 3.10 oder neuer und Pillow. Unter Windows:

```powershell
py -3.14 -m pip install -r scripts/requirements.txt
py -3.14 scripts/generate_game_box_art.py
```

Die PNGs werden nach `app/src/main/assets/images` und
`app/src/main/res/drawable-nodpi` geschrieben. Texte und Farben kommen aus den
Android-Ressourcen. Fehlen die Windows-Schriften, verwendet der Generator die
skalierbare Standardschrift von Pillow.

Android Studio führt `scripts` als eigenes Python-Modul `Impulse.scripts`.
Diesem Modul ist das SDK `Python 3.14 (Impulse scripts)` zugeordnet. Auf einem
neuen Rechner diesen Python-Interpreter unter **Project Structure → SDKs**
anlegen oder dem Modul einen vorhandenen Python-Interpreter zuweisen und dort
die Abhängigkeiten aus `requirements.txt` installieren. Das Android-Projekt
verwendet weiterhin sein Java-SDK.
