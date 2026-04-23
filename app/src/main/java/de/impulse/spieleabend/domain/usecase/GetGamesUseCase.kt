package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.Spiel
import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class GetGamesUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke(): List<Spiel> = repository.getGames()
}
