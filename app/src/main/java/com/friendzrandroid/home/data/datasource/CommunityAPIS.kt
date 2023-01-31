package com.friendzrandroid.home.data.datasource

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.home.data.model.community.RecentlyConnectedResponse
import com.friendzrandroid.home.data.model.community.RecommendedEventResponse
import com.friendzrandroid.home.data.model.community.RecommendedPeopleResponse
import retrofit2.Response
import retrofit2.http.*


interface CommunityAPIS {

    @GET("FrindRequest/RecommendedPeople")
    suspend fun getRecommendedPeople(
        @Header("Authorization") authHeader: String,
        @Query("userId") userId: String
    ): Response<BaseDataWrapper<RecommendedPeopleResponse>>

    @GET("Events/RecommendedEvent")
    suspend fun getRecommendedEvent(
        @Header("Authorization") authHeader: String,
        @Query("eventId") userId: String
    ): Response<BaseDataWrapper<RecommendedEventResponse>>


    @GET("FrindRequest/RecentlyConnected")
    suspend fun getRecentlyConnected(
        @Header("Authorization") authHeader: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageNumber") pageNumber: Int
    ): Response<BaseDataWrapper<RecentlyConnectedResponse>>
}