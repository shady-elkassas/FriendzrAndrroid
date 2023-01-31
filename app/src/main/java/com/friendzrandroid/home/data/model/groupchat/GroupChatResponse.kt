package com.friendzrandroid.home.data.model.groupchat

data class GroupChatResponse(
    val chatGroupSubscribers: List<GroupChatSubscriber>,
    val id: String,
    val image: String,
    val listOfUserIDs: List<String>,
    val name: String,
    val registrationDateTime: String,
    val isMuted: Boolean,
    val isAdminGroup: Boolean,
    val leaveGroup: Int

)