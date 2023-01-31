package com.friendzrandroid.home.data.repository

import android.util.Log
import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.data.network.SafeApiRequest
import com.friendzrandroid.core.utils.FormatToDate
import com.friendzrandroid.core.utils.FormatToTime
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.datasource.FeedRequestAPIS
import com.friendzrandroid.home.data.model.AllUsersResponse
import com.friendzrandroid.home.data.model.UserProfileData
import java.util.*
import javax.inject.Inject

class FeedRequestRepository @Inject constructor(private val api: FeedRequestAPIS) :
    SafeApiRequest() {

    suspend fun getFeedUsers(
        pageSize: Int,
        PageNumber: Int,
        degree: Int,
        Lang: Double,
        Lat: Double,
        sortByInterestMatch: Boolean
    ): ResultWrapper<BaseDataWrapper<AllUsersResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            if (token != null && !token.equals("")) {

                api.getAllUsers(
                    token,
                    pageSize,
                    PageNumber, Lang,
                    Lat,
                    degree,
                    sortByInterestMatch
                )
            } else {

                api.getAllUsersPublic(

                    pageSize,
                    PageNumber,
                    Lang,
                    Lat,
                    degree
                )

            }

        } as ResultWrapper<BaseDataWrapper<AllUsersResponse>>
    }

    suspend fun getMyFriends(
        pageSize: Int,
        PageNumber: Int,
        searchText: String?
    ): ResultWrapper<BaseDataWrapper<AllUsersResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.myFriends(
                token!!,
                pageSize,
                PageNumber,
                searchText
            )
        } as ResultWrapper<BaseDataWrapper<AllUsersResponse>>
    }


    suspend fun getRequests(
        pageSize: Int,
        PageNumber: Int,
        requestType: Int
    ): ResultWrapper<BaseDataWrapper<AllUsersResponse>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getAllRequests(
                token!!,
                pageSize,
                PageNumber,
                requestType
            )
        } as ResultWrapper<BaseDataWrapper<AllUsersResponse>>
    }


    suspend fun updateUserStatus(
        userId: String,
        key: Int,
        isNotFriend: Boolean? = null
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        val currentDate = Date()
        val mesgDate = currentDate.FormatToDate()
        val mesgTime = currentDate.FormatToTime("hh:mm:ss")

        val date = "$mesgDate $mesgTime"

        return apiRequest {
            api.updateUserStatus(
                token!!,
                userId,
                key,
                isNotFriend,
                date
            )
        } as ResultWrapper<BaseDataWrapper<Any>>

    }


    suspend fun getUserProfile(userId: String): ResultWrapper<BaseDataWrapper<UserProfileData>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getProfile(
                token!!,
                userId
            )
        } as ResultWrapper<BaseDataWrapper<UserProfileData>>
    }

    suspend fun getAllBlockedUsers(
        pageSize: Int,
        pageNumber: Int,
    ): ResultWrapper<BaseDataWrapper<AllUsersResponse>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            api.getAllBlockedUser(
                token!!,
                pageSize,
                pageNumber
            )
        } as ResultWrapper<BaseDataWrapper<AllUsersResponse>>
    }

}