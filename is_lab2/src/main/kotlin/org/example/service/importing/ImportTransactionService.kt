package org.example.service.importing

import org.example.dto.importing.request.EntityImportFileRequest
import org.example.dto.importing.response.ImportHistoryResponse
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
import org.example.entity.enums.ImportStatus
import org.example.entity.toResponse
import org.example.repository.ImportHistoryRepository
import org.example.service.location.LocationService
import org.example.service.movie.MovieService
import org.example.service.person.PersonService
import org.example.service.coordinates.CoordinatesService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Сервис для сохранения данных из файла
 */
@Service
class ImportTransactionService(
    private val movieService: MovieService,
    private val personService: PersonService,
    private val coordinatesService: CoordinatesService,
    private val locationService: LocationService,
    private val importHistoryRepository: ImportHistoryRepository,
) {

    /**
     * Импорт данных и обновление статуса
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    fun importEntities(
        request: EntityImportFileRequest,
        historyId: UUID,
    ): ImportHistoryResponse {
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

        val history = importHistoryRepository.findById(historyId).orElseThrow()

        history.apply {
            status = ImportStatus.SUCCESS
            this.moviesCount = moviesCount
            this.personsCount = personsCount
            this.coordinatesCount = coordinatesCount
            this.locationsCount = locationsCount
        }

        return history.toResponse()
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