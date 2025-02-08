import com.github.softwareplace.springboot.kotlin.kotlinReactive
import com.github.softwareplace.springboot.kotlin.openapi.kotlinOpenApiSettings
import com.github.softwareplace.springboot.kotlin.testKotlinMockito
import com.github.softwareplace.springboot.utils.*

plugins {
    id("com.github.softwareplace.springboot.kotlin")
}

group = "com.eliasmeireles.kotlintesting"
version = "0.0.1-SNAPSHOT"

kotlinOpenApiSettings {
    reactive = true
}

dependencies {
    implementation("org.springframework.kafka:spring-kafka")
    springBootSecurityUtil()
    springBootDataCommons()
    springBootStartWeb()
    springBootStarter()
//    TODO(fix flyway migration bug for Unsupported Database: PostgreSQL <version>)
//    flayWayMigration()
    springJettyApi()
    kotlinReactive()
    jsonLogger()
    postgresql()

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.rest-assured:kotlin-extensions:5.4.0")
    testImplementation("io.rest-assured:rest-assured:5.3.2")
    testImplementation("org.testcontainers:mongodb")
    testContainersPostgresql()
    testKotlinMockito()
}

