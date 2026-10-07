package org.example.dto.person.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.example.entity.enums.Color
import org.example.entity.enums.Country

data class PersonUpsertRequest(
    @field:NotBlank
    val name: String? = null,

    val eyeColor: Color? = null,

    @field:NotNull
    val hairColor: Color? = null,

    @field:Positive
    val locationId: Int? = null,

    @field:NotNull
    @field:Positive(message = "рост больше 0, пожалуйста")
    val height: Int? = null,

    val nationality: Country? = null,
)

