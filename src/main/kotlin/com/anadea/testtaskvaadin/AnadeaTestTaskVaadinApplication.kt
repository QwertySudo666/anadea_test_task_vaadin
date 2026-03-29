package com.anadea.testtaskvaadin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class AnadeaTestTaskVaadinApplication

fun main(args: Array<String>) {
    runApplication<AnadeaTestTaskVaadinApplication>(*args)
}
