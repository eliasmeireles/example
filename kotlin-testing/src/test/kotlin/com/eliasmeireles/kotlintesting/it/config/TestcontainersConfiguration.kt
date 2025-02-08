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
import org.testcontainers.utility.DockerImageName
import java.util.UUID.randomUUID


@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    val logger: Logger = LoggerFactory.getLogger(TestcontainersConfiguration::class.java)

    val testInstanceId = randomUUID().toString()
    var network: Network = Network.newNetwork()


    @PostConstruct
    fun kafkaContainer() {
        val confluentIncCpZookeeperImage = DockerImageName
            .parse("confluentinc/cp-zookeeper:7.7.1")

        val zookeeperContainerName = "$testInstanceId-kafka-zookeeper"
        val kafkaContainerName = "$testInstanceId-kafka"

        val zookeeper = GenericContainer(confluentIncCpZookeeperImage)
            .withImagePullPolicy(PullPolicy.defaultPolicy())
            .withCreateContainerCmdModifier { it.withName(zookeeperContainerName) }
            .withNetwork(network)
            .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
            .withEnv("ZOOKEEPER_TICK_TIME", "2000")

        val zookeeperHost = "$zookeeperContainerName:%2181"

        val confluentIncCpKafkaImage = DockerImageName.parse("confluentinc/cp-kafka:7.7.1")

        val kafkaContainer = GenericContainer(confluentIncCpKafkaImage)

        val kafkaContainerPort = getAContainerPort()
        val kafkaContainerPortS = getAContainerPort()

        val listeners = "INSIDE://$kafkaContainerName:9092,OUTSIDE://localhost:$kafkaContainerPort"
        kafkaContainer.withImagePullPolicy(PullPolicy.defaultPolicy())
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
    fun mongoDbContainer() {
        val dbPass = randomUUID().toString()
        val containerEnvs = listOf(
            "MONGO_INITDB_DATABASE=test",
            "MONGO_INITDB_ROOT_USERNAME=root",
            "MONGO_INITDB_ROOT_PASSWORD=$dbPass"
        )

        val dbContainer = MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withNetwork(network)
//            .withLogConsumer { outputFrame -> logger.info(outputFrame.utf8String) }
            .withCreateContainerCmdModifier {
                it.withName("$testInstanceId-mongo")
//                    .withEnv(containerEnvs)
            }

        dbContainer.start()

        System.setProperty("MONGODB_PORT", dbContainer.firstMappedPort.toString())
        System.setProperty("MONGODB_HOST", dbContainer.host)
        System.setProperty("MONGODB_USERNAME", "root")
        System.setProperty("MONGODB_PASSWORD", dbPass)
    }


    @PostConstruct
    fun postgresContainer() {
        PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
            .withNetwork(network)
            .withCreateContainerCmdModifier { it.withName("$testInstanceId-postgres") }
            .start()
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
