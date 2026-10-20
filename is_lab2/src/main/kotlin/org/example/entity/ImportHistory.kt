package org.example.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.example.dto.importing.response.ImportHistoryResponse
import org.example.entity.enums.ImportStatus
import java.time.Instant
import java.util.UUID

@Entity
class ImportHistory(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    @field:Column(nullable = false, updatable = false)
    var id: UUID? = null,

    @field:Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @field:ManyToOne(fetch = FetchType.EAGER, optional = false)
    @field:JoinColumn(
        name = "user_id",
        nullable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_import_history_user"),
    )
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ImportStatus = ImportStatus.STARTED,

    @Column
    var locationsCount: Int? = null,

    @Column
    var moviesCount: Int? = null,

    @Column
    var coordinatesCount: Int? = null,

    @Column
    var personsCount: Int? = null,

    )

fun ImportHistory.toResponse() =
    ImportHistoryResponse(
        id = requireNotNull(this.id),
        moviesCount = moviesCount,
        personsCount = personsCount,
        coordinatesCount = coordinatesCount,
        locationsCount = locationsCount,
        userId = requireNotNull(user.id),
        username = requireNotNull(user.name),
        createdAt = requireNotNull(this.createdAt),
        status = status,
    )
