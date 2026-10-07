package org.example.dto.auth.response

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
)