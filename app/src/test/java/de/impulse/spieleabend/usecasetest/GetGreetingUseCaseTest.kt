package de.impulse.spieleabend.usecasetest

import de.impulse.spieleabend.domain.model.Greeting
import de.impulse.spieleabend.domain.repository.GreetingRepository
import de.impulse.spieleabend.domain.usecase.GetGreetingUseCase
import org.junit.Assert
import org.junit.Test

class GetGreetingUseCaseTest {
    @Test
    fun returnsHelloWorldGreeting() {
        val useCase = GetGreetingUseCase(
            repository = object : GreetingRepository {
                override fun getGreeting(): Greeting = Greeting("Hello World!")
            },
        )

        Assert.assertEquals("Hello World!", useCase().text)
    }
}
