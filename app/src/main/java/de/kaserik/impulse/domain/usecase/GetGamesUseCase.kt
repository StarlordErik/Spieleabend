package de.kaserik.impulse.domain.usecase

import de.kaserik.impulse.domain.model.Spiel
import de.kaserik.impulse.domain.repository.GameRepository
import javax.inject.Inject

class GetGamesUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke(): List<Spiel> = repository.getGames()
}
