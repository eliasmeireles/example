package com.softwareplace.fileserver.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.storage")
class AppProperties {

    lateinit var storagePath: String
    lateinit var authorizationPath: String
}
