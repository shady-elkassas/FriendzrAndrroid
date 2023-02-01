package com.friendzrandroid.home.data.datasource

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.home.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface EventAPIS {


    @POST("Interests/GetAllInterests")
//    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getAllInterests(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<List<InterestData>>>

    @POST("Events/GetAllcategory")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getAllCategories(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<List<InterestData>>>

    @POST("Public/GetCategories")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getAllCategoriesPublic(
    ): Response<BaseDataWrapper<List<InterestData>>>


    @POST("Events/getMyEvent")
    @FormUrlEncoded
    suspend fun getEvents(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("search") search: String? = null
    ): Response<BaseDataWrapper<EventsResponse>>


    @POST("Events/OnlyEventsAroundUser")
    @FormUrlEncoded
    suspend fun getOnlyEventsAround(
        @Header("Authorization") authHeader: String,
        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("lat") lat: Double,
        @Field("lang") lang: Double,
        @Field("categories") filterSelectedTags: String,
        @Field("dateCriteria") dateCriteria: String?,
        @Field("startDate") startDate: String?,
        @Field("endDate") endDate: String?
    ): Response<BaseDataWrapper<EventsResponse>>


    @POST("Public/OnlyEventsAround")
    @FormUrlEncoded
    suspend fun getOnlyEventsAroundPublic(

        @Field("pageSize") pageSize: Int,
        @Field("pageNumber") pageNumber: Int,
        @Field("lat") lat: Double,
        @Field("lang") lang: Double
    ): Response<BaseDataWrapper<EventsResponse>>


//    @POST("Events/getallEvent")
//    @FormUrlEncoded
//    suspend fun getEvents(
//        @Header("Authorization") authHeader: String,
//        @Field("pageSize") pageSize:Int,
//        @Field("pageNumber") pageNumber:Int
//        ): Response<BaseDataWrapper<EventsResponse>>


    @POST("Events/getEvent")
    @FormUrlEncoded
    suspend fun getEventDetails(
        @Header("Authorization") authHeader: String,
        @Field("id") id: String
    ): Response<BaseDataWrapper<EventDetails>>


    @POST("Events/geteventtype")
    suspend fun getEventType(
        @Header("Authorization") authHeader: String,
    ): Response<BaseDataWrapper<List<EventTypesResponse>>>


    @POST("Events/AddEventData")
    @JvmSuppressWildcards
    @Multipart
    suspend fun addEvent(
        @Header("Authorization") authHeader: String,
        @PartMap requestBody: Map<String, RequestBody>,
        @Part UserImags: MultipartBody.Part?
    ): Response<BaseDataWrapper<Any>>

    @POST("Events/updateEventData")
    @JvmSuppressWildcards
    @Multipart
    suspend fun UpdateEvent(
        @Header("Authorization") authHeader: String,
        @PartMap requestBody: Map<String, RequestBody>,
        @Part UserImags: MultipartBody.Part?
    ): Response<BaseDataWrapper<Any>>


    @POST("Events/DeletEventData")
    @FormUrlEncoded
    suspend fun deleteEvent(
        @Header("Authorization") authHeader: String,
        @Field("id") EventDataid: String
    ): Response<BaseDataWrapper<Any>>


    @POST("Events/joinEvent")
    @FormUrlEncoded
    suspend fun joinEvent(
        @Header("Authorization") authHeader: String,
        @Field("EventDataid") EventDataid: String,
        @Field("JoinDate") JoinDate: String,
        @Field("Jointime") Jointime: String

    ): Response<BaseDataWrapper<Any>>

    @POST("Events/getEventAttende")
    @FormUrlEncoded
    suspend fun getEventAttendace(
        @Header("Authorization") authHeader: String,
        @Field("id") EventDataid: String
    ): Response<BaseDataWrapper<EventAttende>>


    @POST("Events/leaveEvent")
    @FormUrlEncoded
    suspend fun leaveEvent(
        @Header("Authorization") authHeader: String,
        @Field("EventDataid") EventDataid: String,
        @Field("leaveeventDate") leaveDate: String,
        @Field("leaveeventtime") leadeTime: String
    ): Response<BaseDataWrapper<Any>>


    @POST("Events/Clickoutevent")
    @FormUrlEncoded
    suspend fun cickOutUser(
        @Header("Authorization") authHeader: String,
        @Field("EventDataid") EventDataid: String,
        @Field("UserattendId") UserattendId: String,
        @Field("stutus") stutus: Int,
        @Field("ActionDate") ActionDate: String,
        @Field("Actiontime") Actiontime: String
    ): Response<BaseDataWrapper<Any>>


    @POST("Events/leaveeventchat")
    @FormUrlEncoded
    suspend fun leaveEventChat(
        @Header("Authorization") authHeader: String,
        @Field("EventDataid") EventDataid: String,
        @Field("ActionDate") ActionDate: String,
        @Field("Actiontime") Actiontime: String
    ): Response<BaseDataWrapper<Any>>

}