package com.eliasmeireles.kotlintesting.it.controller

import com.eliasmeireles.kotlintesting.domain.model.AppUser
import com.eliasmeireles.kotlintesting.it.BaseIT
import com.eliasmeireles.kotlintesting.rest.model.AuthorizationRest
import com.eliasmeireles.kotlintesting.rest.model.ResponseRest
import com.eliasmeireles.kotlintesting.rest.model.UserInfoRest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class AuthorizationControllerIT : BaseIT() {

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    @Test
    fun `must return expected 401 when user data is incorrect`() {
        Given {
            val authorizationBody = UserInfoRest(
                username = "any-username",
                password = "any-password"
            )
            spec
                .body(authorizationBody)
                .contentType("application/json")

        } When {
            post("/authorization")
        } Then {
            log()
                .all(true)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
        } Extract {
            val response = body().`as`(ResponseRest::class.java)
            assertThat(response).isNotNull()

            assertThat(response.errorInfo).isNull()
            assertThat(response.timestamp).isNotNull()
            assertThat(401).isEqualTo(response.code)
            assertTrue(response.timestamp > 1739032122648)
            assertThat("Access denied!")
            assertFalse(response.success)
        }
    }

    @Test
    fun `must return expected 401 when user data is correct but account is not verified`() {
        val appUser = AppUser(
            userPassword = "ye2QJmj6HquAcOJg6pOk1HrM",
            email = "eliasmeirelesf@mail.com",
            name = "Elias Meireles"
        )

        userRepository.save(appUser)

        Given {
            val authorizationBody = UserInfoRest(
                username = "eliasmeirelesf@mail.com",
                password = "ye2QJmj6HquAcOJg6pOk1HrM"
            )

            spec
                .body(authorizationBody)
                .contentType("application/json")

        } When {
            post("/authorization")
        } Then {
            log()
                .all(true)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
        } Extract {
            val response = body().`as`(ResponseRest::class.java)
            assertThat(response).isNotNull()

            assertThat(response.errorInfo).isNull()
            assertThat(response.timestamp).isNotNull()
            assertThat(401).isEqualTo(response.code)
            assertTrue(response.timestamp > 1739032122648)
            assertThat("Access denied!")
            assertFalse(response.success)
        }
    }

    @Test
    fun `must return expected login response when user data is correctly provided`() {
        val appUser = AppUser(
            userPassword = "ye2QJmj6HquAcOJg6pOk1HrM",
            email = "eliasmeirelesf@mail.com",
            name = "Elias Meireles",
            verifiedAccount = true
        )

        userRepository.save(appUser)

        Given {
            val authorizationBody = UserInfoRest(
                username = "eliasmeirelesf@mail.com",
                password = "ye2QJmj6HquAcOJg6pOk1HrM"
            )

            spec
                .body(authorizationBody)
                .contentType("application/json")

        } When {
            post("/authorization")
        } Then {
            log()
                .all(true)
                .statusCode(HttpStatus.OK.value())
        } Extract {
            val response = body().`as`(AuthorizationRest::class.java)
            assertThat(response).isNotNull()

            assertThat(response.errorInfo).isNull()
            assertThat(response.timestamp).isNotNull()
            assertTrue(response.timestamp > 1739032122648)
            assertThat("Authorization successful.").isEqualTo(response.message)
            assertTrue(response.success)
            assertThat(response.jwt).isNotNull()
        }
    }
}
