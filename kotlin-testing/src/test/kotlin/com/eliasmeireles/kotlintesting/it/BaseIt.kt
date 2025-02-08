package com.eliasmeireles.kotlintesting.it

import com.eliasmeireles.kotlintesting.it.config.TestcontainersConfiguration
import io.restassured.RestAssured
import io.restassured.config.LogConfig
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
class BaseIt {

    val log = LoggerFactory.getLogger(this::class.java)

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var environment: Environment

    protected lateinit var spec: RequestSpecification

    @BeforeEach
    fun setUp() {
        val contextPath = environment.getProperty("server.servlet.context-path") ?: ""

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = contextPath

        val logConfig = LogConfig.logConfig()
            .enablePrettyPrinting(true)

        val config = RestAssured.config()
            .logConfig(logConfig)

        spec = RestAssured.given()
            .log()
            .all(true)
            .config(config)

    }
}
