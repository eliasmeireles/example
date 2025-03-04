package com.softwareplace.fileserver

import com.github.softwareplace.springboot.security.SecurityModule
import com.github.softwareplace.springboot.starter.StarterModule
import com.softwareplace.fileserver.properties.AppProperties
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(value = [AppProperties::class])
@ImportAutoConfiguration(classes = [SecurityModule::class, StarterModule::class])
class FileServerApplication

fun main(args: Array<String>) {
    runApplication<FileServerApplication>(*args)
}
