package com.friendzrandroid.home.data.model

import java.io.Serializable

data class UsersInChatDataResponse(
    val data: List<InboxData>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)


data class UsersInChatSearchResponse(
    val data: List<InboxData>
)

data class InboxData(
    val id: String,
    val image: String,
    val isevent: Boolean,
    val latestdate: String,
    val latesttime: String,
    val messagestype: Int,
    val messages: String,
    val isfrind: Boolean,
    val muit: Boolean,
    val name: String,
    val isChatGroup: Boolean,
    val message_not_Read: Int,
    val leavevent: Int,
    val leaveGroup: Int,
    val isChatGroupAdmin: Boolean,
    val isWhiteLabel: Boolean,
    val isCommunityGroup: Boolean
) : Serializable