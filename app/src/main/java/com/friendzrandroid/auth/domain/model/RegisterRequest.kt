package com.friendzrandroid.auth.domain.model

import com.friendzrandroid.auth.data.model.LoginRegisterTypeEnum

data class RegisterRequest(
    val userName: String,
    val email: String,
    val password: String,
    val loginType: LoginRegisterTypeEnum,
    val userId: String,
    val platform: LoginRegisterTypeEnum


)