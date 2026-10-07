package org.example.dto.movie.response

import org.example.entity.enums.MovieGenre
import org.example.entity.enums.MpaaRating
import java.util.Date

data class MovieResponse(
    val id: Int,
    val name: String,
    val coordinatesId: Int,
    val creationDate: Date,
    val oscarsCount: Int,
    val budget: Int,
    val totalBoxOffice: Int,
    val mpaaRating: MpaaRating?,
    val directorId: Int,
    val screenwriterId: Int?,
    val operatorId: Int?,
    val length: Long?,
    val goldenPalmCount: Long,
    val genre: MovieGenre?,
)

