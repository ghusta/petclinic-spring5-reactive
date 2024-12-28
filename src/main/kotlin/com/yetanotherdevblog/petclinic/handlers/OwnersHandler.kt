package com.yetanotherdevblog.petclinic.handlers

import com.yetanotherdevblog.petclinic.html
import com.yetanotherdevblog.petclinic.model.Owner
import com.yetanotherdevblog.petclinic.model.Pet
import com.yetanotherdevblog.petclinic.model.Visit
import com.yetanotherdevblog.petclinic.repositories.OwnersRepository
import com.yetanotherdevblog.petclinic.repositories.PetRepository
import com.yetanotherdevblog.petclinic.repositories.PetTypeRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import java.util.*

@Component
class OwnersHandler(val ownersRepository: OwnersRepository,
                    val petRepository: PetRepository,
                    val petTypeRepository: PetTypeRepository,
                    val mongoTemplate: ReactiveMongoTemplate) {

    fun indexPage(serverRequest: ServerRequest) =
            serverRequest.queryParam("q").filter { it.trim().isNotEmpty() }
                    .map { indexPageWithQuery(it) }
                    .orElse(indexPage())

    fun addPage(serverRequest: ServerRequest) = ok().html().render("owners/add")

    fun editPage(serverRequest: ServerRequest) =
            serverRequest.queryParam("id")
                    .map { ownersRepository.findById(it) }
                    .orElse(Mono.empty<Owner>())
                    .map { mapOf("id" to it.id,
                          "firstName" to it.firstName,
                           "lastName" to it.lastName,
                            "address" to it.address,
                               "city" to it.city,
                          "telephone" to it.telephone)
                    }
                    .flatMap { ok().html().render("owners/edit", it) }

    fun view(serverRequest: ServerRequest) =
        serverRequest.queryParam("id")
            .map { id -> ownersRepository.findById(id) }
            .orElse(Mono.empty())
            .flatMap { owner ->
                petRepository.findAllByOwner(owner.id)
                    .collectList()
                    .flatMap { pets ->
                        val model = mapOf(
                            "owner" to owner,
                            "pets" to pets,
                            "petTypes" to petTypeRepository.findAll()
                                .collectMap({ it.id }, { it.name }),
                            "petVisits" to petVisits(pets.map { it.id })
                        )
                        ok().html().render("owners/view", model)
                    }
            }
            .switchIfEmpty(ServerResponse.notFound().build())

    fun petVisits(petIds: List<String>) =
            mongoTemplate.find(Query(where("petId").`in`(petIds) ), Visit::class.java)
                    .collectMultimap { it.petId }

    fun add(serverRequest: ServerRequest) = serverRequest.body(BodyExtractors.toFormData())
            .flatMap {
                val formData = it.toSingleValueMap()
                ownersRepository.save(Owner(
                               id = formData["id"] ?: UUID.randomUUID().toString(),
                        firstName = formData["firstName"]!!,
                         lastName = formData["lastName"]!!,
                          address = formData["address"]!!,
                        telephone = formData["telephone"]!!,
                             city = formData["city"]!!))
            }
            .then(indexPage())

    fun edit(serverRequest: ServerRequest) =
            serverRequest.queryParam("id").map { ownersRepository.findById(it) }.orElse(Mono.empty<Owner>())
                .flatMap { ownersRepository.save(it) }
                .flatMap { ok().render("owners/edit", it) }

    fun indexPageWithQuery(query:String) = ok().html().render("owners/index",
                mapOf("owners" to findByNameLike(query)
                        .map { Pair(it, emptySet<Pet>()) },
                        "pets" to petRepository.findAll().collectMultimap { it.owner }))

    fun indexPage(): Mono<ServerResponse> = ok().html().render("owners/index",
            mapOf("owners" to ownersRepository.findAll().map { Pair(it, emptySet<Pet>()) },
                    "pets" to petRepository.findAll().collectMultimap { it.owner }))

    fun findByNameLike(query:String) = mongoTemplate.find(
            Query(Criteria().orOperator(
                    where("firstName").regex(query, "i"),
                    where("lastName").regex(query, "i"))), Owner::class.java)

}