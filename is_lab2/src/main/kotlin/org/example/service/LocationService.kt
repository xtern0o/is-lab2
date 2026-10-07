package org.example.service

import org.example.dto.location.request.LocationUpsertRequest
import org.example.dto.location.response.LocationResponse
import org.example.entity.Location
import org.example.entity.toResponse
import org.example.repository.LocationRepository
import org.example.repository.PersonRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val personRepository: PersonRepository
) {
    @Transactional(readOnly = true)
    fun getPage(
        nameFilter: String?,
        pageable: Pageable
    ): Page<LocationResponse> {
        val locations = if (nameFilter.isNullOrBlank()) {
            locationRepository.findAll(pageable)
        } else {
            locationRepository.findByNameContainingIgnoreCase(nameFilter, pageable)
        }

        return locations.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getById(id: Int): LocationResponse =
        findLocation(id).toResponse()

    @Transactional
    fun create(request: LocationUpsertRequest): LocationResponse {
        val location = Location(
            x = requireNotNull(request.x),
            y = requireNotNull(request.y),
            z = requireNotNull(request.z),
            name = request.name,
        )

        return locationRepository.save(location).toResponse()
    }

    @Transactional
    fun update(
        id: Int,
        request: LocationUpsertRequest,
    ): LocationResponse =
        findLocation(id).apply {
            x = requireNotNull(request.x)
            y = requireNotNull(request.y)
            z = requireNotNull(request.z)
            name = request.name
        }.toResponse()

    @Transactional
    fun delete(id: Int) {
        findLocation(id)

        if (personRepository.existsByLocationId(id)) {
            throw IllegalStateException(
                "не могу удалить локацию, так как на нее кто-то ссылается",
            )
        }

        locationRepository.deleteById(id)
    }

    private fun findLocation(id: Int): Location =
        locationRepository.findById(id).orElseThrow {
            NoSuchElementException("нет локации с id=$id :(")
        }

}