package com.friendzrandroid.home.data.datasource

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.home.data.EventChatResponse
import com.friendzrandroid.home.data.UserChatResponse
import com.friendzrandroid.home.data.model.NotificationsResponse
import com.friendzrandroid.home.data.model.UsersInChatDataResponse
import com.friendzrandroid.home.data.model.UsersInChatSearchResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MessageAPIS {
    @POST("Messages/UsersinChat")
    @FormUrlEncoded
    suspend fun getUsersInConnect(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("search") search: String? = null
    ): Response<BaseDataWrapper<UsersInChatDataResponse>>

    @POST("Messages/NotificationData")
    @FormUrlEncoded
    suspend fun getNotifications(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int
    ): Response<BaseDataWrapper<NotificationsResponse>>


    @POST("Messages/EventChat")
    @FormUrlEncoded
    suspend fun getEventChat(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("Eventid") Eventid: String
    ): Response<BaseDataWrapper<EventChatResponse>>


    @POST("Messages/Chatdata")
    @FormUrlEncoded
    suspend fun getUserChat(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("userid") userId: String
    ): Response<BaseDataWrapper<UserChatResponse>>


    @POST("Messages/SendEventMessage")
    @JvmSuppressWildcards
    @Multipart
    suspend fun sendEventMessage(
        @Header("Authorization") authHeader: String,
        @PartMap requestBody: Map<String, RequestBody>,
        @Part Attach: MultipartBody.Part?
    ): Response<BaseDataWrapper<Any>>


    @POST("Messages/SendChatGroupMessage")
    @JvmSuppressWildcards
    @Multipart
    suspend fun sendGroupMessage(
        @Header("Authorization") authHeader: String,
        @PartMap requestBody: Map<String, RequestBody>,
        @Part Attach_File: MultipartBody.Part?
    ): Response<BaseDataWrapper<Any>>

    @POST("Messages/SendMessage")
    @JvmSuppressWildcards
    @Multipart
    suspend fun sendUserMessage(
        @Header("Authorization") authHeader: String,
        @PartMap requestBody: Map<String, RequestBody>,
        @Part Attach: MultipartBody.Part?
    ): Response<BaseDataWrapper<Any>>


    @POST("Messages/SearshUsersinChat")
    @FormUrlEncoded
    suspend fun searchInInbox(
        @Header("Authorization") authHeader: String,
        @Field("username") username: String
    ): Response<BaseDataWrapper<UsersInChatSearchResponse>>

    @POST("Messages/muitchat")
    @FormUrlEncoded
    suspend fun actionMuteChat(
        @Header("Authorization") authHeader: String,
        @Field("id") chatId: String,
        @Field("muit") mute: Boolean,
        @Field("isevent") isEvent: Boolean
    ): Response<BaseDataWrapper<Any>>


    @POST("Messages/Deletchat")
    @FormUrlEncoded
    suspend fun actionDeleteChat(
        @Header("Authorization") authHeader: String,
        @Field("id") chatId: String,
        @Field("DeleteDateTime") timeAndDate: String,
        @Field("isevent") isEvent: Boolean
    ): Response<BaseDataWrapper<Any>>
}