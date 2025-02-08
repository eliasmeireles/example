package com.eliasmeireles.kotlintesting

import com.github.softwareplace.springboot.security.SecurityModule
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@ImportAutoConfiguration(classes = [SecurityModule::class])
class JavaTestingApplication

fun main(args: Array<String>) {
    runApplication<JavaTestingApplication>(*args)
}
