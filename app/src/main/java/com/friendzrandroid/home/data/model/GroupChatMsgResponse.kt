package com.friendzrandroid.home.data.model

import com.friendzrandroid.home.data.UserChatResponse

data class GroupChatMsgResponse(
    val pagedModel: PagedModel,
    val subscribers: List<Subscriber>
) {
    data class PagedModel(
        val `data`: List<MessageData>,
        val pageNumber: Int,
        val pageSize: Int,
        val totalPages: Int,
        val totalRecords: Int
    ) {
        data class Data(
            val currentuserMessage: Boolean,
            val displayedUserName: Any,
            val eventChatID: Any,
            val eventData: Any,
            val eventLINKid: Any,
            val eventtype: Any,
            val eventtypecolor: Any,
            val eventtypeid: Any,
            val id: String,
            val linkable: Boolean,
            val messageAttachedVM: List<MessageAttachedVM>,
            val messages: String,
            val messagesdate: String,
            val messagestime: String,
            val messagetype: Int,
            val userId: String,
            val userMessagessId: Any,
            val userimage: String,
            val username: String
        ) {
            data class MessageAttachedVM(
                val attached: String
            )
        }
    }

    data class Subscriber(
        val chatGroupSubscriberType: Int,
        val clearChatDateTime: Any,
        val image: String,
        val isAdminGroup: Boolean,
        val isMuted: Boolean,
        val joinDate: String,
        val leaveDateTime: Any,
        val leaveGroup: Int,
        val removedDateTime: Any,
        val userId: String,
        val userName: String
    )
}