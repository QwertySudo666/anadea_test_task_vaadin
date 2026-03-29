package com.anadea.testtaskvaadin.dtos

import com.anadea.testtaskvaadin.entities.UserEntity
import java.time.Instant
import java.util.UUID

data class UserDto(
    val id: UUID? = null,
    val name: String,
    val email: String,
    val role: String,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
) {
    constructor(user: UserEntity) : this(
        id = user.id,
        name = user.name,
        email = user.email,
        role = user.role.name,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt,
    )
}

data class CreateUserRequest(
    var name: String,
    var email: String,
    var password: String,
    var role: String,
)

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: String? = null,
)
