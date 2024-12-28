package com.yetanotherdevblog.petclinic.handlers

import com.yetanotherdevblog.petclinic.html
import com.yetanotherdevblog.petclinic.model.Visit
import com.yetanotherdevblog.petclinic.repositories.OwnersRepository
import com.yetanotherdevblog.petclinic.repositories.PetRepository
import com.yetanotherdevblog.petclinic.repositories.VisitRepository
import com.yetanotherdevblog.petclinic.toLocalDate
import com.yetanotherdevblog.petclinic.toStr
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import java.util.*

@Component
class VisitHandler(val visitRepository: VisitRepository,
                   val petRepository: PetRepository,
                   val ownersRepository: OwnersRepository,
                   val ownersHandler: OwnersHandler) {

    fun addPage(serverRequest: ServerRequest) =
            petRepository.findById(
                    serverRequest.queryParam("petId").orElseThrow { IllegalArgumentException() })
                .flatMap { pet ->
                    ok().html().render("visits/add", mapOf(
                        "owner" to ownersRepository.findById(pet.owner),
                        "pet" to pet))
                }

    fun add(serverRequest: ServerRequest) =
            serverRequest.body(BodyExtractors.toFormData())
                    .flatMap {
                        val formData = it.toSingleValueMap()
                        visitRepository.save(Visit(
                                id = UUID.randomUUID().toString(),
                                description = formData["description"]!!,
                                petId =  formData["petId"]!!,
                                visitDate = formData["date"]!!.toLocalDate()))
                    }
                    .then(ownersHandler.indexPage())

    fun editPage(serverRequest: ServerRequest) =
        serverRequest.queryParam("id")
            .map { visitId -> visitRepository.findById(visitId) }
            .orElse(Mono.error(IllegalArgumentException("Visit ID is required")))
            .flatMap { visit ->
                petRepository.findById(visit.petId)
                    .zipWith(ownersRepository.findById(visit.petId)) // Combine pet and owner
                    .flatMap { tuple ->
                        val pet = tuple.t1 // Access the first element of Tuple2
                        val owner = tuple.t2 // Access the second element of Tuple2
                        val model = mapOf(
                            "id" to visit.id,
                            "date" to visit.visitDate.toStr(),
                            "description" to visit.description,
                            "pet" to pet,
                            "owner" to owner
                        )
                        ok().render("visits/edit", model)
                    }
            }

    fun edit(serverRequest: ServerRequest) =
            serverRequest.body(BodyExtractors.toFormData())
                    .flatMap {
                        val formData = it.toSingleValueMap()
                        visitRepository.save(Visit(
                                id = formData["id"]!!,
                                visitDate = formData["date"]!!.toLocalDate(),
                                petId = formData["petId"]!!,
                                description = formData["description"]!!))
                    }
                    .then(ownersHandler.indexPage())

}
