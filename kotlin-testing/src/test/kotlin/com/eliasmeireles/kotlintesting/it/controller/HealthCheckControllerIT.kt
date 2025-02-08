package com.eliasmeireles.kotlintesting.it.controller

import com.eliasmeireles.kotlintesting.it.BaseIt
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test


class HealthCheckControllerIT : BaseIt() {

    @Test
    fun `must to return expected response by health check endpoint`() {
        spec.`when`()
            .get("/health")
            .then()
            .statusCode(200)
            .body("status", `is`("UP"))
    }
}
