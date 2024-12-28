package com.yetanotherdevblog

import com.yetanotherdevblog.petclinic.model.*
import com.yetanotherdevblog.petclinic.repositories.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.*

@Component
class DbInitializer(val petTypeRepository: PetTypeRepository,
                    val specialityRepository: SpecialityRepository,
                    val vetRepository: VetRepository,
                    val ownersRepository: OwnersRepository,
                    val petRepository: PetRepository,
                    val visitRepository: VisitRepository): CommandLineRunner {

    val log: Logger = LoggerFactory.getLogger(DbInitializer::class.java)

    override fun run(vararg args: String?) {

        val ownerId = UUID.fromString("5bead0d3-cd7b-41e5-b064-09f48e5e6a08").toString()
        val petId = UUID.fromString("6bead0d3-cd7b-41e5-b064-09f48e5e6a08").toString()
        val secondPetId = UUID.fromString("6bead0d2-cd7b-41e5-b064-09f48e5e6a08").toString()
        val thirdPetId = UUID.fromString("6bead0a3-cd7b-41e5-b064-09f48e5e6a08").toString()
        val dogId = UUID.randomUUID().toString()

        petTypeRepository.deleteAll().subscribeOnComplete {
            val petTypes = listOf("cat", "lizard", "snake", "bird", "hamster", "dog")
                    .map { if (it == "dog") PetType(name=it, id = dogId) else PetType(name = it) }
            petTypeRepository.saveAll(petTypes)
                .subscribeOnComplete { log.info("Added  PetTypes") }
        }


        specialityRepository.deleteAll().subscribeOnComplete {
            val specialities = listOf("radiology", "dentistry", "surgery")
                    .map {Speciality(name = it)}
            specialityRepository.saveAll(specialities)
                .subscribeOnComplete { log.info("Added  Specialities") }
        }

        vetRepository.deleteAll().subscribeOnComplete {
            vetRepository.saveAll(listOf(
                    Vet(firstName = "James", lastName="Carter"),
                    Vet(firstName = "Helen", lastName="Leary", specialities = setOf("radiology")),
                    Vet(firstName = "Linda", lastName="Douglas", specialities = setOf("dentistry", "surgery")),
                    Vet(firstName = "Rafael", lastName="Ortega", specialities = setOf("surgery")),
                    Vet(firstName = "Henry", lastName="Stevens", specialities = setOf("radiology")),
                    Vet(firstName = "Sharon", lastName="Jenkins")))
                .subscribeOnComplete { log.info("Added  Vets") }
        }

        ownersRepository.deleteAll().subscribeOnComplete {
            ownersRepository.saveAll(
                listOf(
                    Owner(
                        firstName = "James", lastName = "Owner",
                        telephone = "+44 4444444", address = "Road St",
                        city = "Serverless",
                        id = ownerId
                    ),
                    Owner(
                        firstName = "John", lastName = "Doe",
                        telephone = "+44 1234567", address = "Church St",
                        city = "London",
                        id = UUID.randomUUID().toString()
                    )
                )
            )
                .subscribeOnComplete { log.info("Added  Owners") }
        }

        petRepository.deleteAll().subscribeOnComplete {
            petRepository.saveAll(listOf(
                    Pet(id = petId, name = "Pet 1", birthDate = LocalDate.now(), type = dogId, owner = ownerId),
                    Pet(id = secondPetId, name = "Pet 2", birthDate = LocalDate.now(), type = dogId, owner = ownerId),
                    Pet(id = thirdPetId, name = "Pet 3", birthDate = LocalDate.now(), type = dogId, owner = ownerId)))
                .subscribeOnComplete { log.info("Added Pets") }
        }

        visitRepository.deleteAll().subscribeOnComplete {
            visitRepository.saveAll(listOf(
                    Visit(visitDate= LocalDate.now(), description = "Visit description ${Random().nextInt()}", petId= petId),
                    Visit(visitDate= LocalDate.now(), description = "Visit description ${Random().nextInt()}", petId= petId),
                    Visit(visitDate= LocalDate.now(), description = "Visit description ${Random().nextInt()}", petId= petId),
                    Visit(visitDate= LocalDate.now(), description = "Visit description ${Random().nextInt()}", petId= secondPetId)))
                .subscribeOnComplete { log.info("Added Visits") }
        }

    }

    /**
     * Subscribe onComplete only. Used by db populators.
     */
    private fun <T> Mono<T>.subscribeOnComplete(completeConsumer: () -> Unit) : Disposable {
        return this.subscribe(null, null, completeConsumer)
    }

    private fun <T> Flux<T>.subscribeOnComplete(completeConsumer: () -> Unit) : Disposable {
        return this.subscribe(null, null, completeConsumer)
    }
}


