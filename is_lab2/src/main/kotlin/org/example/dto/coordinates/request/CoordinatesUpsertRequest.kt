package org.example.dto.coordinates.request

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class CoordinatesUpsertRequest(
    @field:NotNull
    @field:DecimalMax("455.0")
    val x: Float? = null,

    @field:NotNull
    @field:Min(-123)
    val y: Long? = null,
)
