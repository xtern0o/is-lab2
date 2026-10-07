package org.example.dto.movie.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class AwardOscarsByLengthRequest(
    @field:NotNull
    @field:Positive(message = "не бывает фильмов длиной <= 0 секунд")
    val minimumLength: Long? = null,

    @field:NotNull
    @field:Positive(message = "какой смысл добавлять 0?")
    val additionalOscars: Int? = null
)
