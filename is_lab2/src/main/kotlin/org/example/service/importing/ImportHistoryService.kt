package org.example.service.importing

import org.example.dto.importing.response.ImportHistoryResponse
import org.example.entity.ImportHistory
import org.example.entity.enums.ImportStatus
import org.example.entity.toResponse
import org.example.repository.ImportHistoryRepository
import org.example.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Сервис для обработки текущего статуса импорта из файла
 */
@Service
class ImportHistoryService(
    private val historyRepository: ImportHistoryRepository,
    private val userRepository: UserRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun start(userId: UUID): UUID {
        val user = userRepository.findById(userId).orElseThrow {
            NoSuchElementException("user $userId not found")
        }

        return requireNotNull(
            historyRepository.save(
                ImportHistory(user = user),
            ).id,
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun markFailed(historyId: UUID) {
        val history = historyRepository.findById(historyId).orElseThrow()
        history.status = ImportStatus.FAILED
    }

    @PreAuthorize("#userId.toString() == authentication.name or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    fun getPageByUserId(
        userId: UUID,
        pageable: Pageable
    ): Page<ImportHistoryResponse> {
        val history = historyRepository.findByUserId(userId, pageable)

        return history.map { it.toResponse() }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    fun getPage(
        pageable: Pageable
    ): Page<ImportHistoryResponse> {
        val history = historyRepository.findAll(pageable)

        return history.map { it.toResponse() }
    }
}