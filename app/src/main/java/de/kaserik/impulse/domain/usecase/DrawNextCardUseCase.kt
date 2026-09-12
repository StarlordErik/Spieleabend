package de.kaserik.impulse.domain.usecase

import javax.inject.Inject

class DrawNextCardUseCase @Inject constructor(
    private val drawFromCategory: DrawNextCardFromCategoryUseCase,
    private val drawRandom: DrawNextRandomCardUseCase,
) {
    suspend operator fun invoke(gameId: Int, categoryId: Int?): DrawCardResult =
        if (categoryId == null) drawRandom(gameId) else drawFromCategory(gameId, categoryId)
}
