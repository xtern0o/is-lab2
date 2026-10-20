package org.example.controller

import jakarta.validation.Valid
import org.example.dto.auth.request.SetRoleRequest
import org.example.dto.auth.response.UserResponse
import org.example.service.auth.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {

    @PatchMapping("/{id}/role")
    fun setRole(
        @PathVariable id: UUID,
        @Valid @RequestBody request: SetRoleRequest,
    ): UserResponse =
        userService.setRole(id, request)

    @GetMapping
    fun getPage(
        @PageableDefault(size = 20, sort = ["name"])
        pageable: Pageable,
    ): Page<UserResponse> =
        userService.getPage(pageable)
}