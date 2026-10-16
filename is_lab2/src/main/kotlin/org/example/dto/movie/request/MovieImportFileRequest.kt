package org.example.dto.movie.request

import jakarta.validation.Valid

data class MovieImportFileRequest(
    @field:Valid
    val movies: List<MovieImportRequest>,
)
