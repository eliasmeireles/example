package com.eliasmeireles.kotlintesting.domain.model


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.softwareplace.springboot.data.domain.model.BaseEntity
import com.github.softwareplace.springboot.data.domain.sql.ApplicationConstants.DEFAULT_FALSE
import com.github.softwareplace.springboot.data.domain.sql.ApplicationConstants.SERIAL
import com.github.softwareplace.springboot.data.domain.sql.ApplicationConstants.VARCHAR_150_COLUMN_DEFINITION
import com.github.softwareplace.springboot.data.domain.sql.ApplicationConstants.VARCHAR_255_COLUMN_DEFINITION
import com.github.softwareplace.springboot.data.domain.sql.ApplicationConstants.VARCHAR_60_COLUMN_DEFINITION
import com.github.softwareplace.springboot.security.encrypt.Encrypt
import com.github.softwareplace.springboot.security.model.UserData
import com.github.softwareplace.springboot.security.validator.annotation.ValidEmail
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@Entity
@Table(name = "app_users")
data class AppUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var userId: Long? = null,
    @NotEmpty
    @Column(columnDefinition = VARCHAR_150_COLUMN_DEFINITION, nullable = false)
    var name: String,
    @ValidEmail
    @Column(columnDefinition = VARCHAR_150_COLUMN_DEFINITION, nullable = false, unique = true)
    var email: String,
    @Column(name = "password", columnDefinition = VARCHAR_60_COLUMN_DEFINITION, nullable = false)
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    var userPassword: String?,
    @Column(columnDefinition = DEFAULT_FALSE, nullable = false)
    var verifiedAccount: Boolean = false,
    @Column(columnDefinition = VARCHAR_60_COLUMN_DEFINITION)
    var accessToken: String? = null,
    @Column(columnDefinition = VARCHAR_60_COLUMN_DEFINITION)
    var salt: String? = null,
    @Column(columnDefinition = VARCHAR_60_COLUMN_DEFINITION)
    var authToken: String? = null,

) : BaseEntity(), UserData {


    @PrePersist
    fun beforePersist() {
        val encrypt = Encrypt(password = userPassword!!)
        this.userPassword = encrypt.encodedPassword
        this.salt = encrypt.salt
        this.accessToken = Encrypt(encrypt.encodedPassword).authToken
        this.authToken = encrypt.authToken
    }

    fun passwordUpdate(password: String) {
        val encrypt = Encrypt(password = password)
        this.userPassword = encrypt.encodedPassword
        this.salt = encrypt.salt
        this.accessToken = Encrypt(encrypt.encodedPassword).authToken
        this.authToken = encrypt.authToken
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return verifiedAccount
    }

    override fun authToken(): String {
        return accessToken ?: ""
    }

    @JsonIgnore
    override fun getUsername(): String {
        return email
    }

    override fun getPassword(): String? {
        return this.userPassword
    }
}
