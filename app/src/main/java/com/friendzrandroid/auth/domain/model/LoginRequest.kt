package com.friendzrandroid.auth.domain.model

import com.friendzrandroid.auth.data.model.LoginRegisterTypeEnum

data class LoginRequest(
    val email:String,
    val password:String,
    val fcmToken:String,
    val loginType: LoginRegisterTypeEnum,
    val userId: String,
    val platform: LoginRegisterTypeEnum


)