package com.seiko.domain.entity

data class UserEntity(
    val appScope: String,
    val errorCode: Int,
    val errorMessage: String,
    val legacyTokenNumber: Int,
    val profileImage: String,
    val registerRequired: Boolean,
    val screenName: String,
    val success: Boolean,
    val token: String,
    val tokenExpireTime: String,
    val userId: Int,
    val userName: String,
    val userType: String
)