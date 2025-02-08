package com.eliasmeireles.kotlintesting.it.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.images.PullPolicy
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.UUID.randomUUID


@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration : ApplicationContextInitializer<ConfigurableApplicationContext> {

    private val logger: Logger = LoggerFactory.getLogger(TestcontainersConfiguration::class.java)

    private val mongodbRootPassword = "UJXSeEGJwDhuP1RA9ixz0imj"
    private val mongoDbUsername = "root"
    private val mongoDbPort = 27017


    private val testInstanceId = randomUUID().toString()
    private val network: Network = Network.newNetwork()

    private val postgresContainer by lazy { postgresContainer() }
    private val mongoDBContainer by lazy { mongoDbContainer() }
    private val kafkaContainer by lazy { kafkaContainer() }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {

        val environmentVariables = mapOf(
            "KAFKA_SERVER_ADDRESS" to kafkaContainer.host,
            "POSTGRES_DB_NAME" to postgresContainer.databaseName,
            "POSTGRES_HOST" to postgresContainer.jdbcUrl,
            "POSTGRES_USERNAME" to postgresContainer.username,
            "POSTGRES_PASSWORD" to postgresContainer.password,
            "MONGODB_HOST" to "mongodb://${mongoDBContainer.host}:${mongoDBContainer.getMappedPort(mongoDbPort)}/",
            "MONGODB_USERNAME" to "root",
            "MONGODB_PASSWORD" to mongodbRootPassword,
        )

        val environmentPropertySource = MapPropertySource(
            "testcontainersEnvironment", environmentVariables
        )

        applicationContext.environment.propertySources.addFirst(environmentPropertySource)
    }

    private fun kafkaContainer(): GenericContainer<*> {
        val confluentIncCpZookeeperImage = DockerImageName
            .parse("confluentinc/cp-zookeeper:7.7.1")

        val zookeeperContainerName = "$testInstanceId-kafka-zookeeper"

        val zookeeper = GenericContainer(confluentIncCpZookeeperImage)
            .withImagePullPolicy(PullPolicy.defaultPolicy())
            .withCreateContainerCmdModifier { it.withName(zookeeperContainerName) }
            .withNetwork(network)
            .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
            .withEnv("ZOOKEEPER_TICK_TIME", "2000")

        val zookeeperHost = "$zookeeperContainerName:%2181"

        val confluentIncCpKafkaImage = DockerImageName.parse("confluentinc/cp-kafka:7.7.1")


        val kafkaContainerPort = getAContainerPort()
        val kafkaContainerPortS = getAContainerPort()

        val kafkaContainerName = "$testInstanceId-kafka"
        val listeners = "INSIDE://$kafkaContainerName:9092,OUTSIDE://localhost:$kafkaContainerPort"

        val kafkaContainer = GenericContainer(confluentIncCpKafkaImage)
            .withImagePullPolicy(PullPolicy.defaultPolicy())
            .withCreateContainerCmdModifier { it.withName(kafkaContainerName) }
            .withNetwork(network)
            .withEnv("KAFKA_ZOOKEEPER_CONNECT", zookeeperHost)
            .withEnv("KAFKA_BROKER_ID", "1")
            .withEnv("KAFKA_ADVERTISED_LISTENERS", listeners)
            .withEnv("KAFKA_LISTENERS", "INSIDE://0.0.0.0:9092,OUTSIDE://0.0.0.0:29092")
            .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT")
            .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "INSIDE")
            .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .dependsOn(zookeeper)

        kafkaContainer.portBindings = listOf("$kafkaContainerPort:29092", "$kafkaContainerPortS:9092")
        kafkaContainer.start()

        logger.info("Kafka server started on : http://${kafkaContainer.host}:${kafkaContainerPortS}")
        return kafkaContainer
    }

    private fun mongoDbContainer(): GenericContainer<*> {
        val mongoDBContainer = GenericContainer(DockerImageName.parse("mongo:4.0.10"))
            .withExposedPorts(mongoDbPort)
            .withCreateContainerCmdModifier { it.withName("$testInstanceId-mongo") }
            .withEnv("MONGO_INITDB_ROOT_USERNAME", mongoDbUsername)
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", mongodbRootPassword)
            .withEnv("MONGO_INITDB_DATABASE", "kotlin-testing")
            .withStartupTimeout(Duration.ofMinutes(1))

        mongoDBContainer.start()

        logger.info(
            "MongoDB server started on : mongodb://${mongoDBContainer.host}:${
                mongoDBContainer.getMappedPort(mongoDbPort)
            }/"
        )
        return mongoDBContainer
    }


    private fun postgresContainer(): PostgreSQLContainer<*> {
        val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
            .withNetwork(network)
            .withDatabaseName("kotlin-testing")
            .withCreateContainerCmdModifier { it.withName("$testInstanceId-postgres") }

        postgresContainer.start()
        return postgresContainer
    }

    private fun getAContainerPort(): Int {
        val container = GenericContainer(DockerImageName.parse("nginx:latest"))
            .withExposedPorts(80)

        container.start()

        val port: Int = container.getMappedPort(80)
        container.stop()

        return port
    }

}
