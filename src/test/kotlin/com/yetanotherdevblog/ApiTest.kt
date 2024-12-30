package com.yetanotherdevblog

import com.yetanotherdevblog.petclinic.model.Owner
import com.yetanotherdevblog.petclinic.repositories.PetRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.kotlin.test.test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTest {

    @LocalServerPort
    var port: Int? = null

    lateinit var client: WebClient

    @Autowired
    lateinit var petRepository: PetRepository

    @BeforeEach
    fun setup() {
        client = WebClient.create("http://localhost:$port")
    }

    @Test
    fun `API call for Owners`() {
        client.get().uri("/api/owners").accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Owner::class.java)
                .test()
                .consumeNextWith {
                    assertEquals("James", it.firstName)
                }
                .verifyComplete()

        client.get().uri("/api/owners/5bead0d3-cd7b-41e5-b064-09f48e5e6a08").accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Owner::class.java)
                .test()
                .consumeNextWith {
                    assertEquals("James", it.firstName)
                    assertEquals("Owner", it.lastName)
                }
                .verifyComplete()
    }
}