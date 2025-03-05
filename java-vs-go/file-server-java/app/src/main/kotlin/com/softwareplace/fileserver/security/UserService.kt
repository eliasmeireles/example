package com.softwareplace.fileserver.security

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.softwareplace.springboot.security.encrypt.Encrypt
import com.github.softwareplace.springboot.security.exception.ApiBaseException
import com.github.softwareplace.springboot.security.model.RequestUser
import com.github.softwareplace.springboot.security.service.AuthorizationUserService
import com.github.softwareplace.springboot.security.util.ReadFilesUtils
import com.softwareplace.fileserver.properties.AppProperties
import com.softwareplace.fileserver.rest.model.UserContentRest
import com.softwareplace.fileserver.rest.model.UserInfoRest
import com.softwareplace.fileserver.security.model.InMemoryUser
import com.softwareplace.fileserver.security.model.toInMemoryUser
import jakarta.annotation.PostConstruct
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class UserService(
    private val properties: AppProperties,
    private val objectMapper: ObjectMapper,
    private val resourceLoader: ResourceLoader,
) : AuthorizationUserService {

    @PostConstruct
    fun postConstruct() {
        if (inMemoryUsers.isEmpty()) {
            throw RuntimeException("At least one user must be registered.")
        }
    }

    private val inMemoryUsers: List<InMemoryUser> by lazy {
        val resource = ReadFilesUtils.readFileBytes(resourceLoader, properties.authorizationPath)
        objectMapper.readValue(resource, object : TypeReference<List<UserContentRest>>() {})
            .map(UserContentRest::toInMemoryUser)
    }

    override fun findUser(user: RequestUser): InMemoryUser? {
        return inMemoryUsers.firstOrNull { it.username == user.username }
    }

    override fun findUser(authToken: String): InMemoryUser? {
        return inMemoryUsers.firstOrNull { it.authToken() == authToken }
    }

    override fun loadUserByUsername(username: String?): InMemoryUser? {
        return inMemoryUsers.firstOrNull { it.authToken() == username }
    }

    fun authorizationGen(userInfoRest: UserInfoRest): UserContentRest {
        val encrypt = Encrypt(userInfoRest.password)

        val user: InMemoryUser? = inMemoryUsers.firstOrNull { it.username == userInfoRest.username }

        if (user != null) {
            throw ApiBaseException(status = HttpStatus.BAD_REQUEST, message = "Username is not available.")
        }

        return UserContentRest(
            password = encrypt.encodedPassword,
            authToken = encrypt.authToken,
            username = userInfoRest.username
        )
    }
}
