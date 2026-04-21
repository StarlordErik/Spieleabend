package de.impulse.spieleabend.data

import de.impulse.spieleabend.domain.model.Greeting
import de.impulse.spieleabend.domain.repository.GreetingRepository
import javax.inject.Inject

class GreetingRepositoryImpl @Inject constructor() : GreetingRepository {
    override fun getGreeting(): Greeting = Greeting("Hello World!")
}
