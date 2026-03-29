package com.anadea.testtaskvaadin.config

import com.anadea.testtaskvaadin.repositories.UserRepository
import com.anadea.testtaskvaadin.ui.views.LoginView
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(): UserDetailsService =
        UserDetailsService { email ->
            log.info("Attempting to authenticate user: $email")
            val user = userRepository.findByEmail(email)
            if (user == null) {
                log.warn("User not found: $email")
                throw UsernameNotFoundException("User not found: $email")
            }
            log.info("User found: $email, role: ${user.role}")
            org.springframework.security.core.userdetails.User
                .withUsername(user.email)
                .password(user.password)
                .roles(user.role.name)
                .build()
        }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.with(
            VaadinSecurityConfigurer.vaadin(),
        ) { configurer ->
            configurer.loginView(LoginView::class.java)
        }

//        http.formLogin {
//            it.defaultSuccessUrl("/dashboard", false)
//        }
//
//        http.logout {
//            it.logoutSuccessUrl("/login")
//        }

        return http.build()
    }
}
