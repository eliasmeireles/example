package com.eliasmeireles.kotlintesting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JavaTestingApplication

fun main(args: Array<String>) {
    runApplication<JavaTestingApplication>(*args)
}
