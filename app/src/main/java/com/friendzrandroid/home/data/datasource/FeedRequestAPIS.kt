package com.friendzrandroid.home.data.datasource

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.home.data.model.AllUsersResponse
import com.friendzrandroid.home.data.model.UserProfileData
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface FeedRequestAPIS {


//    @Field("userlang") userlang: Double,
//    @Field("userlat") userlat: Double

    @POST("FrindRequest/AllUsers")
    @FormUrlEncoded
    suspend fun getAllUsers(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("userlang") Lang: Double,
        @Field("userlat") Lat: Double,
        @Field("degree") degree: Int,
        @Field("sortByInterestMatch") sortByInterestMatch: Boolean
    ): Response<BaseDataWrapper<AllUsersResponse>>

    @POST("Public/AllUserPublic")
    @FormUrlEncoded
    suspend fun getAllUsersPublic(

        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("userlang") Lang: Double,
        @Field("userlat") Lat: Double,
        @Field("degree") degree: Int
    ): Response<BaseDataWrapper<AllUsersResponse>>


    @POST("FrindRequest/AllFriendes")
    @FormUrlEncoded
    suspend fun myFriends(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("search") searchText: String? = null
    ): Response<BaseDataWrapper<AllUsersResponse>>

    @POST("FrindRequest/Allrequest")
    @FormUrlEncoded
    suspend fun getAllRequests(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("requestesType") requestType: Int
    ): Response<BaseDataWrapper<AllUsersResponse>>


    @POST("FrindRequest/Userprofil")
    @FormUrlEncoded
    suspend fun getProfile(
        @Header("Authorization") authHeader: String,
        @Field("userid") userid: String
    ): Response<BaseDataWrapper<UserProfileData>>


    @FormUrlEncoded
    @POST("FrindRequest/RequestFriendStatus")
    suspend fun updateUserStatus(
        @Header("Authorization") authHeader: String,
        @Field("userid") userid: String,
        @Field("key") key: Int,
        @Field("isNotFriend") isNotFriend: Boolean? = null,
        @Field("Requestdate") Requestdate: String,
    ): Response<BaseDataWrapper<Any>>


    @POST("FrindRequest/AllBlocked")
    @FormUrlEncoded
    suspend fun getAllBlockedUser(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
    ): Response<BaseDataWrapper<AllUsersResponse>>

}