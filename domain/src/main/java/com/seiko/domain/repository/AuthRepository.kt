package com.seiko.domain.repository

import com.seiko.domain.entity.UserEntity

interface AuthRepository {

    fun login(params: Map<String, String>): UserEntity


}