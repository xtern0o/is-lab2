package org.example.controller

import jakarta.validation.Valid
import org.example.dto.movie.request.AwardOscarsByLengthRequest
import org.example.dto.movie.response.MovieResponse
import org.example.dto.movie.request.MovieUpsertRequest
import org.example.dto.movie.response.AwardOscarsResponse
import org.example.dto.movie.response.OscarsCountGroupResponse
import org.example.service.DatabaseEventsService
import org.example.service.MovieService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/movies")
class MovieController(
    private val movieService: MovieService,
    private val eventsService: DatabaseEventsService,
) {
    @GetMapping
    fun getPage(
        @RequestParam(required = false) name: String?,
        @PageableDefault(size = 20, sort = ["id"])
        pageable: Pageable,
    ): Page<MovieResponse> =
        movieService.getPage(name, pageable)

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Int,
    ): MovieResponse =
        movieService.getById(id)

    @PostMapping
    fun create(
        @Valid @RequestBody request: MovieUpsertRequest,
    ): ResponseEntity<MovieResponse> {
        val movie = movieService.create(request)
        eventsService.publish("movies")

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(movie)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Int,
        @Valid @RequestBody request: MovieUpsertRequest,
    ): MovieResponse {
        val movie = movieService.update(id, request)
        eventsService.publish("movies")

        return movie
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Int,
    ): ResponseEntity<Void> {
        movieService.delete(id)
        eventsService.publish("movies")

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/search/{name}")
    fun getByContainsName(
        @PathVariable name: String
    ): List<MovieResponse> =
        movieService.findByName(name)

    @GetMapping("/golden-palm-count-less-than/{goldenPalmCount}")
    fun getByGoldenPalmCountLessThan(
        @PathVariable goldenPalmCount: Long,
    ): List<MovieResponse> =
        movieService.findByGoldenPalmCountLessThan(goldenPalmCount)

    @GetMapping("/without-oscars")
    fun getWithoutOscars(): List<MovieResponse> =
        movieService.findWithoutOscars()

    @PostMapping("/actions/award-oscars")
    fun awardOscars(
        @Valid @RequestBody request: AwardOscarsByLengthRequest,
    ): AwardOscarsResponse {
        val result = movieService.awardOscars(request)
        eventsService.publish("movies")

        return result
    }

    @GetMapping("/stats/by-oscars-count")
    fun countGroupedByOscars(): List<OscarsCountGroupResponse> =
        movieService.countGroupedByOscars()


}
