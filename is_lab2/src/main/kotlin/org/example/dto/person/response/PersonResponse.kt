package org.example.dto.person.response

import org.example.entity.enums.Color
import org.example.entity.enums.Country

data class PersonResponse(
    val id: Int,
    val name: String,
    val eyeColor: Color?,
    val hairColor: Color,
    val locationId: Int?,
    val height: Int,
    val nationality: Country?,
)