package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Spiel
import javax.inject.Inject

class GetNextRandomCardUseCase @Inject constructor() {
    operator fun invoke(spiel: Spiel): GezogeneKarte =
        GezogeneKarte(
            kartentexte = spiel.kategorien
                .flatMap { kategorie ->
                    kategorie.kartentexte.map { kartentext ->
                        GezogenerKartentext(
                            kartentext = kartentext,
                            kategorieId = kategorie.id,
                        )
                    }
                }
                .shuffled()
                .distinctBy { gezogenerKartentext -> gezogenerKartentext.kartentext.id }
                .take(spiel.kartentexteProKarte),
        )
}
