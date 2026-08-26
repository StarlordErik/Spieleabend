package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.repository.GameRepository
import javax.inject.Inject

class SetTextsPerCardUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    suspend operator fun invoke(gameId: Int, value: Int) {
        require(value in MIN_TEXTS_PER_CARD..MAX_TEXTS_PER_CARD)
        repository.setTextsPerCardOverride(gameId, value)
    }
}

private const val MIN_TEXTS_PER_CARD = 1
private const val MAX_TEXTS_PER_CARD = 5
