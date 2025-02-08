package com.eliasmeireles.kotlintesting.it.controller

import com.eliasmeireles.kotlintesting.it.BaseIT
import com.eliasmeireles.kotlintesting.rest.model.ResponseRest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class HealthCheckControllerIT : BaseIT() {

    @Test
    fun `must to return expected response by health check endpoint`() {
        Given {
            spec
        } When {
            get("/health")
        } Then {
            log()
                .all(true)
                .statusCode(HttpStatus.OK.value())
        } Extract {
            val response = body().`as`(ResponseRest::class.java)
            assertThat(response).isNotNull()
            assertThat("Application is running").isEqualTo(response.message)

            assertThat(response.errorInfo).isNull()
            assertThat(response.timestamp).isNotNull()
            assertTrue(response.timestamp > 1739032122648)
            assertTrue(response.success)
        }
    }
}
