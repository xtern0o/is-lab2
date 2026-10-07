package org.example.service

import org.example.dto.coordinates.request.CoordinatesUpsertRequest
import org.example.dto.coordinates.response.CoordinatesResponse
import org.example.entity.Coordinates
import org.example.entity.toResponse
import org.example.repository.CoordinatesRepository
import org.example.repository.MovieRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CoordinatesService(
    private val coordinatesRepository: CoordinatesRepository,
    private val movieRepository: MovieRepository
) {
    @Transactional(readOnly = true)
    fun getPage(pageable: Pageable): Page<CoordinatesResponse> =
        coordinatesRepository.findAll(pageable).map { it.toResponse() }

    fun getById(id: Int): CoordinatesResponse =
        findCoordinates(id).toResponse()

    @Transactional
    fun create(request: CoordinatesUpsertRequest): CoordinatesResponse {
        val coordinates = Coordinates(
            x = requireNotNull(request.x),
            y = requireNotNull(request.y),
        )

        return coordinatesRepository.save(coordinates).toResponse()
    }

    @Transactional
    fun update(
        id: Int,
        request: CoordinatesUpsertRequest,
    ): CoordinatesResponse =
        findCoordinates(id).apply {
            x = requireNotNull(request.x)
            y = requireNotNull(request.y)
        }.toResponse()

    @Transactional
    fun delete(id: Int) {
        findCoordinates(id)

        if (movieRepository.existsByCoordinatesId(id)) {
            throw IllegalStateException(
                "к сожалению не получитчя удалить координаты - на них ссылается какой-то фильм",
            )
        }

        coordinatesRepository.deleteById(id)
    }

    private fun findCoordinates(id: Int): Coordinates =
        coordinatesRepository
            .findById(id)
            .orElseThrow {
                NoSuchElementException("координат с id=$id не найдено :(")
            }
}