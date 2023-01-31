package com.friendzrandroid.home.data.repository

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.data.network.SafeApiRequest
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.datasource.CommunityAPIS
import com.friendzrandroid.home.data.model.community.RecentlyConnectedResponse
import com.friendzrandroid.home.data.model.community.RecommendedEventResponse
import com.friendzrandroid.home.data.model.community.RecommendedPeopleResponse
import com.friendzrandroid.home.domain.model.PagingListRequest
import javax.inject.Inject

class CommunityRepository @Inject constructor(private val api: CommunityAPIS) :
    SafeApiRequest() {


    suspend fun getRecommendPeople(userId: String): ResultWrapper<BaseDataWrapper<RecommendedPeopleResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getRecommendedPeople(
                token!!,
                userId
            )
        } as ResultWrapper<BaseDataWrapper<RecommendedPeopleResponse>>
    }

    suspend fun getRecommendEvents(eventId: String): ResultWrapper<BaseDataWrapper<RecommendedEventResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getRecommendedEvent(
                token!!,
                eventId
            )
        } as ResultWrapper<BaseDataWrapper<RecommendedEventResponse>>
    }

    suspend fun getRecentlyConnected(data: PagingListRequest): ResultWrapper<BaseDataWrapper<RecentlyConnectedResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getRecentlyConnected(
                token!!,
                data.pageSize,
                data.PageNumber
            )
        } as ResultWrapper<BaseDataWrapper<RecentlyConnectedResponse>>
    }

}