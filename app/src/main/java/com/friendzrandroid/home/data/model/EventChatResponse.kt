package com.friendzrandroid.home.data

import com.friendzrandroid.home.data.model.MessageData

data class EventChatResponse(
    val attendees: List<Attendee>,
    val pagedModel: PagedModel
)


data class PagedModel(
    val data: List<MessageData>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalRecords: Int
)

data class Attendee(
    val image: String,
    val interests: List<Any>,
    val joinDate: String,
    val primaryId: Int,
    val stutus: Int,
    val userName: String
)

