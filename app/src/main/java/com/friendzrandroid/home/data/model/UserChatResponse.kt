package com.friendzrandroid.home.data

import com.friendzrandroid.home.data.model.MessageData

data class UserChatResponse(
    val data: List<MessageData>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)


data class MessageAttachedVM(
    val attached: String
)


