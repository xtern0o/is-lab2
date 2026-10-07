package org.example.dto.movie.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.example.entity.enums.MovieGenre
import org.example.entity.enums.MpaaRating

data class MovieUpsertRequest(
    @field:NotBlank
    val name: String? = null,

    @field:NotNull
    @field:Positive
    val coordinatesId: Int? = null,

    @field:NotNull
    @field:PositiveOrZero
    val oscarsCount: Int? = null,

    @field:NotNull
    @field:Positive
    val budget: Int? = null,

    @field:NotNull
    @field:Positive
    val totalBoxOffice: Int? = null,

    val mpaaRating: MpaaRating? = null,

    @field:NotNull
    @field:Positive
    val directorId: Int? = null,

    @field:Positive
    val screenwriterId: Int? = null,

    @field:Positive
    val operatorId: Int? = null,

    @field:Positive
    val length: Long? = null,

    @field:NotNull
    @field:Positive
    val goldenPalmCount: Long? = null,

    val genre: MovieGenre? = null,
)

