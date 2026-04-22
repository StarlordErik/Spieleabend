package de.impulse.spieleabend.domain.repository

import de.impulse.spieleabend.domain.model.Spiel

@Suppress("kotlin:S6517")
interface GameRepository {
    suspend fun getGame(gameId: String): Spiel
}
