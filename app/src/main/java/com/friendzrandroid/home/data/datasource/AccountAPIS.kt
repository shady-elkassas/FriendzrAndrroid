package com.friendzrandroid.home.data.datasource

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.home.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AccountAPIS {
    @POST("Account/getprofildata")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getProfileData(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<UserProfileData>>


    @POST("Account/GetAllValidatConfig")
    suspend fun getConfigurationValidation(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<ConfigurationValidation>>

    @POST("Account/update")
    @JvmSuppressWildcards
    @Multipart
    suspend fun updateProfileData(
        @Header("Authorization") authHeader: String,
        @PartMap requestBody: Map<String, RequestBody>,
        @Part UserImags: MultipartBody.Part?
    ): Response<BaseDataWrapper<UserProfileData>>


    @POST("Account/UserSetting")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getUserSettings(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<UserSettingsResponse>>


    @POST("Account/DeleteAccount")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun deleteAccount(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<Any>>

    @POST("Account/logout")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun logoutAccount(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<Any>>


    @POST("Account/changepass")
    @FormUrlEncoded
    suspend fun changePassword(
        @Header("Authorization") authHeader: String,
        @Field("newPassword") newPassword: String,
        @Field("oldPassword") oldPassword: String,

        ): Response<BaseDataWrapper<Any>>


    @POST("Account/LinkClick")
    @FormUrlEncoded
    suspend fun linkClick(
        @Header("Authorization") authHeader: String,
        @Field("key") key: String

    ): Response<BaseDataWrapper<Any>>

    @POST("Account/Updatelocation")
    @FormUrlEncoded
    suspend fun updateCurrentLocation(
        @Header("Authorization") authHeader: String,
        @Field("lang") lang: Double,
        @Field("lat") lat: Double
    ): Response<BaseDataWrapper<UpdateUserLocationResponse>>


    @POST("Account/updatSetting")
    @FormUrlEncoded
    suspend fun updateUserSettings(
        @Header("Authorization") authHeader: String,
        @Field("pushnotification") pushnotification: Boolean,
        @Field("allowmylocation") allowmylocation: Boolean,
        @Field("ghostmode") ghostmode: Boolean,
        @Field("myAppearanceTypes") allowMyAppearanceType: String,
        @Field("manualdistancecontrol") Manualdistancecontrol: Double,
        @Field("filteringaccordingtoage") Filteringaccordingtoage: Boolean,
        @Field("agefrom") agefrom: Int,
        @Field("ageto") ageto: Int,
        @Field("personalSpace") personalSpace: Boolean,
        @Field("distanceFilter") distanceFilter: Boolean
    ): Response<BaseDataWrapper<UserSettingsResponse>>

    @POST("Interests/GETIam")
    suspend fun getIamTags(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<List<InterestData>>>

    @POST("prefertoLISTES/GETpreferto")
    suspend fun getPreferTags(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<List<InterestData>>>

    @POST("Account/UpdateUserImages")
    @Multipart
    suspend fun updateAdditionalImages(
        @Header("Authorization") authHeader: String,
        @Part images: List<MultipartBody.Part>
    ): Response<BaseDataWrapper<Any>>

}