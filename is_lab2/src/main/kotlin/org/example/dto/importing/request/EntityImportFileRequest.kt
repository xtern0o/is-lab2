package org.example.dto.importing.request

import jakarta.validation.Valid
import org.example.dto.coordinates.request.CoordinatesImportRequest
import org.example.dto.location.request.LocationImportRequest
import org.example.dto.movie.request.MovieImportRequest
import org.example.dto.person.request.PersonImportRequest

data class EntityImportFileRequest(
    @field:Valid
    val movies: List<MovieImportRequest> = emptyList(),

    @field:Valid
    val persons: List<PersonImportRequest> = emptyList(),

    @field:Valid
    val coordinates: List<CoordinatesImportRequest> = emptyList(),

    @field:Valid
    val locations: List<LocationImportRequest> = emptyList(),
)
