package org.example.service.coordinates

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
        validateUpsert(request)

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
    ): CoordinatesResponse {
        validateUpsert(request, excludedId = id)

        return findCoordinates(id).apply {
            x = requireNotNull(request.x)
            y = requireNotNull(request.y)
        }.toResponse()
    }


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

    private fun validateUpsert(
        request: CoordinatesUpsertRequest,
        excludedId: Int? = null,
    ) {
        val x = requireNotNull(request.x) {
            "x обязателен"
        }
        val y = requireNotNull(request.y) {
            "y обязателен"
        }

        val exists = if (excludedId == null) {
            coordinatesRepository.existsByXAndY(x, y)
        } else {
            coordinatesRepository.existsByXAndYAndIdNot(x, y, excludedId)
        }

        if (exists) {
            throw IllegalStateException(
                "coordinates with x=$x and y=$y already exist",
            )
        }
    }

    private fun findCoordinates(id: Int): Coordinates =
        coordinatesRepository
            .findById(id)
            .orElseThrow {
                NoSuchElementException("координат с id=$id не найдено :(")
            }
}