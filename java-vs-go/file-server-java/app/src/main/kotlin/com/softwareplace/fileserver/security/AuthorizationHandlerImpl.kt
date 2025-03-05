package com.softwareplace.fileserver.security

import com.github.softwareplace.springboot.security.authorization.AuthorizationHandler
import com.github.softwareplace.springboot.security.model.UserData
import com.softwareplace.jsonlogger.log.kLogger

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service

@Service
class AuthorizationHandlerImpl : AuthorizationHandler {
    override fun authorizationSuccessfully(request: HttpServletRequest, userData: UserData) {
        kLogger.info("Authorization successfully")
    }
}
