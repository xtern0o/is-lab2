package org.example.repository

import org.example.entity.ImportHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ImportHistoryRepository : JpaRepository<ImportHistory, UUID> {
    fun findByUserId(
        userId: UUID,
        pageable: Pageable
    ): Page<ImportHistory>
}