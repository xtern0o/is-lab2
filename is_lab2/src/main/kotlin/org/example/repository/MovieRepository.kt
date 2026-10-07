package org.example.repository

import org.example.entity.Movie
import org.example.repository.projection.OscarsCountGroup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : JpaRepository<Movie, Int> {
    fun findByNameContainingIgnoreCase(
        name: String,
        pageable: Pageable
    ): Page<Movie>

    fun findByGoldenPalmCountLessThan(
        goldenPalmCount: Long,
        pageable: Pageable,
    ): Page<Movie>

    fun findByOscarsCount(
        oscarsCount: Int = 0,
        pageable: Pageable,
    ): Page<Movie>

    fun existsByDirectorId(id: Int): Boolean
    fun existsByScreenwriterId(id: Int): Boolean
    fun existsByOperatorId(id: Int): Boolean

    fun existsByCoordinatesId(id: Int): Boolean

    @EntityGraph(
        attributePaths = [
            "coordinates",
            "director",
            "screenwriter",
            "operator"
        ]
    )
    @Query("select m from Movie m where m.id = :id")
    fun findDetailedById(
        @Param("id") id: Int
    ): Movie?

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        update Movie m 
        set m.oscarsCount = m.oscarsCount + :anountOfOscars
        where m.length > :length
    """)
    fun addOscarsToMoviesLongerThan(
        @Param("length") length: Long,
        @Param("anountOfOscars") amountOfOscars: Int
    ): Int

    @Query("""
        select m.oscarsCount as oscarsCount, count(m) as movieCount
        from Movie m
        group by m.oscarsCount
        order by m.oscarsCount
    """)
    fun countGroupedByOscars(): List<OscarsCountGroup>
}