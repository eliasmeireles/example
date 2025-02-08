package com.eliasmeireles.kotlintesting.it.config

import com.eliasmeireles.kotlintesting.JavaTestingApplication
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<JavaTestingApplication>().with(TestcontainersConfiguration::class).run(*args)
}
