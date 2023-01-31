package com.friendzrandroid.home.data.model

import com.friendzrandroid.home.data.MessageAttachedVM

data class MessageData(
    val currentuserMessage: Boolean,
    val eventChatID: Any?,
    val id: String?,
    val messageAttachedVM: List<MessageAttachedVM>,
    val messages: String,
    val messagesdate: String,
    val messagestime: String,
    val messagetype: Int,
    val userId: String?,
    val userMessagessId: String?,
    val userimage: String,
    val username: String?,
    val myevent: Boolean?,
    val messagestype: String?,
    val eventData : EventItemData?

)