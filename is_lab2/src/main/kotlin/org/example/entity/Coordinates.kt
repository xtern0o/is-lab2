package org.example.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.example.dto.coordinates.response.CoordinatesResponse

@Entity
@Table(name = "coordinates")
class Coordinates(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(nullable = false, updatable = false)
    var id: Int? = null,

    @field:Column(nullable = false)
    @field:Max(455)
    var x: Float,

    @field:Column(nullable = false)
    @field:Min(-123)
    var y: Long
)

fun Coordinates.toResponse() = CoordinatesResponse(
    id = requireNotNull(id),
    x = x,
    y = y
)