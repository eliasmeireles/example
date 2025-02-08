package com.eliasmeireles.kotlintesting.it.config

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.images.PullPolicy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import java.util.UUID.randomUUID


@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    private val logger: Logger = LoggerFactory.getLogger(TestcontainersConfiguration::class.java)

    private val testInstanceId = randomUUID().toString()
    private val network: Network = Network.newNetwork()

    @Container
    private lateinit var dbContainer: MongoDBContainer

    @Container
    private lateinit var zookeeper: GenericContainer<*>

    @Container
    private lateinit var kafkaContainer: GenericContainer<*>

    @Container
    private lateinit var postgresContainer: PostgreSQLContainer<*>

    @PostConstruct
    private fun kafkaContainer() {
        val confluentIncCpZookeeperImage = DockerImageName
            .parse("confluentinc/cp-zookeeper:7.7.1")

        val zookeeperContainerName = "$testInstanceId-kafka-zookeeper"

        zookeeper = GenericContainer(confluentIncCpZookeeperImage)
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

        kafkaContainer = GenericContainer(confluentIncCpKafkaImage)
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

        System.setProperty("KAFKA_SERVER_ADDRESS", kafkaContainer.host)

        logger.info("Kafka server started on : http://${kafkaContainer.host}:${kafkaContainerPortS}")
    }

    @PostConstruct
    private fun mongoDbContainer() {
        val dbPass = "test-password"
        dbContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "root")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", dbPass)
            .withExposedPorts(27017)
            .withCreateContainerCmdModifier {
                it.withName("$testInstanceId-mongo")
            }

        dbContainer.start()

        System.setProperty("MONGODB_HOST", dbContainer.connectionString)
        System.setProperty("MONGODB_USERNAME", "root")
        System.setProperty("MONGODB_PASSWORD", dbPass)
    }


    @PostConstruct
    private fun postgresContainer() {
        postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
            .withNetwork(network)
            .withCreateContainerCmdModifier { it.withName("$testInstanceId-postgres") }

        postgresContainer.start()

        System.setProperty("POSTGRES_HOST", postgresContainer.jdbcUrl)
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
