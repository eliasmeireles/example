import com.github.softwareplace.springboot.kotlin.kotlinReactive
import com.github.softwareplace.springboot.kotlin.openapi.kotlinOpenApiSettings
import com.github.softwareplace.springboot.kotlin.testKotlinMockito
import com.github.softwareplace.springboot.utils.springBootSecurityUtil
import com.github.softwareplace.springboot.utils.springBootStartWeb
import com.github.softwareplace.springboot.utils.springJettyApi
import com.github.softwareplace.springboot.utils.testContainersPostgresql

plugins {
    id("com.github.softwareplace.springboot.kotlin")
}

group = "com.eliasmeireles.kotlintesting"
version = "0.0.1-SNAPSHOT"

kotlinOpenApiSettings {
    reactive = true
}

repositories {
    mavenCentral()
}

dependencies {
    springBootStartWeb()
    springBootSecurityUtil()
    springJettyApi()
    kotlinReactive()
    implementation("org.springframework.kafka:spring-kafka")
    runtimeOnly("org.postgresql:postgresql")

    testContainersPostgresql()
    testKotlinMockito()
    testImplementation("io.rest-assured:rest-assured:5.3.2")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

