package org.example.controller

import org.example.dto.common.request.EntityImportFileRequest
import org.example.importing.parser.HoconImportParser
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/imports")
class ImportFileController(
    private val hoconImportParser: HoconImportParser
) {

    @PostMapping(
        "/echo",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun importMoviesEchoTest(
        @RequestPart("file") file: MultipartFile,
    ): EntityImportFileRequest {
        if (file.isEmpty) {
            throw IllegalArgumentException("file can not be empty")
        }

        val content = file.inputStream
            .bufferedReader(StandardCharsets.UTF_8)
            .use { it.readText() }

        return hoconImportParser.parse(
            content,
            EntityImportFileRequest::class.java,
        )
    }
}