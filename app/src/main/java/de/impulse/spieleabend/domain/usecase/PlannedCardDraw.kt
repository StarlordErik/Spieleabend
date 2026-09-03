package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.BearbeiteteKartentexteModus
import de.impulse.spieleabend.domain.model.FavoritenModus
import de.impulse.spieleabend.domain.model.GeloeschteKartentexteModus
import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Kartentext
import de.impulse.spieleabend.domain.model.Spiel

internal data class PlannedCardDraw(
    val karte: GezogeneKarte,
    val resetSeenKartentextIds: Set<Int> = emptySet(),
    val resetSeenUndGespieltKartentextIds: Set<Int> = emptySet(),
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

    return planNextCard(
        spiel = spiel,
        candidates = kategorie.kartentexte.map { kartentext ->
            CardTextCandidate(kartentext = kartentext, kategorieId = kategorie.id())
        },
    )
}

internal fun planNextRandomCard(spiel: Spiel): PlannedCardDraw =
    planNextCard(
        spiel = spiel,
        candidates = spiel.kategorien
            .flatMap { kategorie ->
                kategorie.kartentexte.map { kartentext ->
                    CardTextCandidate(kartentext = kartentext, kategorieId = kategorie.id())
                }
            }
            .shuffled()
            .distinctBy { candidate -> candidate.kartentext.id() },
    )

private fun planNextCard(
    spiel: Spiel,
    candidates: List<CardTextCandidate>,
): PlannedCardDraw {
    val planner = CardTextPoolPlanner()
    var hardFilteredCandidates = candidates

    if (spiel.bearbeiteteKartentexteModus == BearbeiteteKartentexteModus.AUSSCHLIESSLICH) {
        hardFilteredCandidates = hardFilteredCandidates.filter { candidate ->
            candidate.kartentext.lokalisierung.hatEigeneLokalisierung()
        }
    }
    if (spiel.favoritenModus == FavoritenModus.AUSSCHLIESSLICH) {
        hardFilteredCandidates = hardFilteredCandidates.filter { candidate -> candidate.kartentext.favorit }
    }
    hardFilteredCandidates = when (spiel.geloeschteKartentexteModus) {
        GeloeschteKartentexteModus.AUSBLENDEN ->
            hardFilteredCandidates.filterNot { candidate -> candidate.kartentext.inaktiv }
        GeloeschteKartentexteModus.AUSSCHLIESSLICH ->
            hardFilteredCandidates.filter { candidate -> candidate.kartentext.inaktiv }
        GeloeschteKartentexteModus.ALS_LETZTE,
        GeloeschteKartentexteModus.UNBEACHTET,
        -> hardFilteredCandidates
    }

    val deletionStage = deletionStage(
        candidates = hardFilteredCandidates.map(planner::effectiveCandidate),
        mode = spiel.geloeschteKartentexteModus,
    )
    val selected = when (spiel.favoritenModus) {
        FavoritenModus.GENAU_EINER_PRO_KARTE -> planner.drawWithExactlyOneFavorite(
            candidates = deletionStage.candidates,
            count = spiel.texteProKarte,
        )
        FavoritenModus.UNBEACHTET,
        FavoritenModus.AUSSCHLIESSLICH,
        -> planner.draw(deletionStage.candidates, spiel.texteProKarte)
    }

    if (deletionStage.shouldResetNormalCandidatesAfterDraw(planner, selected)) {
        planner.refreshForNextCycle(deletionStage.normalCandidatesToResetAfterDraw)
    }

    return PlannedCardDraw(
        karte = GezogeneKarte(
            selected.map { candidate ->
                GezogenerKartentext(
                    kartentext = candidate.kartentext,
                    kategorieId = candidate.kategorieId,
                )
            },
        ),
        resetSeenKartentextIds = planner.resetSeenIds,
        resetSeenUndGespieltKartentextIds = planner.resetSeenAndPlayedIds,
    )
}

@Suppress("ReturnCount")
private fun deletionStage(
    candidates: List<CardTextCandidate>,
    mode: GeloeschteKartentexteModus,
): DeletionStage {
    if (mode != GeloeschteKartentexteModus.ALS_LETZTE) {
        return DeletionStage(candidates = candidates)
    }

    val normalCandidates = candidates.filterNot { candidate -> candidate.kartentext.inaktiv }
    val deletedCandidates = candidates.filter { candidate -> candidate.kartentext.inaktiv }
    val unseenNormalCandidates = normalCandidates.filter(CardTextCandidate::isUnseenAndUnplayed)
    if (unseenNormalCandidates.isNotEmpty() || deletedCandidates.isEmpty()) {
        return DeletionStage(candidates = normalCandidates)
    }

    return DeletionStage(
        candidates = deletedCandidates,
        normalCandidatesToResetAfterDraw = normalCandidates,
    )
}

private data class DeletionStage(
    val candidates: List<CardTextCandidate>,
    val normalCandidatesToResetAfterDraw: List<CardTextCandidate> = emptyList(),
)

private fun DeletionStage.shouldResetNormalCandidatesAfterDraw(
    planner: CardTextPoolPlanner,
    selected: List<CardTextCandidate>,
): Boolean {
    if (normalCandidatesToResetAfterDraw.isEmpty()) return false

    val selectedIds = selected.mapTo(mutableSetOf()) { candidate -> candidate.kartentext.id() }
    val unseenDeletedAfterRefresh = candidates
        .map(planner::effectiveCandidate)
        .filter(CardTextCandidate::isUnseenAndUnplayed)
    return unseenDeletedAfterRefresh.isNotEmpty() &&
        unseenDeletedAfterRefresh.all { candidate -> candidate.kartentext.id() in selectedIds }
}

private data class CardTextCandidate(
    val kartentext: Kartentext,
    val kategorieId: Int,
) {
    fun isUnseenAndUnplayed(): Boolean = !kartentext.gesehen && !kartentext.gespielt
}

private class CardTextPoolPlanner {
    private val mutableResetSeenIds = linkedSetOf<Int>()
    private val mutableResetSeenAndPlayedIds = linkedSetOf<Int>()

    val resetSeenIds: Set<Int>
        get() = mutableResetSeenIds

    val resetSeenAndPlayedIds: Set<Int>
        get() = mutableResetSeenAndPlayedIds

    fun drawWithExactlyOneFavorite(
        candidates: List<CardTextCandidate>,
        count: Int,
    ): List<CardTextCandidate> {
        val favoriteCandidates = candidates.filter { candidate -> candidate.kartentext.favorit }
        if (favoriteCandidates.isEmpty()) {
            return draw(candidates, count)
        }

        val selectedFavorite = draw(favoriteCandidates, count = 1)
        val otherCandidates = candidates.filterNot { candidate -> candidate.kartentext.favorit }
        val selectedOthers = draw(
            candidates = otherCandidates,
            count = (count - selectedFavorite.size).coerceAtLeast(0),
        )
        return (selectedFavorite + selectedOthers).shuffled()
    }

    fun draw(
        candidates: List<CardTextCandidate>,
        count: Int,
    ): List<CardTextCandidate> {
        if (count <= 0 || candidates.isEmpty()) {
            return emptyList()
        }

        var effectiveCandidates = candidates.map(::effectiveCandidate)
        var unplayedCandidates = effectiveCandidates.filterNot { candidate -> candidate.kartentext.gespielt }
        if (unplayedCandidates.isEmpty()) {
            resetSeenAndPlayed(effectiveCandidates)
            effectiveCandidates = effectiveCandidates.map(::effectiveCandidate)
            unplayedCandidates = effectiveCandidates
        }

        var unseenCandidates = unplayedCandidates.filterNot { candidate -> candidate.kartentext.gesehen }
        if (unseenCandidates.isEmpty()) {
            resetSeen(unplayedCandidates)
            unseenCandidates = unplayedCandidates.map(::effectiveCandidate)
        }

        return unseenCandidates.shuffled().take(count)
    }

    fun resetSeen(candidates: Collection<CardTextCandidate>) {
        candidates
            .filterNot { candidate -> candidate.kartentext.gespielt }
            .mapTo(mutableResetSeenIds) { candidate -> candidate.kartentext.id() }
    }

    fun resetSeenAndPlayed(candidates: Collection<CardTextCandidate>) {
        val ids = candidates.map { candidate -> candidate.kartentext.id() }
        mutableResetSeenIds.removeAll(ids.toSet())
        mutableResetSeenAndPlayedIds.addAll(ids)
    }

    fun refreshForNextCycle(candidates: Collection<CardTextCandidate>) {
        val effectiveCandidates = candidates.map(::effectiveCandidate)
        if (effectiveCandidates.isNotEmpty() && effectiveCandidates.all { candidate ->
                candidate.kartentext.gespielt
            }
        ) {
            resetSeenAndPlayed(effectiveCandidates)
        } else {
            resetSeen(effectiveCandidates)
        }
    }

    fun effectiveCandidate(candidate: CardTextCandidate): CardTextCandidate {
        val id = candidate.kartentext.id()
        return when (id) {
            in mutableResetSeenAndPlayedIds -> candidate.copy(
                kartentext = candidate.kartentext.copy(gesehen = false, gespielt = false),
            )
            in mutableResetSeenIds -> candidate.copy(
                kartentext = candidate.kartentext.copy(gesehen = false),
            )
            else -> candidate
        }
    }
}
