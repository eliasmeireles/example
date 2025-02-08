package com.eliasmeireles.kotlintesting.it

import com.eliasmeireles.kotlintesting.domain.repository.UserRepository
import com.eliasmeireles.kotlintesting.it.config.TestcontainersConfiguration
import io.restassured.RestAssured
import io.restassured.config.DecoderConfig
import io.restassured.config.LogConfig
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ContextConfiguration(
    initializers = [TestcontainersConfiguration::class]
)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class BaseIT {

    val log = LoggerFactory.getLogger(this::class.java)

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    protected lateinit var environment: Environment

    @Autowired
    protected lateinit var userRepository: UserRepository

    protected lateinit var spec: RequestSpecification

    @BeforeEach
    fun setUp() {
        val contextPath = environment.getProperty("server.servlet.context-path") ?: ""

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = contextPath

        val logConfig = LogConfig.logConfig()
            .enablePrettyPrinting(true)

        val decoderConfig = DecoderConfig.decoderConfig()
            .noContentDecoders()

        val config = RestAssured.config()
            .logConfig(logConfig)
            .decoderConfig(decoderConfig)

        spec = RestAssured.given()
            .log()
            .all(true)
            .config(config)

    }
}
