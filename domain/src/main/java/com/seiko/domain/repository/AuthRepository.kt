package com.seiko.domain.repository

import com.seiko.domain.entities.UserEntity

interface AuthRepository {

    fun login(params: Map<String, String>): UserEntity


}