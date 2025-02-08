package com.eliasmeireles.kotlintesting.domain.exception

import org.apache.kafka.common.errors.ApiException

class UserNotFoundException: ApiException("User not found")
