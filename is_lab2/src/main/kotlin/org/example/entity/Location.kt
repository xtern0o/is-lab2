package org.example.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.example.dto.location.response.LocationResponse

@Entity
@Table(name = "locations")
class Location(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(nullable = false, updatable = false)
    var id: Int? = null,

    @field:Column(nullable = false)
    var x: Double = 0.0,

    @field:Column(nullable = false)
    var y: Int = 0,

    @field:NotNull
    @field:Column(nullable = false)
    var z: Double? = null,

    @field:Size(max = 389)
    @field:Column(length = 389)
    var name: String? = null,
)

fun Location.toResponse() = LocationResponse(
    id = requireNotNull(id),
    x = x,
    y = y,
    z = requireNotNull(z),
    name = name,
)
