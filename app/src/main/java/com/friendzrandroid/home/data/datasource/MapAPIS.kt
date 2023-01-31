package com.friendzrandroid.home.data.datasource

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.home.data.model.EventAroundMeResponse
import com.friendzrandroid.home.data.model.EventByLocationResponse
import com.friendzrandroid.home.data.model.EventAllLocationResponse
import retrofit2.Response
import retrofit2.http.*

interface MapAPIS {

    @POST("Events/Eventsaroundme")
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getEventsAroundMe(
        @Header("Authorization") authHeader: String,
        @Field("categories") filterSelectedTags: String,
        @Field("lang") lang: Double,
        @Field("lat") lat: Double
    ): Response<BaseDataWrapper<EventAroundMeResponse>>

    @FormUrlEncoded
    @POST("Public/EventsAroundMe")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getEventsAroundMePublic(
        @Field("lang") lang: Double,
        @Field("lat") lat: Double
    ): Response<BaseDataWrapper<EventAroundMeResponse>>


    //we can use this api to get events based on location when click on marker
    @POST("Events/locationEvente")
    @FormUrlEncoded
    suspend fun getEventsByLocation(
        @Header("Authorization") authHeader: String,
        @Field("lat") lat: Double,
        @Field("lang") lang: Double
    ): Response<BaseDataWrapper<EventByLocationResponse>>


    @POST("Events/AllLocationEvente")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getAllLocationEvent(
        @Header("Authorization") authHeader: String
    ): Response<BaseDataWrapper<EventAllLocationResponse>>

}