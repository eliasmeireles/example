package com.eliasmeireles.kotlintesting.domain.service

import com.eliasmeireles.kotlintesting.domain.model.AppUser
import com.eliasmeireles.kotlintesting.domain.repository.UserRepository
import com.github.softwareplace.springboot.data.domain.repository.RepositoryUtils.findOneBy
import com.github.softwareplace.springboot.data.domain.repository.Spec
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository
) {

    fun findByUsername(email: String): AppUser? {
        val spec = validUserSpec()
            .andEquals("email", email)

        return repository.findOneBy(spec)
            .orElse(null)
    }

    fun findByAccessToken(accessToken: String): AppUser? {
        val spec = validUserSpec()
            .andEquals("accessToken", accessToken)

        return repository.findOneBy(spec)
            .orElse(null)
    }

    private fun validUserSpec() = Spec<AppUser>()
        .andEquals("verifiedAccount", true)
        .andIsNotDeleted()
}
