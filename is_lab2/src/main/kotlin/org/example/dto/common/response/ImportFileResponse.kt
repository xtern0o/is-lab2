package org.example.dto.common.response

data class ImportFileResponse(
    val moviesCount: Int = 0,
    val personsCount: Int = 0,
    val coordinatesCount: Int = 0,
    val locationsCount: Int = 0,
)