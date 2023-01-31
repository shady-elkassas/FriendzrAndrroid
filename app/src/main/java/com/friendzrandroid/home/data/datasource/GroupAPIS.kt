package com.friendzrandroid.home.data.datasource

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.home.data.model.GroupChatMsgResponse
import com.friendzrandroid.home.data.model.UsersInChatDataResponse
import com.friendzrandroid.home.data.model.groupchat.GroupChatResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface GroupAPIS {

    @POST("ChatGroup/Create")
    @JvmSuppressWildcards
    @Multipart
    suspend fun createGroupChat(
        @Header("Authorization") authHeader: String,
        @PartMap requestBody: Map<String, RequestBody>,
        @Part Image_File: MultipartBody.Part?
    ): Response<BaseDataWrapper<GroupChatResponse>>


    @POST("ChatGroup/GetChatGroup")
    @FormUrlEncoded
    suspend fun getChatGroup(
        @Header("Authorization") authHeader: String,
        @Field("ID") chatId: String
    ): Response<BaseDataWrapper<GroupChatResponse>>


    @POST("ChatGroup/GetChat")
    @FormUrlEncoded
    suspend fun getChatGroupInbox(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("ID") groupId: String
    ): Response<BaseDataWrapper<GroupChatMsgResponse>>


    @POST("ChatGroup/Update")
    @JvmSuppressWildcards
    @Multipart
    suspend fun updateGroupChat(
        @Header("Authorization") authHeader: String,
        @PartMap requestBody: Map<String, RequestBody>,
        @Part Image_File: MultipartBody.Part?
    ): Response<BaseDataWrapper<Any>>


    @POST("ChatGroup/AddUsers")
    @FormUrlEncoded
    suspend fun addUsers(
        @Header("Authorization") authHeader: String,
        @Field("ID") chatId: String,
        @Field("RegistrationDateTime") registerDate: String,
        @Field("ListOfUserIDs") listOfUsers: String
    ): Response<BaseDataWrapper<Any>>


    @POST("ChatGroup/kickOutUser")
    @FormUrlEncoded
    suspend fun removeUserFromGroup(
        @Header("Authorization") authHeader: String,
        @Field("ID") chatId: String,
        @Field("RegistrationDateTime") registerDate: String,
        @Field("ListOfUserIDs") listOfUsers: String,
    ): Response<BaseDataWrapper<Any>>

    @POST("ChatGroup/Remove")
    @FormUrlEncoded
    suspend fun removeGroup(
        @Header("Authorization") authHeader: String,
        @Field("ID") chatId: String
    ): Response<BaseDataWrapper<Any>>


    @POST("ChatGroup/Leave")
    @FormUrlEncoded
    suspend fun leaveGroupChat(
        @Header("Authorization") authHeader: String,
        @Field("ID") chatId: String,
        @Field("RegistrationDateTime") registerDate: String,
    ): Response<BaseDataWrapper<Any>>

    @POST("ChatGroup/ClearChatGroup")
    @FormUrlEncoded
    suspend fun clearChatGroup(
        @Header("Authorization") authHeader: String,
        @Field("ID") chatId: String,
        @Field("RegistrationDateTime") registerDate: String,
    ): Response<BaseDataWrapper<Any>>


    @POST("ChatGroup/GetAllChats")
    @FormUrlEncoded
    suspend fun getMyChatGroups(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("search") search: String? = null
    ): Response<BaseDataWrapper<UsersInChatDataResponse>>


    @POST("ChatGroup/MuteChatGroup")
    @FormUrlEncoded
    suspend fun actionMuteGroupChat(
        @Header("Authorization") authHeader: String,
        @Field("ID") chatId: String,
        @Field("IsMuted") mute: Boolean
    ): Response<BaseDataWrapper<Any>>

}