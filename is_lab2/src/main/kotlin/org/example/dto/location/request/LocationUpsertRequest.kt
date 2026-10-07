package org.example.dto.location.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class LocationUpsertRequest(
    @field:NotNull
    val x: Double? = null,

    @field:NotNull
    val y: Int? = null,

    @field:NotNull
    val z: Double? = null,

    @field:Size(max = 389)
    val name: String? = null,
)

