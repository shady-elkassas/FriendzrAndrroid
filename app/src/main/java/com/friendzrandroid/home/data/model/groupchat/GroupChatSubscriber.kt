package com.friendzrandroid.home.data.model.groupchat

data class GroupChatSubscriber(
    val joinDateTime: String,
    val leaveDateTime: Any,
    val removedDateTime: Any,
    val status: Int,
    val type: Int,
    val userId: String,
    val userName: String,
    val isAdminGroup: Boolean,
    val image: String
)
