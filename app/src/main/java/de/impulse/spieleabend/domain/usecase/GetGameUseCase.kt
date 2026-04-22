package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class GetGameUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    operator fun invoke(gameId: String): Spiel = repository.getGame(gameId)
}
