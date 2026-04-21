package de.impulse.spieleabend.domain.usecase

import de.impulse.spieleabend.domain.model.Greeting
import de.impulse.spieleabend.domain.repository.GreetingRepository
import javax.inject.Inject

class GetGreetingUseCase @Inject constructor(
    private val repository: GreetingRepository,
) {
    operator fun invoke(): Greeting = repository.getGreeting()
}
