package com.eliasmeireles.kotlintesting.security

import com.github.softwareplace.springboot.security.model.RequestUser
import com.github.softwareplace.springboot.security.model.UserData
import com.github.softwareplace.springboot.security.service.AuthorizationUserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class UserService : AuthorizationUserService {
    override fun findUser(user: RequestUser): UserData? {
        TODO("Not yet implemented")
    }

    override fun findUser(authToken: String): UserData? {
        TODO("Not yet implemented")
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        TODO("Not yet implemented")
    }
}
