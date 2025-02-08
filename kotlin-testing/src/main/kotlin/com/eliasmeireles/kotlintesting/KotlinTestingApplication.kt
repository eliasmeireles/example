package com.eliasmeireles.kotlintesting

import com.github.softwareplace.springboot.security.SecurityModule
import com.github.softwareplace.springboot.starter.StarterModule
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ImportAutoConfiguration(
    classes = [
        SecurityModule::class,
        StarterModule::class,
    ]
)
class KotlinTestingApplication

fun main(args: Array<String>) {
    runApplication<KotlinTestingApplication>(*args)
}
