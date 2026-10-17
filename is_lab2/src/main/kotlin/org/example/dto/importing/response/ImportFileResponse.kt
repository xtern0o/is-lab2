package org.example.dto.common.response

/**
 * Считает верхнеуровневые сущности - это подсчет без учета
 * вложенных сущностей.
 *
 * Можно сделать и подсчет всех сущностей
 * но мне так сильно проще
 */
data class ImportFileResponse(
    val moviesCount: Int = 0,
    val personsCount: Int = 0,
    val coordinatesCount: Int = 0,
    val locationsCount: Int = 0,
)