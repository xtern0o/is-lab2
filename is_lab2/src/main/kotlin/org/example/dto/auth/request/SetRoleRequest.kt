package org.example.dto.auth.request

import jakarta.validation.constraints.NotNull
import org.example.entity.enums.Role

data class SetRoleRequest(
    @field:NotNull(message = "роль обязательна")
    val role: Role? = null,
)