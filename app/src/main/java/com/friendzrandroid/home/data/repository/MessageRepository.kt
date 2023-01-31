package com.friendzrandroid.home.data.repository

import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.data.network.SafeApiRequest
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.EventChatResponse
import com.friendzrandroid.home.data.UserChatResponse
import com.friendzrandroid.home.data.datasource.MessageAPIS
import com.friendzrandroid.home.data.model.NotificationsResponse
import com.friendzrandroid.home.data.model.UsersInChatDataResponse
import com.friendzrandroid.home.data.model.UsersInChatSearchResponse
import com.friendzrandroid.home.domain.model.ChatOptionRequest
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import javax.inject.Inject

class MessageRepository @Inject constructor(private val apis: MessageAPIS) : SafeApiRequest() {


    suspend fun getUsersInChat(
        pageSize: Int,
        pageNumber: Int,
        search: String?
    ): ResultWrapper<BaseDataWrapper<UsersInChatDataResponse>> {


        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.getUsersInConnect(
                token!!,
                pageSize,
                pageNumber,
                search
            )
        } as ResultWrapper<BaseDataWrapper<UsersInChatDataResponse>>
    }


    suspend fun getNotifications(
        pageSize: Int,
        pageNumber: Int
    ): ResultWrapper<BaseDataWrapper<NotificationsResponse>> {


        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.getNotifications(
                token!!,
                pageSize,
                pageNumber
            )
        } as ResultWrapper<BaseDataWrapper<NotificationsResponse>>
    }


    suspend fun getEventMessages(
        pageSize: Int,
        pageNumber: Int,
        eventID: String
    ): ResultWrapper<BaseDataWrapper<EventChatResponse>> {


        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.getEventChat(
                token!!,
                pageSize,
                pageNumber,
                eventID
            )
        } as ResultWrapper<BaseDataWrapper<EventChatResponse>>
    }


    suspend fun getUserMessages(
        pageSize: Int,
        pageNumber: Int,
        userID: String
    ): ResultWrapper<BaseDataWrapper<UserChatResponse>> {


        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.getUserChat(
                token!!,
                pageSize,
                pageNumber,
                userID
            )
        } as ResultWrapper<BaseDataWrapper<UserChatResponse>>
    }

    suspend fun sendEventMessage(data: FormDataRequestWithImage): ResultWrapper<BaseDataWrapper<Any>> {


        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.sendEventMessage(
                token!!,
                data.userData,
                data.UserImags
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }


    suspend fun sendUserMessage(data: FormDataRequestWithImage): ResultWrapper<BaseDataWrapper<Any>> {


        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.sendUserMessage(
                token!!,
                data.userData,
                data.UserImags
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }


    suspend fun sendGroupMessage(data: FormDataRequestWithImage): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        return apiRequest {
            apis.sendGroupMessage(
                token!!,
                data.userData,
                data.UserImags
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }


    suspend fun searchInInbox(data: String): ResultWrapper<BaseDataWrapper<UsersInChatSearchResponse>> {


        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.searchInInbox(
                token!!,
                data
            )
        } as ResultWrapper<BaseDataWrapper<UsersInChatSearchResponse>>
    }

    suspend fun muteChat(data: ChatOptionRequest): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.actionMuteChat(
                token!!,
                data.id,
                data.mute!!,
                data.isEvent!!
            )
        } as ResultWrapper<BaseDataWrapper<Any>>

    }

    suspend fun deleteChat(data: ChatOptionRequest): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.actionDeleteChat(
                token!!,
                data.id,
                data.DeleteDateTime!!,
                data.isEvent!!
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }
}