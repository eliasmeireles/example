import com.github.softwareplace.springboot.kotlin.kotlinReactive
import com.github.softwareplace.springboot.kotlin.openapi.kotlinOpenApiSettings
import com.github.softwareplace.springboot.kotlin.testKotlinMockito
import com.github.softwareplace.springboot.utils.*

plugins {
    id("com.github.softwareplace.springboot.kotlin")
}

group = "com.softwareplace.fileserver"
version = "1.0.0"

kotlinOpenApiSettings()

dependencies {
    logstashLogbackEncoderVersion()
    springBootSecurityUtil()
    springBootStartWeb()
    springBootStarter()
    kotlinReactive()
    springJettyApi()
    jsonLogger()

    testKotlinMockito()
}

tasks.bootBuildImage {
    System.getProperties().apply {
        setProperty("BP_NATIVE_IMAGE", "true")
        setProperty("BP_NATIVE_IMAGE_EXECUTABLE", "runner")
    }
}


