package com.eliasmeireles.kotlintesting.domain.repository

import com.eliasmeireles.kotlintesting.domain.model.AppUser
import com.github.softwareplace.springboot.data.domain.repository.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : BaseRepository<AppUser, Long>
