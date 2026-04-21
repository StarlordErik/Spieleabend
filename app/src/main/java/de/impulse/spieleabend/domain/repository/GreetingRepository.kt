package de.impulse.spieleabend.domain.repository

import de.impulse.spieleabend.domain.model.Greeting

@Suppress("kotlin:S6517")
interface GreetingRepository {
    fun getGreeting(): Greeting
}
