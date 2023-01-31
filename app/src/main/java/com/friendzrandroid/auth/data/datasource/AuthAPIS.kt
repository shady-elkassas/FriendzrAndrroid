package com.friendzrandroid.auth.data.datasource

import com.friendzrandroid.auth.data.model.LoginRegisterTypeEnum
import com.friendzrandroid.auth.data.model.LoginResponseData
import com.friendzrandroid.auth.data.model.RegistrationResponse
import com.friendzrandroid.core.data.network.BaseDataWrapper
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface AuthAPIS {

    @FormUrlEncoded
    @POST("Authenticat/login")
    suspend fun login(
        @Field("email") email:String,
        @Field("Password") Password:String,
        @Field("FcmToken") FcmToken:String,
        @Field("logintype") logintype:Int,
        @Field("UserId") UserId:String,
        @Field("platform") PlatForm: Int
    ): Response<BaseDataWrapper<LoginResponseData>>

    @FormUrlEncoded
    @POST("Authenticat/register")
    suspend fun register(
        @Field("username")username:String,
        @Field("Email") email :String,
        @Field("Password") Password:String,
        @Field("registertype") logintype:Int,
        @Field("UserId") UserId:String,
        @Field("platform") PlatForm:Int
    ): Response<BaseDataWrapper<RegistrationResponse>>



    @FormUrlEncoded
    @POST("Authenticat/forgetpass")
    suspend fun forgetPassword(
        @Field("Email")Email:String
    ): Response<BaseDataWrapper<Any>>



}