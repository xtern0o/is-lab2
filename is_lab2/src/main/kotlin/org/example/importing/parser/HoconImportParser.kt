package org.example.importing.parser

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import jakarta.validation.Validator
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper


/**
 * Парсер HOCON текста в объектное представление
 */
@Component
class HoconImportParser(
    private val objectMapper: ObjectMapper,
    private val validator: Validator,
) {
    fun <T: Any> parse(
        content: String,
        targetClass: Class<T>
    ): T {
        val config = try {
            ConfigFactory.parseString(content).resolve()
        } catch (e: ConfigException) {
            throw IllegalArgumentException(
                "invalid hocon format: ${e.message}",
            )
        }

        val request = try {
            objectMapper.convertValue(
                config.root().unwrapped(),
                targetClass,
            )
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(
                "file cannot be imported as a hocon: ${e.message}",
            )
        }

        val violations = validator.validate(request)
        if (violations.isNotEmpty()) {
            throw IllegalArgumentException(
                violations.joinToString("; ") {
                    "${it.propertyPath}: ${it.message}"
                }
            )
        }

        return request
    }
}