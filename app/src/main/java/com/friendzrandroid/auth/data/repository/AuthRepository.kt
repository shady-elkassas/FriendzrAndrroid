package com.friendzrandroid.auth.data.repository

import com.friendzrandroid.auth.data.datasource.AuthAPIS
import com.friendzrandroid.auth.data.model.LoginResponseData
import com.friendzrandroid.auth.data.model.LoginRegisterTypeEnum
import com.friendzrandroid.auth.data.model.RegistrationResponse
import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.data.network.SafeApiRequest
import javax.inject.Inject


class AuthRepository @Inject constructor(private val api: AuthAPIS) : SafeApiRequest() {


    suspend fun login(
        email: String,
        password: String,
        logintype: LoginRegisterTypeEnum,
        FcmToken: String = "",
        UserID: String = "",

        platform: LoginRegisterTypeEnum

    ): ResultWrapper<BaseDataWrapper<LoginResponseData>> {
        return apiRequest {
            api.login(
                email,
                password,
                FcmToken,
                logintype.typeID,
                UserID,
                platform.typeID
            )
        } as ResultWrapper<BaseDataWrapper<LoginResponseData>>
    }

    suspend fun register(
        userName: String,
        email: String,
        password: String,
        logintype: LoginRegisterTypeEnum,
        UserID: String,
        platform: LoginRegisterTypeEnum
    ): ResultWrapper<BaseDataWrapper<RegistrationResponse>> {
        return apiRequest {

            api.register(
                userName,
                email,
                password,
                logintype.typeID,
                UserID,
                platform.typeID
            )
        } as ResultWrapper<BaseDataWrapper<RegistrationResponse>>

    }

    suspend fun forgetPassword(email: String): ResultWrapper<BaseDataWrapper<Any>> {
        return apiRequest {
            api.forgetPassword(
                email
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }
}