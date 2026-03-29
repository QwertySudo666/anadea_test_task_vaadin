package com.anadea.testtaskvaadin.services

import com.anadea.testtaskvaadin.dtos.CreateUserRequest
import com.anadea.testtaskvaadin.dtos.UpdateUserRequest
import com.anadea.testtaskvaadin.dtos.UserDto
import com.anadea.testtaskvaadin.entities.UserEntity
import com.anadea.testtaskvaadin.repositories.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional(readOnly = true)
    fun search(
        name: String?,
        email: String?,
        pageable: Pageable,
    ): Page<UserDto> =
        userRepository
            .search(name, email, pageable)
            .map { UserDto(it) }

    @Transactional
    fun create(request: CreateUserRequest): UserDto {
        val user =
            UserEntity(
                name = request.name,
                email = request.email,
                password = passwordEncoder.encode(request.password)!!,
                role = UserEntity.Role.valueOf(request.role),
            )
        return UserDto(userRepository.save(user))
    }

    @Transactional
    fun update(
        id: UUID,
        request: UpdateUserRequest,
    ): UserDto {
        val user =
            userRepository
                .findById(id)
                .orElseThrow { IllegalArgumentException("User not found: $id") }

        request.name?.let { user.name = it }
        request.email?.let { user.email = it }
        request.password?.let { user.password = passwordEncoder.encode(it)!! }
        request.role?.let { user.role = UserEntity.Role.valueOf(it) }

        return UserDto(userRepository.save(user))
    }

    @Transactional
    fun delete(id: UUID) {
        userRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): UserDto? =
        userRepository
            .findById(id)
            .map { UserDto(it) }
            .orElse(null)
}
