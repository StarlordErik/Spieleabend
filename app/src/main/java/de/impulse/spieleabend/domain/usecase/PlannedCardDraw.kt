package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Kategorie
import de.impulse.spieleabend.domain.model.Spiel

internal data class PlannedCardDraw(
    val karte: GezogeneKarte,
    val resetSeenKategorieIds: Set<Int> = emptySet(),
    val resetSeenUndGespieltKategorieIds: Set<Int> = emptySet(),
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

    val playedResetKategorieIds =
        if (kategorie.kartentexte.isNotEmpty() && kategorie.kartentexte.all { kartentext -> kartentext.gespielt }) {
            setOf(kategorie.id())
        } else {
            emptySet()
        }
    val effektiveKategorie =
        if (playedResetKategorieIds.isEmpty()) {
            kategorie
        } else {
            kategorie.resetSeenUndGespielt()
        }
    val ungeseheneKartentexte =
        effektiveKategorie.kartentexte.filterNot { kartentext ->
            kartentext.gesehen
        }

    val resetSeenKategorieIds =
        if (ungeseheneKartentexte.size < spiel.texteProKarte && playedResetKategorieIds.isEmpty()) {
            setOf(kategorie.id())
        } else {
            emptySet()
        }

    return if (resetSeenKategorieIds.isNotEmpty()) {
        PlannedCardDraw(
            karte = effektiveKategorie.gezogeneKarte(spiel.texteProKarte),
            resetSeenKategorieIds = resetSeenKategorieIds,
            resetSeenUndGespieltKategorieIds = playedResetKategorieIds,
        )
    } else {
        PlannedCardDraw(
            karte = effektiveKategorie.gezogeneKarte(
                texteProKarte = spiel.texteProKarte,
                kartentexte = ungeseheneKartentexte,
            ),
            resetSeenUndGespieltKategorieIds = playedResetKategorieIds,
        )
    }
}

internal fun planNextRandomCard(spiel: Spiel): PlannedCardDraw {
    val kategorien = spiel.kategorien.toList()
    val playedResetKategorieIds =
        if (
            kategorien.isNotEmpty() &&
            randomKartentexte(kategorien, unseenOnly = false).all { kartentext ->
                kartentext.kartentext.gespielt
            }
        ) {
            kategorien.map { kategorie -> kategorie.id() }.toSet()
        } else {
            emptySet()
        }
    val effektiveKategorien =
        if (playedResetKategorieIds.isEmpty()) {
            kategorien
        } else {
            kategorien.map { kategorie -> kategorie.resetSeenUndGespielt() }
        }
    val ungeseheneKartentexte = randomKartentexte(effektiveKategorien, unseenOnly = true)
    val resetSeenKategorieIds =
        if (ungeseheneKartentexte.size < spiel.texteProKarte && playedResetKategorieIds.isEmpty()) {
            effektiveKategorien.map { kategorie -> kategorie.id() }.toSet()
        } else {
            emptySet()
        }

    return if (resetSeenKategorieIds.isNotEmpty()) {
        PlannedCardDraw(
            karte = GezogeneKarte(randomKartentexte(effektiveKategorien, unseenOnly = false).take(spiel.texteProKarte)),
            resetSeenKategorieIds = resetSeenKategorieIds,
            resetSeenUndGespieltKategorieIds = playedResetKategorieIds,
        )
    } else {
        PlannedCardDraw(
            karte = GezogeneKarte(ungeseheneKartentexte.take(spiel.texteProKarte)),
            resetSeenUndGespieltKategorieIds = playedResetKategorieIds,
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

private fun Kategorie.resetSeenUndGespielt(): Kategorie =
    copy(
        originaleKartentexte = originaleKartentexte.mapToLinkedHashSet { kartentext ->
            kartentext.copy(
                gesehen = false,
                gespielt = false,
            )
        },
        hinzugefuegteKartentexte = hinzugefuegteKartentexte.mapToLinkedHashSet { kartentext ->
            kartentext.copy(
                gesehen = false,
                gespielt = false,
            )
        },
        inaktiveKartentexte = inaktiveKartentexte.mapToLinkedHashSet { kartentext ->
            kartentext.copy(
                gesehen = false,
                gespielt = false,
            )
        },
    )

private fun <T, R> Iterable<T>.mapToLinkedHashSet(transform: (T) -> R): LinkedHashSet<R> =
    map(transform).toCollection(LinkedHashSet())
