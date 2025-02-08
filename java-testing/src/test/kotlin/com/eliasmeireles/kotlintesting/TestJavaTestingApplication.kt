package com.eliasmeireles.javatesting

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<JavaTestingApplication>().with(TestcontainersConfiguration::class).run(*args)
}
