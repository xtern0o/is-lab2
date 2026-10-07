package org.example.repository

import org.example.entity.Location
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LocationRepository : JpaRepository<Location, Int> {
    fun findByNameContainingIgnoreCase(
        name: String,
        pageable: Pageable,
    ): Page<Location>
}