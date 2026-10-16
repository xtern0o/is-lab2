package org.example.dto.person.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.example.dto.location.request.LocationImportRequest
import org.example.entity.enums.Color
import org.example.entity.enums.Country

data class PersonImportRequest(
    @field:NotBlank
    val name: String? = null,

    val eyeColor: Color? = null,

    @field:NotNull
    val hairColor: Color? = null,

    @field:Valid
    val location: LocationImportRequest? = null,

    @field:NotNull
    @field:Positive
    val height: Int? = null,

    val nationality: Country? = null,
)
