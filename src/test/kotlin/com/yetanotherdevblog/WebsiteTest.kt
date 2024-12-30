package com.yetanotherdevblog

import com.yetanotherdevblog.petclinic.model.Owner
import com.yetanotherdevblog.petclinic.repositories.OwnersRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.kotlin.test.test
import java.time.Duration
import java.util.*
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebsiteTest {

    @LocalServerPort
    var port: Int? = null

    lateinit var client: WebClient

    @Autowired
    lateinit var ownerRepository: OwnersRepository

    @BeforeEach
    fun setup() {
        client = WebClient.create("http://localhost:$port")
    }

    @Test
    fun home() {
        client.get().uri("/").accept(MediaType.TEXT_HTML)
                .exchange()
                .test()
                .expectNextMatches { it.statusCode() == HttpStatus.OK }
                .verifyComplete()
    }

    @Test
    fun owners() {
        client.get().uri("/owners").accept(MediaType.TEXT_HTML)
                .exchange()
                .test()
                .expectNextMatches { it.statusCode() == HttpStatus.OK }
                .verifyComplete()
    }

    @Test
    fun pets() {

        val ownerId = UUID.randomUUID().toString()

        ownerRepository.save(Owner(
                id=ownerId,
                firstName = "Lorem",
                lastName = "Ipsum",
                address = "LoremIpsum",
                city = "IpsumLorem",
                telephone = "1111"
        )).block(Duration.ofSeconds(2))

        client.get().uri("/pets/add?ownerId=$ownerId").accept(MediaType.TEXT_HTML)
                .exchange()
                .test()
                .expectNextMatches { it.statusCode() == HttpStatus.OK }
                .verifyComplete()
    }

    @Test
    fun vets() {
        client.get().uri("/vets").accept(MediaType.TEXT_HTML)
                .exchange()
                .test()
                .expectNextMatches { it.statusCode() == HttpStatus.OK }
                .verifyComplete()
    }

    @Test
    fun specialities() {
        client.get().uri("/specialities").accept(MediaType.TEXT_HTML)
                .exchange()
                .test()
                .expectNextMatches { it.statusCode() == HttpStatus.OK }
                .verifyComplete()
    }

    @Test
    fun petTypes() {
        client.get().uri("/petTypes").accept(MediaType.TEXT_HTML)
                .exchange()
                .test()
                .expectNextMatches { it.statusCode() == HttpStatus.OK }
                .verifyComplete()
    }

}