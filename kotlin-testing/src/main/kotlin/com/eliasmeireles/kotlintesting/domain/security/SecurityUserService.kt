package com.eliasmeireles.kotlintesting.domain.security

import com.eliasmeireles.kotlintesting.domain.exception.UserNotFoundException
import com.eliasmeireles.kotlintesting.domain.model.AppUser
import com.eliasmeireles.kotlintesting.domain.service.UserService
import com.github.softwareplace.springboot.security.model.RequestUser
import com.github.softwareplace.springboot.security.model.UserData
import com.github.softwareplace.springboot.security.service.AuthorizationUserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class SecurityUserService(
    private val serviceUser: UserService
) : AuthorizationUserService {

    override fun findUser(user: RequestUser): AppUser? {
        return serviceUser.findByUsername(user.username)
    }

    override fun findUser(authToken: String): AppUser? {
        return serviceUser.findByAccessToken(authToken)
    }

    override fun loadUserByUsername(username: String): AppUser {
        return when (val user = serviceUser.findByAccessToken(username)) {
            null -> throw UserNotFoundException()
            else -> user
        }
    }
}
