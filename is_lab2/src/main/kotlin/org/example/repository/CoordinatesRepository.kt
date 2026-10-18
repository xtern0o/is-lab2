package org.example.repository

import org.example.entity.Coordinates
import org.springframework.data.jpa.repository.JpaRepository

interface CoordinatesRepository : JpaRepository<Coordinates, Int> {

    /**
     * business-layer unique constraint
     */
    fun existsByXAndY(x: Float, y: Long): Boolean
    fun existsByXAndYAndIdNot(x: Float, y: Long, id: Int): Boolean
}
