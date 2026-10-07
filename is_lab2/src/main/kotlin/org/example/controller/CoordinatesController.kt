package org.example.controller

import jakarta.validation.Valid
import org.example.dto.coordinates.request.CoordinatesUpsertRequest
import org.example.dto.coordinates.response.CoordinatesResponse
import org.example.service.CoordinatesService
import org.example.service.DatabaseEventsService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/coordinates")
class CoordinatesController(
    private val coordinatesService: CoordinatesService,
    private val eventsService: DatabaseEventsService,
) {
    @GetMapping
    fun getPage(
        @PageableDefault(page = 0, size = 20, sort = ["id"])
        pageable: Pageable
    ): Page<CoordinatesResponse> =
        coordinatesService.getPage(pageable)

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Int,
    ): CoordinatesResponse =
        coordinatesService.getById(id)

    @PostMapping
    fun create(
        @Valid @RequestBody request: CoordinatesUpsertRequest,
    ): ResponseEntity<CoordinatesResponse> {
        val coordinates = coordinatesService.create(request)
        eventsService.publish("coordinates")

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(coordinates)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Int,
        @Valid @RequestBody request: CoordinatesUpsertRequest,
    ): CoordinatesResponse {
        val coordinates = coordinatesService.update(id, request)
        eventsService.publish("coordinates")

        return coordinates
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Int,
    ): ResponseEntity<Void> {
        coordinatesService.delete(id)
        eventsService.publish("coordinates")

        return ResponseEntity.noContent().build() // 204
    }

}