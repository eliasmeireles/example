package com.softwareplace.fileserver.controller

import com.softwareplace.fileserver.rest.controller.HealthCheckController
import com.softwareplace.fileserver.rest.model.ResponseRest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckControllerImpl : HealthCheckController {
    override suspend fun healthGet(): ResponseEntity<ResponseRest> {
        return "Application is running".ok(true)
    }

    fun String.ok(success: Boolean): ResponseEntity<ResponseRest> {
        return ResponseRest(message = this, success = success, timestamp = System.currentTimeMillis())
            .ok()
    }

    fun ResponseRest.ok(): ResponseEntity<ResponseRest> = ResponseEntity.ok(this)
}
