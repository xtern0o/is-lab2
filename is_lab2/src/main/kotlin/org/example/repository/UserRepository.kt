package org.example.repository

import org.example.dto.auth.response.UserResponse
import org.example.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByName(name: String): User?
}