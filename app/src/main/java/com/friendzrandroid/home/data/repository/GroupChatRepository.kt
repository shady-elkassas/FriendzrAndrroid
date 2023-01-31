package com.friendzrandroid.home.data.repository

import android.util.Log
import com.friendzrandroid.core.data.network.BaseDataWrapper
import com.friendzrandroid.core.data.network.ResultWrapper
import com.friendzrandroid.core.data.network.SafeApiRequest
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.data.datasource.GroupAPIS
import com.friendzrandroid.home.data.model.GroupChatMsgResponse
import com.friendzrandroid.home.data.model.UsersInChatDataResponse
import com.friendzrandroid.home.data.model.groupchat.GroupChatResponse
import com.friendzrandroid.home.domain.model.ChatOptionRequest
import com.friendzrandroid.home.domain.model.FormDataRequestWithImage
import com.friendzrandroid.home.domain.model.PagingListRequest
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GroupChatRepository @Inject constructor(val apis: GroupAPIS) : SafeApiRequest() {

    suspend fun createGroup(data: FormDataRequestWithImage): ResultWrapper<BaseDataWrapper<GroupChatResponse>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.createGroupChat(
                token!!,
                data.userData,
                data.UserImags
            )
        } as ResultWrapper<BaseDataWrapper<GroupChatResponse>>
    }

    suspend fun getChatDetails(chatId: String): ResultWrapper<BaseDataWrapper<GroupChatResponse>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.getChatGroup(
                token!!,
                chatId
            )
        } as ResultWrapper<BaseDataWrapper<GroupChatResponse>>
    }

    suspend fun getGroupChat(
        pageSize: Int,
        pageNumber: Int,
        groupID: String
    ): ResultWrapper<BaseDataWrapper<GroupChatMsgResponse>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.getChatGroupInbox(
                token!!,
                pageSize,
                pageNumber,
                groupID
            )
        } as ResultWrapper<BaseDataWrapper<GroupChatMsgResponse>>

    }

    suspend fun updateGroupChat(data: FormDataRequestWithImage): ResultWrapper<BaseDataWrapper<Any>> {

        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.updateGroupChat(
                token!!,
                data.userData,
                data.UserImags
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun addUserToChat(
        id: String,
        data: String,
        userList: String
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.addUsers(
                token!!,
                id,
                data,
                userList
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }


    suspend fun removeUserFromChat(
        id: String,
        userId: String,
        date: String
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.removeUserFromGroup(
                token!!,
                id,
                userId,
                date
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun removeGroupChat(
        id: String
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.removeGroup(
                token!!,
                id

            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun leaveGroupChat(
        id: String
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        val currentDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val date = "$currentDate $currentTime"
        Log.d("chat", "token: $token")

        return apiRequest {
            apis.leaveGroupChat(
                token!!,
                id,
                date
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun clearGroupChat(
        id: String
    ): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()
        val currentDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val date = "$currentDate $currentTime"
        Log.d("chat", "token: $token")

        return apiRequest {
            apis.clearChatGroup(
                token!!,
                id,
                date
            )
        } as ResultWrapper<BaseDataWrapper<Any>>
    }

    suspend fun getMyGroupChats(
        param: PagingListRequest
    ): ResultWrapper<BaseDataWrapper<UsersInChatDataResponse>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.getMyChatGroups(
                token!!,
                param.pageSize,
                param.PageNumber,
                param.search
            )
        } as ResultWrapper<BaseDataWrapper<UsersInChatDataResponse>>
    }


    suspend fun muteChat(data: ChatOptionRequest): ResultWrapper<BaseDataWrapper<Any>> {
        val token = UserSessionManagement.getKeyAuthToken()

        return apiRequest {
            apis.actionMuteGroupChat(
                token!!,
                data.id,
                data.mute!!
            )
        } as ResultWrapper<BaseDataWrapper<Any>>

    }
}