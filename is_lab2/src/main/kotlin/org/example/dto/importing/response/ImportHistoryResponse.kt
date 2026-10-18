package org.example.dto.importing.response

import org.example.entity.enums.ImportStatus
import java.time.Instant
import java.util.UUID

/**
 * Считает верхнеуровневые сущности - это подсчет без учета
 * вложенных сущностей.
 *
 * Можно сделать и подсчет всех сущностей
 * но мне так сильно проще
 */
data class ImportHistoryResponse(
    val id: UUID,
    val createdAt: Instant,
    val status: ImportStatus,
    val userId: UUID,
    val username: String,
    val moviesCount: Int?,
    val personsCount: Int?,
    val coordinatesCount: Int?,
    val locationsCount: Int?,
)