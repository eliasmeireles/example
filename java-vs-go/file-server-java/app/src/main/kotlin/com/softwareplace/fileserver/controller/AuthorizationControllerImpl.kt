package com.softwareplace.fileserver.controller

import com.softwareplace.fileserver.rest.controller.AuthorizationController
import com.softwareplace.fileserver.rest.model.AuthorizationRest
import com.softwareplace.fileserver.rest.model.UserContentRest
import com.softwareplace.fileserver.rest.model.UserInfoRest
import com.softwareplace.fileserver.security.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorizationControllerImpl(
    private val userService: UserService
) : AuthorizationController {

    override suspend fun authorizationGen(
        authorization: String,
        userInfoRest: UserInfoRest
    ): ResponseEntity<UserContentRest> {
        return userService.authorizationGen(userInfoRest)
            .run { ResponseEntity.ok(this) }
    }

    override suspend fun getAuthorization(userInfoRest: UserInfoRest): ResponseEntity<AuthorizationRest> {
        throw IllegalAccessException("Impl just to enable spring documentation on swagger ui")
    }
}
