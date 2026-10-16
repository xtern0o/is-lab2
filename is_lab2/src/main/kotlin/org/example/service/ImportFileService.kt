package org.example.service

import org.example.dto.common.request.EntityImportFileRequest
import org.example.dto.common.response.ImportFileResponse
import org.example.dto.coordinates.request.CoordinatesImportRequest
import org.example.dto.coordinates.request.CoordinatesUpsertRequest
import org.example.dto.location.request.LocationImportRequest
import org.example.dto.location.request.LocationUpsertRequest
import org.example.dto.movie.request.MovieImportRequest
import org.example.dto.movie.request.MovieUpsertRequest
import org.example.dto.movie.response.MovieResponse
import org.example.dto.person.request.PersonImportRequest
import org.example.dto.person.request.PersonUpsertRequest
import org.example.dto.person.response.PersonResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ImportFileService(
    private val movieService: MovieService,
    private val personService: PersonService,
    private val coordinatesService: CoordinatesService,
    private val locationService: LocationService,
) {

    @Transactional
    fun importEntities(
        request: EntityImportFileRequest,
    ): ImportFileResponse {
        var moviesCount = 0
        var personsCount = 0
        var coordinatesCount = 0
        var locationsCount = 0

        request.locations.forEach {
            locationService.create(it.toUpsertRequest())
            locationsCount++
        }

        request.coordinates.forEach{
            coordinatesService.create(it.toUpsertRequest())
            coordinatesCount++
        }

        request.persons.forEach {
            createPerson(it)
            personsCount++
        }

        request.movies.forEach {
            createMovie(it)
            moviesCount++
        }

        return ImportFileResponse(
            moviesCount,
            personsCount,
            coordinatesCount,
            locationsCount,
        )

    }

    private fun createPerson(
        request: PersonImportRequest
    ): PersonResponse {
        val location = request.location?.let {
            locationService.create(it.toUpsertRequest())
        }

        return personService.create(
            PersonUpsertRequest(
                name = request.name,
                eyeColor = request.eyeColor,
                hairColor = request.hairColor,
                locationId = location?.id,
                height = request.height,
                nationality = request.nationality,
            ),
        )
    }

    private fun createMovie(
        request: MovieImportRequest
    ): MovieResponse {
        val coordinates = coordinatesService.create(
            request.coordinates!!.toUpsertRequest(),
        )

        val director = createPerson(request.director!!)
        val screenwriter = request.screenwriter?.let(::createPerson)
        val operator = request.operator?.let(::createPerson)

        return movieService.create(
            MovieUpsertRequest(
                name = request.name,
                coordinatesId = coordinates.id,
                oscarsCount = request.oscarsCount,
                budget = request.budget,
                totalBoxOffice = request.totalBoxOffice,
                mpaaRating = request.mpaaRating,
                directorId = director.id,
                screenwriterId = screenwriter?.id,
                operatorId = operator?.id,
                length = request.length,
                goldenPalmCount = request.goldenPalmCount,
                genre = request.genre,
            ),
        )
    }

}

private fun CoordinatesImportRequest.toUpsertRequest() =
    CoordinatesUpsertRequest(
        x = x,
        y = y,
    )

private fun LocationImportRequest.toUpsertRequest() =
    LocationUpsertRequest(
        x = x,
        y = y,
        z = z,
        name = name,
    )