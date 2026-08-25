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
        if (kategorie.ziehbareKartentexte(unseenOnly = false).isEmpty()) {
            setOf(kategorie.id())
        } else {
            emptySet()
        }
    val effektiveKategorieNachPlayedReset =
        if (playedResetKategorieIds.isEmpty()) {
            kategorie
        } else {
            kategorie.resetSeenUndGespielt()
        }
    val ungeseheneKartentexte = effektiveKategorieNachPlayedReset.ziehbareKartentexte(unseenOnly = true)

    val resetSeenKategorieIds =
        if (ungeseheneKartentexte.isEmpty() && playedResetKategorieIds.isEmpty()) {
            setOf(kategorie.id())
        } else {
            emptySet()
        }
    val effektiveKategorie =
        if (resetSeenKategorieIds.isEmpty()) {
            effektiveKategorieNachPlayedReset
        } else {
            effektiveKategorieNachPlayedReset.resetSeenForNichtGespielte()
        }
    val ziehbareKartentexte = effektiveKategorie.ziehbareKartentexte(unseenOnly = true)

    return PlannedCardDraw(
        karte = effektiveKategorie.gezogeneKarte(
            texteProKarte = spiel.texteProKarte,
            kartentexte = ziehbareKartentexte,
        ),
        resetSeenKategorieIds = resetSeenKategorieIds,
        resetSeenUndGespieltKategorieIds = playedResetKategorieIds,
    )
}

internal fun planNextRandomCard(spiel: Spiel): PlannedCardDraw {
    val kategorien = spiel.kategorien.toList()
    val playedResetKategorieIds =
        if (kategorien.isNotEmpty() && randomKartentexte(kategorien, unseenOnly = false).isEmpty()) {
            kategorien.map { kategorie -> kategorie.id() }.toSet()
        } else {
            emptySet()
        }
    val effektiveKategorienNachPlayedReset =
        if (playedResetKategorieIds.isEmpty()) {
            kategorien
        } else {
            kategorien.map { kategorie -> kategorie.resetSeenUndGespielt() }
        }
    val ungeseheneKartentexte = randomKartentexte(effektiveKategorienNachPlayedReset, unseenOnly = true)
    val resetSeenKategorieIds =
        if (ungeseheneKartentexte.isEmpty() && playedResetKategorieIds.isEmpty()) {
            effektiveKategorienNachPlayedReset.map { kategorie -> kategorie.id() }.toSet()
        } else {
            emptySet()
        }
    val effektiveKategorien =
        if (resetSeenKategorieIds.isEmpty()) {
            effektiveKategorienNachPlayedReset
        } else {
            effektiveKategorienNachPlayedReset.map { kategorie -> kategorie.resetSeenForNichtGespielte() }
        }
    val ziehbareKartentexte = randomKartentexte(effektiveKategorien, unseenOnly = true)

    return PlannedCardDraw(
        karte = GezogeneKarte(ziehbareKartentexte.take(spiel.texteProKarte)),
        resetSeenKategorieIds = resetSeenKategorieIds,
        resetSeenUndGespieltKategorieIds = playedResetKategorieIds,
    )
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
            kategorie.ziehbareKartentexte(unseenOnly)
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

private fun Kategorie.ziehbareKartentexte(unseenOnly: Boolean): List<de.impulse.spieleabend.domain.model.Kartentext> =
    kartentexte.filterNot { kartentext ->
        kartentext.gespielt || (unseenOnly && kartentext.gesehen)
    }

private fun Kategorie.resetSeenForNichtGespielte(): Kategorie =
    copy(
        originaleKartentexte = originaleKartentexte.mapToLinkedHashSet { kartentext ->
            if (kartentext.gespielt) {
                kartentext
            } else {
                kartentext.copy(gesehen = false)
            }
        },
        hinzugefuegteKartentexte = hinzugefuegteKartentexte.mapToLinkedHashSet { kartentext ->
            if (kartentext.gespielt) {
                kartentext
            } else {
                kartentext.copy(gesehen = false)
            }
        },
        inaktiveKartentexte = inaktiveKartentexte.mapToLinkedHashSet { kartentext ->
            if (kartentext.gespielt) {
                kartentext
            } else {
                kartentext.copy(gesehen = false)
            }
        },
    )

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
