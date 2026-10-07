package org.example.repository

import org.example.entity.Person
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, Int> {
    fun findByNameContainingIgnoreCase(
        name: String,
        pageable: Pageable,
    ): Page<Person>

    @EntityGraph(attributePaths = ["location"])
    fun findOneById(id: Int): Person?

    fun existsByLocationId(locationId: Int): Boolean
}