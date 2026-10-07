package org.example.service

import org.example.dto.person.request.PersonUpsertRequest
import org.example.dto.person.response.PersonResponse
import org.example.entity.Location
import org.example.entity.Person
import org.example.entity.toResponse
import org.example.repository.LocationRepository
import org.example.repository.MovieRepository
import org.example.repository.PersonRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PersonService(
    private val personRepository: PersonRepository,
    private val locationRepository: LocationRepository,
    private val movieRepository: MovieRepository
) {

    @Transactional(readOnly = true)
    fun getPage(nameFilter: String?, pageable: Pageable): Page<PersonResponse> {
        val persons = if (nameFilter.isNullOrBlank()) {
            personRepository.findAll(pageable)
        } else {
            personRepository.findByNameContainingIgnoreCase(nameFilter, pageable)
        }

        return persons.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getById(id: Int): PersonResponse =
        findPerson(id).toResponse()

    @Transactional
    fun create(request: PersonUpsertRequest): PersonResponse {
        val person = Person(
            name = requireNotNull(request.name),
            eyeColor = request.eyeColor,
            hairColor = requireNotNull(request.hairColor),
            location = request.locationId?.let(::findLocation),
            height = requireNotNull(request.height),
            nationality = request.nationality,
        )

        return personRepository.save(person).toResponse()
    }

    @Transactional
    fun update(id: Int, request: PersonUpsertRequest): PersonResponse =
        findPerson(id).apply {
            name = requireNotNull(request.name)
            eyeColor = request.eyeColor
            hairColor = requireNotNull(request.hairColor)
            location = request.locationId?.let(::findLocation)
            height = requireNotNull(request.height)
            nationality = request.nationality
        }.toResponse()

    @Transactional
    fun delete(id: Int) {
        findPerson(id)

        if (
            movieRepository.existsByDirectorId(id) ||
            movieRepository.existsByScreenwriterId(id) ||
            movieRepository.existsByOperatorId(id)
        ) {
            throw IllegalStateException(
                "не получится удалить - на человека ссылается какой-то фильм :(",
            )
        }

        personRepository.deleteById(id)
    }



    private fun findPerson(id: Int): Person =
        personRepository.findOneById(id)
            ?: throw NoSuchElementException("человек с id=$id НЕ найден...")


    private fun findLocation(id: Int): Location =
        locationRepository.findById(id).orElseThrow {
            NoSuchElementException("Локация с id=$id не найдена.")
        }
}