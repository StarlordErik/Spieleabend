package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.GezogeneKarte
import de.impulse.spieleabend.domain.model.GezogenerKartentext
import de.impulse.spieleabend.domain.model.Spiel
import javax.inject.Inject

class GetNextCardFromCategoryUseCase @Inject constructor() {
    operator fun invoke(
        spiel: Spiel,
        kategorieId: Int,
    ): GezogeneKarte {
        val kategorie = requireNotNull(spiel.kategorien.firstOrNull { kategorie ->
            kategorie.id == kategorieId
        }) {
            "Das Spiel ${spiel.id} enth\u00e4lt keine Kategorie mit der ID $kategorieId."
        }

        return GezogeneKarte(
            kartentexte = kategorie.kartentexte
                .shuffled()
                .take(spiel.texteProKarte)
                .map { kartentext ->
                    GezogenerKartentext(
                        kartentext = kartentext,
                        kategorieId = kategorie.id,
                    )
                },
        )
    }
}
