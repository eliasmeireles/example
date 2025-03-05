package com.softwareplace.fileserver.controller

import com.softwareplace.fileserver.rest.model.ResponseRest
import org.springframework.http.ResponseEntity


fun String.ok(success: Boolean): ResponseEntity<ResponseRest> {
    return ResponseRest(message = this, success = success, timestamp = System.currentTimeMillis())
        .ok()
}

fun ResponseRest.ok(): ResponseEntity<ResponseRest> = ResponseEntity.ok(this)
