package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Spiel

internal data class PlannedCardDraw(
    val karte: GezogeneKarte,
    val resetKategorieIds: Set<Int> = emptySet(),
)

internal fun planNextCardFromCategory(
    spiel: Spiel,
    kategorieId: Int,
): PlannedCardDraw {
    val kategorie = requireNotNull(spiel.kategorien.firstOrNull { aktiveKategorie ->
        aktiveKategorie.id() == kategorieId
    }) {
        "Das Spiel ${spiel.id()} enth\u00e4lt keine Kategorie mit der ID $kategorieId."
    }

    val ungeseheneKartentexte =
        kategorie.kartentexte.filterNot { kartentext ->
            kartentext.gesehen
        }

    return if (ungeseheneKartentexte.size < spiel.texteProKarte) {
        PlannedCardDraw(
            karte = kategorie.gezogeneKarte(spiel.texteProKarte),
            resetKategorieIds = setOf(kategorie.id()),
        )
    } else {
        PlannedCardDraw(
            karte = kategorie.gezogeneKarte(
                texteProKarte = spiel.texteProKarte,
                kartentexte = ungeseheneKartentexte,
            ),
        )
    }
}

internal fun planNextRandomCard(spiel: Spiel): PlannedCardDraw {
    val kategorien = spiel.kategorien.toList()
    val ungeseheneKartentexte = randomKartentexte(kategorien, unseenOnly = true)

    return if (ungeseheneKartentexte.size < spiel.texteProKarte) {
        PlannedCardDraw(
            karte = GezogeneKarte(randomKartentexte(kategorien, unseenOnly = false).take(spiel.texteProKarte)),
            resetKategorieIds = kategorien.map { kategorie -> kategorie.id() }.toSet(),
        )
    } else {
        PlannedCardDraw(
            karte = GezogeneKarte(ungeseheneKartentexte.take(spiel.texteProKarte)),
        )
    }
}

private fun Kategorie.gezogeneKarte(
    texteProKarte: Int,
    kartentexte: List<de.impulse.spieleabend.domain.model.Kartentext> = this.kartentexte.toList(),
): GezogeneKarte =
    GezogeneKarte(
        kartentexte =
            kartentexte
                .shuffled()
                .take(texteProKarte)
                .map { kartentext ->
                    GezogenerKartentext(
                        kartentext = kartentext,
                        kategorieId = id(),
                    )
                },
    )

private fun randomKartentexte(
    kategorien: List<Kategorie>,
    unseenOnly: Boolean,
): List<GezogenerKartentext> =
    kategorien
        .flatMap { kategorie ->
            kategorie.kartentexte
                .filterNot { kartentext ->
                    unseenOnly && kartentext.gesehen
                }
                .map { kartentext ->
                    GezogenerKartentext(
                        kartentext = kartentext,
                        kategorieId = kategorie.id(),
                    )
                }
        }
        .shuffled()
        .distinctBy { gezogenerKartentext ->
            gezogenerKartentext.kartentext.id()
        }
