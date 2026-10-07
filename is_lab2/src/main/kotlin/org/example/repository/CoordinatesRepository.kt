package org.example.repository

import org.example.entity.Coordinates
import org.springframework.data.jpa.repository.JpaRepository

interface CoordinatesRepository : JpaRepository<Coordinates, Int> {
}
