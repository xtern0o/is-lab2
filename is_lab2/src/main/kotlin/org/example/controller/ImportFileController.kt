package org.example.controller

import org.example.dto.importing.response.ImportHistoryResponse
import org.example.service.importing.ImportFileService
import org.example.service.importing.ImportHistoryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets
import java.util.UUID

@RestController
@RequestMapping("/api/imports")
class ImportFileController(
    private val importFileService: ImportFileService,
    private val historyService: ImportHistoryService
) {

    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun importMoviesEchoTest(
        @RequestPart("file") file: MultipartFile,
        @AuthenticationPrincipal jwt: Jwt,
    ): ImportHistoryResponse {
        if (file.isEmpty) {
            throw IllegalArgumentException("file can not be empty")
        }

        val content = file.inputStream
            .bufferedReader(StandardCharsets.UTF_8)
            .use { it.readText() }

        val userId = UUID.fromString(jwt.subject)
        return importFileService.importFile(content, userId)
    }

    @GetMapping("/history")
    fun getMyHistory(
        @AuthenticationPrincipal jwt: Jwt,
        @PageableDefault(
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
    ): Page<ImportHistoryResponse> =
        historyService.getPageByUserId(
            UUID.fromString(jwt.subject),
            pageable,
        )

    // были мысли вынести в /admin/imports эндпоинт но много кода лишнего мне лень
    @GetMapping("/history/all")
    fun getAllHistory(
        @PageableDefault(
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
    ): Page<ImportHistoryResponse> =
        historyService.getPage(pageable)
}