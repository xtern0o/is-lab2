package org.example.service.importing

import org.example.dto.importing.request.EntityImportFileRequest
import org.example.dto.importing.response.ImportHistoryResponse
import org.example.importing.parser.HoconImportParser
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Сервис-орекстратор для импорта от имени пользователя
 */
@Service
class ImportFileService(
    private val parser: HoconImportParser,
    private val historyService: ImportHistoryService,
    private val transactionService: ImportTransactionService,
) {
    fun importFile(
        content: String,
        userId: UUID,
    ): ImportHistoryResponse {
        val historyId = historyService.start(userId)

        return try {
            val request = parser.parse(
                content,
                EntityImportFileRequest::class.java,
            )
            transactionService.importEntities(request, historyId)

        } catch (exception: Exception) {
            historyService.markFailed(historyId)
            throw exception
        }
    }
}