package com.anadea.testtaskvaadin.repositories

import com.anadea.testtaskvaadin.entities.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?

    @Query(
        """
            SELECT u FROM UserEntity u 
            WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
            AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:email AS string), '%')))
        """,
    )
    fun search(
        @Param("name") name: String?,
        @Param("email") email: String?,
        pageable: Pageable,
    ): Page<UserEntity>
}
