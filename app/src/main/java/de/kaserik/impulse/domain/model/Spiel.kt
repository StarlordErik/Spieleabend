package de.kaserik.impulse.domain.model

import de.kaserik.impulse.common.AppMessages

data class Spiel(
    override val lokalisierung: Lokalisierung,
    val originaleKategorien: Set<Kategorie> = emptySet(),
    val hinzugefuegteKategorien: Set<Kategorie> = emptySet(),
    val inaktiveKategorien: Set<Kategorie> = emptySet(),
    override val inaktiv: Boolean = false,
    override val selbstErstellt: Boolean = false,
    override val favorit: Boolean = false,
    val bildDateiname: String? = null,
    val texteProKarte: Int = 1,
    val standardTexteProKarte: Int = texteProKarte,
    val geloeschteKartentexteModus: GeloeschteKartentexteModus = GeloeschteKartentexteModus.ALS_LETZTE,
    val favoritenModus: FavoritenModus = FavoritenModus.UNBEACHTET,
    val bearbeiteteKartentexteModus: BearbeiteteKartentexteModus = BearbeiteteKartentexteModus.UNBEACHTET,
) : Spielelement(lokalisierung, inaktiv, selbstErstellt, favorit) {
    val kategorien: Set<Kategorie>
        get() {
            val inaktiveKategorieIds = inaktiveKategorien.map { kategorie -> kategorie.id() }.toSet()

            return (originaleKategorien + hinzugefuegteKategorien)
                .filterNot { kategorie -> kategorie.id() in inaktiveKategorieIds }
                .toSet()
        }

    init {
        require(texteProKarte > 0) {
            AppMessages.EmptyCard
        }
        require(standardTexteProKarte > 0) {
            AppMessages.EmptyDefaultCard
        }

        val originaleKategorieIds = originaleKategorien.map { kategorie -> kategorie.id() }
        require(originaleKategorieIds.distinct().size == originaleKategorieIds.size) {
            AppMessages.DuplicateOriginalCategory
        }

        val hinzugefuegteKategorieIds = hinzugefuegteKategorien.map { kategorie -> kategorie.id() }
        require(hinzugefuegteKategorieIds.distinct().size == hinzugefuegteKategorieIds.size) {
            AppMessages.DuplicateAddedCategory
        }

        val inaktiveKategorieIds = inaktiveKategorien.map { kategorie -> kategorie.id() }
        require(inaktiveKategorieIds.distinct().size == inaktiveKategorieIds.size) {
            AppMessages.DuplicateInactiveCategory
        }

        val doppelteKategorieIds = originaleKategorieIds.intersect(hinzugefuegteKategorieIds.toSet())
        require(doppelteKategorieIds.isEmpty()) {
            AppMessages.OverlappingCategories
        }

        val bekannteKategorieIds = (originaleKategorieIds + hinzugefuegteKategorieIds).toSet()
        require(inaktiveKategorieIds.all { kategorieId -> kategorieId in bekannteKategorieIds }) {
            AppMessages.UnknownInactiveCategory
        }

        val kategorieIds = bekannteKategorieIds.toList()
        require(kategorieIds.distinct().size == kategorieIds.size) {
            AppMessages.DuplicateCategory
        }
    }
}
