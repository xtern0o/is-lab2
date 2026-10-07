package org.example.service

import org.example.dto.movie.request.AwardOscarsByLengthRequest
import org.example.dto.movie.request.MovieUpsertRequest
import org.example.dto.movie.response.AwardOscarsResponse
import org.example.dto.movie.response.MovieResponse
import org.example.dto.movie.response.OscarsCountGroupResponse
import org.example.entity.Coordinates
import org.example.entity.Movie
import org.example.entity.Person
import org.example.entity.toResponse
import org.example.repository.CoordinatesRepository
import org.example.repository.LocationRepository
import org.example.repository.MovieRepository
import org.example.repository.PersonRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MovieService(
    private val movieRepository: MovieRepository,
    private val locationRepository: LocationRepository,
    private val personRepository: PersonRepository,
    private val coordinatesRepository: CoordinatesRepository
) {
    @Transactional(readOnly = true)
    fun getPage(
        nameFilter: String?,
        pageable: Pageable,
    ): Page<MovieResponse> {
        val movies = if (nameFilter.isNullOrBlank()) {
            movieRepository.findAll(pageable)
        } else {
            movieRepository.findByNameContainingIgnoreCase(nameFilter, pageable)
        }

        return movies.map { it.toResponse() }
    }

    fun getById(id: Int): MovieResponse = findMovieDetailed(id).toResponse()

    @Transactional
    fun create(request: MovieUpsertRequest): MovieResponse {
        val movie = Movie(
            name = requireNotNull(request.name),
            coordinates = findCoordinates(requireNotNull(request.coordinatesId)),
            oscarsCount = requireNotNull(request.oscarsCount),
            budget = requireNotNull(request.budget),
            totalBoxOffice = requireNotNull(request.totalBoxOffice),
            mpaaRating = request.mpaaRating,
            director = findPerson(requireNotNull(request.directorId)),
            screenwriter = request.screenwriterId?.let(::findPerson),
            operator = request.operatorId?.let(::findPerson),
            length = request.length,
            goldenPalmCount = requireNotNull(request.goldenPalmCount),
            genre = request.genre,
        )

        return movieRepository.save(movie).toResponse()
    }

    @Transactional
    fun update(
        id: Int,
        request: MovieUpsertRequest,
    ): MovieResponse =
        findMovieDetailed(id).apply {
            name = requireNotNull(request.name)
            coordinates = findCoordinates(requireNotNull(request.coordinatesId))
            oscarsCount = requireNotNull(request.oscarsCount)
            budget = requireNotNull(request.budget)
            totalBoxOffice = requireNotNull(request.totalBoxOffice)
            mpaaRating = request.mpaaRating
            director = findPerson(requireNotNull(request.directorId))
            screenwriter = request.screenwriterId?.let(::findPerson)
            operator = request.operatorId?.let(::findPerson)
            length = request.length
            goldenPalmCount = requireNotNull(request.goldenPalmCount)
            genre = request.genre
        }.toResponse()

    @Transactional
    fun delete(id: Int) {
        findMovieDetailed(id)
        movieRepository.deleteById(id)
    }

    /**
     * Найти фильмы, содержащие подстроку в названии
     */
    @Transactional(readOnly = true)
    fun findByName(name: String): List<MovieResponse> =
        movieRepository
            .findByNameContainingIgnoreCase(name, Pageable.unpaged())
            .content
            .map { it.toResponse() }

    /**
     * Найти фильмы, где goldenPalmCount < заданного
     */
    @Transactional(readOnly = true)
    fun findByGoldenPalmCountLessThan(value: Long): List<MovieResponse> =
        movieRepository.findByGoldenPalmCountLessThan(value, Pageable.unpaged())
            .content
            .map { it.toResponse() }

    /**
     * Найти фильмы без оскара
     */
    @Transactional(readOnly = true)
    fun findWithoutOscars(): List<MovieResponse> =
        movieRepository
            .findByOscarsCount(0, Pageable.unpaged())
            .content
            .map { it.toResponse() }

    @Transactional
    fun awardOscars(request: AwardOscarsByLengthRequest): AwardOscarsResponse {
        val updatedCount = movieRepository.addOscarsToMoviesLongerThan(
            length = requireNotNull(request.minimumLength),
            amountOfOscars = requireNotNull(request.additionalOscars),
        )

        return AwardOscarsResponse(updatedMoviesCount = updatedCount)
    }

    @Transactional(readOnly = true)
    fun countGroupedByOscars(): List<OscarsCountGroupResponse> =
        movieRepository.countGroupedByOscars().map {
            OscarsCountGroupResponse(
                oscarsCount = it.oscarsCount,
                movieCount = it.movieCount,
            )
        }


    private fun findMovieDetailed(id: Int): Movie =
        movieRepository.findDetailedById(id)
            ?: throw NoSuchElementException("фильм с id=$id не найден!!")

    private fun findCoordinates(id: Int): Coordinates =
        coordinatesRepository.findById(id).orElseThrow {
            throw NoSuchElementException("нет координат с id=$id")
        }

    private fun findPerson(id: Int): Person =
        personRepository.findById(id).orElseThrow {
            throw NoSuchElementException("нет человека с id=$id :(")
        }

}